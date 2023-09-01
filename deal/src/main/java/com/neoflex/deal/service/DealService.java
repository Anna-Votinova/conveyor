package com.neoflex.deal.service;

import com.neoflex.deal.dto.enums.EmailTheme;
import com.neoflex.deal.dto.response.EmailMessage;
import com.neoflex.deal.entity.Application;
import com.neoflex.deal.entity.Client;
import com.neoflex.deal.entity.Credit;
import com.neoflex.deal.entity.Employment;
import com.neoflex.deal.dto.LoanOfferDTO;
import com.neoflex.deal.dto.request.FinishRegistrationRequestDTO;
import com.neoflex.deal.dto.LoanApplicationRequestDTO;
import com.neoflex.deal.dto.request.CreditDTO;
import com.neoflex.deal.dto.response.ScoringDataDTO;
import com.neoflex.deal.entity.enums.ApplicationStatus;
import com.neoflex.deal.entity.jsonb.element.AppliedOffer;
import com.neoflex.deal.entity.mapper.ClientMapper;
import com.neoflex.deal.entity.mapper.CreditMapper;
import com.neoflex.deal.entity.mapper.EmploymentMapper;
import com.neoflex.deal.entity.mapper.OfferMapper;
import com.neoflex.deal.entity.mapper.ScoringDataMapper;
import com.neoflex.deal.exception.ApplicationNotFoundException;
import com.neoflex.deal.exception.BadRequestConveyorException;
import com.neoflex.deal.exception.ScoringConveyorException;
import com.neoflex.deal.integration.conveyor.ConveyorClient;
import com.neoflex.deal.integration.dossier.kafka.EmailMessageProducer;
import com.neoflex.deal.integration.dossier.kafka.KafkaTopicConfig;
import com.neoflex.deal.repository.ApplicationRepository;
import com.neoflex.deal.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class DealService {

    private static final String LOG_MESSAGE = "Saved application: {}";

    private final KafkaTopicConfig topicConfig;
    private final ClientRepository clientRepository;
    private final ApplicationRepository applicationRepository;
    private final ChangeStatusHistoryService changeStatusHistoryService;
    private final ConveyorClient conveyorClient;
    private final EmailMessageProducer emailMessageProducer;
    private final EmploymentMapper employmentMapper;
    private final ClientMapper clientMapper;
    private final OfferMapper offerMapper;
    private final ScoringDataMapper scoringDataMapper;
    private final CreditMapper creditMapper;

    /**
     * <p>Saves short info about the client and their application, connects to the Conveyor and receives an offers list
     * </p>
     * @param loanApplicationRequestDTO short information about the client
     * @return a list with four offers
     * @throws BadRequestConveyorException - if the LoanApplicationRequestDTO sent to the Conveyor
     * is invalid
     */
    public List<LoanOfferDTO> startRegistration(LoanApplicationRequestDTO loanApplicationRequestDTO) {

        Client client = clientMapper.toClientShort(loanApplicationRequestDTO);

        Application application = new Application();
        application.setClient(client);
        Application updatedStatusHistoryApplication = changeStatusHistoryService
                .changeStatusHistory(application, ApplicationStatus.PREAPPROVAL);
        Application savedApplication = applicationRepository.save(updatedStatusHistoryApplication);
        log.info(LOG_MESSAGE, savedApplication);

        loanApplicationRequestDTO.setId(savedApplication.getId());
        log.info("Got full request dto to prepare loan offers {}", loanApplicationRequestDTO);

        return conveyorClient.preCalculateLoan(loanApplicationRequestDTO);
    }

    /**
     * <p>Saves the loan offer chosen by the client
     * </p>
     * @param loanOfferDTO the loan offer chosen by the client
     * @throws com.neoflex.deal.exception.ApplicationNotFoundException - if the application does not exist
     */
    public void chooseOffer(LoanOfferDTO loanOfferDTO) {

        Application application = getApplication(loanOfferDTO.getApplicationId());
        AppliedOffer appliedOffer = offerMapper.toAppliedOffer(loanOfferDTO);

        application.setAppliedOffer(appliedOffer);
        Application updatedStatusHistoryApplication = changeStatusHistoryService
                .changeStatusHistory(application, ApplicationStatus.APPROVED);
        Application savedApplication = applicationRepository.save(updatedStatusHistoryApplication);
        log.info(LOG_MESSAGE, savedApplication);

        EmailMessage emailMessage = EmailMessage.builder()
                .address(savedApplication.getClient().getEmail())
                .theme(EmailTheme.FINISH_REGISTRATION)
                .applicationId(savedApplication.getId())
                .build();
        emailMessageProducer.sendEmail(topicConfig.getFinishRegistrationTopic(), emailMessage);
    }

    /**
     * <p>Saves additional information about the client, connects to the Conveyor and receives a finish loan offer,
     * saves it in the database
     * </p>
     * @param finishRegistrationRequestDTO additional information about the client
     * @param applicationId the application id saved in the database
     * @throws com.neoflex.deal.exception.ApplicationNotFoundException - if the application does not exist
     * @throws BadRequestConveyorException - if the ScoringDataDTO sent to the Conveyor is invalid
     */
    public void finishRegistration(FinishRegistrationRequestDTO finishRegistrationRequestDTO,
                                             Long applicationId) {

        Application application = getApplication(applicationId);
        ScoringDataDTO scoringDataDTO = scoringDataMapper.toScoringDataDTO(application, finishRegistrationRequestDTO);
        Employment employment = employmentMapper.toEmployment(finishRegistrationRequestDTO.getEmployment());

        Client client = application.getClient();
        Client fullClient = clientMapper.fillAdditionalClientInfo(client, finishRegistrationRequestDTO, employment);
        Client savedClient = clientRepository.save(fullClient);
        log.info("Saved client: {}", savedClient);

        try {
            CreditDTO creditDTO = conveyorClient.calculateLoan(scoringDataDTO);
            log.info("Received calculated credit with parameters: amount = {}, term = {}, monthly payment = {}, rate = {}," +
                            "psk = {}, insurance enabled = {}, salary client = {}. And payment schedule list's size = {}",
                    creditDTO.getAmount(), creditDTO.getTerm(), creditDTO.getMonthlyPayment(), creditDTO.getRate(),
                    creditDTO.getPsk(), creditDTO.getIsInsuranceEnabled(), creditDTO.getIsSalaryClient(),
                    creditDTO.getPaymentSchedule().size());

            saveCredit(creditDTO, application);

            EmailMessage emailMessage = EmailMessage.builder()
                    .address(savedClient.getEmail())
                    .theme(EmailTheme.CREATE_DOCUMENTS)
                    .applicationId(application.getId())
                    .build();
            emailMessageProducer.sendEmail(topicConfig.getCreateDocumentsTopic(), emailMessage);

        } catch (ScoringConveyorException e) {
            log.error("Got the exception from the Conveyor with the message: {}", e.getMessage());
            Application updatedStatusHistoryApplication = changeStatusHistoryService
                    .changeStatusHistory(application, ApplicationStatus.CC_DENIED);
            Application savedApplication = applicationRepository.save(updatedStatusHistoryApplication);
            log.info(LOG_MESSAGE, savedApplication);

            EmailMessage emailMessage = EmailMessage.builder()
                    .address(savedClient.getEmail())
                    .theme(EmailTheme.APPLICATION_DENIED)
                    .applicationId(application.getId())
                    .build();
            emailMessageProducer.sendEmail(topicConfig.getApplicationDeniedTopic(), emailMessage);
        }
    }

    private void saveCredit(CreditDTO creditDTO, Application application) {

        Credit credit = creditMapper.toCredit(creditDTO);

        application.setCredit(credit);
        Application updatedStatusHistoryApplication = changeStatusHistoryService
                .changeStatusHistory(application, ApplicationStatus.CC_APPROVED);
        Application savedApplication = applicationRepository.save(updatedStatusHistoryApplication);
        log.info(LOG_MESSAGE, savedApplication);
    }

    private Application getApplication(Long applicationId) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ApplicationNotFoundException("Заявка с id " + applicationId + " не существует"));
        log.info("Got the application from the repository: {}", application);
        return application;
    }
}
