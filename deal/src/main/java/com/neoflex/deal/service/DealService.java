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
import com.neoflex.deal.entity.enums.ChangeType;
import com.neoflex.deal.entity.jsonb.element.AppliedOffer;
import com.neoflex.deal.entity.jsonb.element.StatusHistory;
import com.neoflex.deal.entity.mapper.ClientMapper;
import com.neoflex.deal.entity.mapper.CreditMapper;
import com.neoflex.deal.entity.mapper.EmploymentMapper;
import com.neoflex.deal.entity.mapper.OfferMapper;
import com.neoflex.deal.entity.mapper.ScoringDataMapper;
import com.neoflex.deal.exception.ApplicationNotFoundException;
import com.neoflex.deal.integration.conveyor.ConveyorClient;
import com.neoflex.deal.integration.dossier.kafka.EmailMessageProducer;
import com.neoflex.deal.repository.ApplicationRepository;
import com.neoflex.deal.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class DealService {

    private final ClientRepository clientRepository;
    private final ApplicationRepository applicationRepository;
    private final ConveyorClient conveyorClient;
    private final EmploymentMapper employmentMapper;
    private final ClientMapper clientMapper;
    private final OfferMapper offerMapper;
    private final ScoringDataMapper scoringDataMapper;
    private final CreditMapper creditMapper;
    private final EmailMessageProducer emailMessageProducer;

    /**
     * <p>Saves short info about the client and their application, connects to the Conveyor and receives an offers list
     * </p>
     * @param loanApplicationRequestDTO short information about the client
     * @return a list with four offers
     * @throws com.neoflex.deal.exception.BadRequestException - if the LoanApplicationRequestDTO sent to the Conveyor
     * is invalid
     */
    public List<LoanOfferDTO> startRegistration(LoanApplicationRequestDTO loanApplicationRequestDTO) {

        Client client = clientMapper.toClientShort(loanApplicationRequestDTO);

        Application application = new Application();
        application.setClient(client);
        Application updatedStatusHistoryApplication = changeStatusHistory(application, ApplicationStatus.PREAPPROVAL);
        Application savedApplication = applicationRepository.save(updatedStatusHistoryApplication);
        log.info("Application saved: {}", savedApplication);

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
        Application updatedStatusHistoryApplication = changeStatusHistory(application, ApplicationStatus.APPROVED);
        Application savedApplication = applicationRepository.save(updatedStatusHistoryApplication);
        log.info("Saved application: {}", savedApplication);

        EmailMessage emailMessage = new EmailMessage(
                savedApplication.getClient().getEmail(), EmailTheme.FINISH_REGISTRATION, savedApplication.getId());
        emailMessageProducer.sendEmail(EmailTheme.FINISH_REGISTRATION.getValue(), emailMessage);
    }

    /**
     * <p>Saves additional information about the client, connects to the Conveyor and receives a finish loan offer,
     * saves it in the database
     * </p>
     * @param finishRegistrationRequestDTO additional information about the client
     * @param applicationId the application id saved in the database
     * @throws com.neoflex.deal.exception.ApplicationNotFoundException - if the application does not exist
     * @throws com.neoflex.deal.exception.BadRequestException - if the ScoringDataDTO sent to the Conveyor is invalid
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

        CreditDTO creditDTO = conveyorClient.calculateLoan(scoringDataDTO);
        log.info("Received calculated credit with parameters: amount = {}, term = {}, monthly payment = {}, rate = {}," +
                        "psk = {}, insurance enabled = {}, salary client = {}. And payment schedule list's size = {}",
                creditDTO.getAmount(), creditDTO.getTerm(), creditDTO.getMonthlyPayment(), creditDTO.getRate(),
                creditDTO.getPsk(), creditDTO.getIsInsuranceEnabled(), creditDTO.getIsSalaryClient(),
                creditDTO.getPaymentSchedule().size());

        saveCredit(creditDTO, application);

        EmailMessage emailMessage = new EmailMessage(
                savedClient.getEmail(), EmailTheme.CREATE_DOCUMENTS, application.getId());
        emailMessageProducer.sendEmail(EmailTheme.CREATE_DOCUMENTS.getValue(), emailMessage);
    }

    private void saveCredit(CreditDTO creditDTO, Application application) {

        Credit credit = creditMapper.toCredit(creditDTO);

        application.setCredit(credit);
        Application updatedStatusHistoryApplication = changeStatusHistory(application, ApplicationStatus.CC_APPROVED);
        Application savedApplication = applicationRepository.save(updatedStatusHistoryApplication);
        log.info("Saved application: {}", savedApplication);
    }

    private Application getApplication(Long applicationId) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ApplicationNotFoundException("Заявка с id " + applicationId + " не существует"));
        log.info("Got the application from the repository: {}", application);

        return application;
    }

    private Application changeStatusHistory(Application application, ApplicationStatus applicationStatus) {

        application.setStatus(applicationStatus);

        StatusHistory statusHistory = new StatusHistory();
        statusHistory.setId(UUID.randomUUID());
        statusHistory.setStatus(applicationStatus);
        statusHistory.setChangeType(ChangeType.AUTOMATIC);

        application.getStatusHistory().add(statusHistory);
        log.info("History updated: {}", application.getStatusHistory());

        return application;
    }
}
