package com.neoflex.deal.service;

import com.neoflex.deal.entity.Application;
import com.neoflex.deal.entity.Client;
import com.neoflex.deal.entity.Credit;
import com.neoflex.deal.entity.Employment;
import com.neoflex.deal.entity.dto.request_responce.LoanOfferDTO;
import com.neoflex.deal.entity.dto.request.FinishRegistrationRequestDTO;
import com.neoflex.deal.entity.dto.request_responce.LoanApplicationRequestDTO;
import com.neoflex.deal.entity.dto.request.CreditDTO;
import com.neoflex.deal.entity.dto.response.ScoringDataDTO;
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
import com.neoflex.deal.repository.ApplicationRepository;
import com.neoflex.deal.repository.ClientRepository;
import com.neoflex.deal.repository.CreditRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class DealService {

    private final ClientRepository clientRepository;

    private final ApplicationRepository applicationRepository;

    private final CreditRepository creditRepository;

    private final EmploymentMapper employmentMapper;

    private final ClientMapper clientMapper;

    private final OfferMapper offerMapper;

    private final ScoringDataMapper scoringDataMapper;

    private final CreditMapper creditMapper;

    /**
     *<p>Saves short info about the client and them application
     *</p>
     * @param loanApplicationRequestDTO short information about the client
     * @return loanApplicationRequestDTO with id got from the database
     */
    public LoanApplicationRequestDTO startRegistration(LoanApplicationRequestDTO loanApplicationRequestDTO) {

        Client client = clientMapper.toClientShort(loanApplicationRequestDTO);
        log.debug("Mapped client: {}", client);

        clientRepository.save(client);
        log.debug("Saved client: {}", client);

        Application application = new Application();
        application.setClient(client);
        Application updatedStatusHistoryApplication = changeStatusHistory(application, ApplicationStatus.PREAPPROVAL);

        Application savedApplication = applicationRepository.save(updatedStatusHistoryApplication);
        log.info("Application saved: {}", savedApplication);

        loanApplicationRequestDTO.setId(savedApplication.getId());

        return loanApplicationRequestDTO;

    }

    /**
     *<p>Saves the loan offer chosen by the client
     *</p>
     * @param loanOfferDTO the loan offer chosen by the client
     * @throws com.neoflex.deal.exception.ApplicationNotFoundException - if the application does not exist
     */
    public void chooseOffer(LoanOfferDTO loanOfferDTO) {

        Application application = getApplication(loanOfferDTO.getApplicationId());

        AppliedOffer appliedOffer = offerMapper.toAppliedOffer(loanOfferDTO);
        log.debug("Mapped chosen offer: {}", appliedOffer);

        application.setAppliedOffer(appliedOffer);

        Application updatedStatusHistoryApplication = changeStatusHistory(application, ApplicationStatus.APPROVED);

        Application savedApplication = applicationRepository.save(updatedStatusHistoryApplication);
        log.info("Saved application: {}", savedApplication);

    }

    /**
     *<p>Saves additional information about the client
     *</p>
     * @param finishRegistrationRequestDTO additional information about the client
     * @param applicationId the application id saved in the database
     * @return scoringDataDTO - full client information
     * @throws com.neoflex.deal.exception.ApplicationNotFoundException - if the application does not exist
     */
    public ScoringDataDTO finishRegistration(FinishRegistrationRequestDTO finishRegistrationRequestDTO,
                                             Long applicationId) {
        Application application = getApplication(applicationId);

        ScoringDataDTO scoringDataDTO = scoringDataMapper.toScoringDataDTO(application, finishRegistrationRequestDTO);
        log.debug("Mapped scoringDataDTO: {}", scoringDataDTO);

        Employment employment = employmentMapper.toEmployment(finishRegistrationRequestDTO.getEmployment());

        Client client = application.getClient();
        Client fullClient = clientMapper.toClientFull(client, finishRegistrationRequestDTO, employment);
        log.debug("Mapped client: {}", fullClient);

        Client savedClient = clientRepository.save(fullClient);
        log.info("Saved client: {}", savedClient);

        return scoringDataDTO;

    }

    /**
     *<p>Saves credit approved by the service Credit Conveyor
     *</p>
     * @param creditDTO credit approved by the conveyor
     * @param applicationId the application id saved in the database
     * @throws com.neoflex.deal.exception.ApplicationNotFoundException - if the application does not exist
     */
    public void saveCredit(CreditDTO creditDTO, Long applicationId) {

        Application application = getApplication(applicationId);

        Credit credit = creditMapper.toCredit(creditDTO);
        log.debug("Mapped credit: {}", credit);

        Credit saveCredit = creditRepository.save(credit);
        log.debug("Saved credit: {}", saveCredit);

        application.setCredit(credit);

        Application updatedStatusHistoryApplication = changeStatusHistory(application, ApplicationStatus.CC_APPROVED);

        Application savedApplication = applicationRepository.save(updatedStatusHistoryApplication);
        log.info("Saved application: {}", savedApplication);

    }

    private Application getApplication(Long applicationId) {
        Application application = applicationRepository.findById(applicationId).orElseThrow(
                () -> new ApplicationNotFoundException("Заявка с id " + applicationId + " не существует"));
        log.info("Got the application from the repository: {}", application);

        return application;
    }

    private Application changeStatusHistory(Application application, ApplicationStatus applicationStatus) {

        application.setStatus(applicationStatus);

        StatusHistory statusHistory = new StatusHistory();
        statusHistory.setStatus(applicationStatus);
        statusHistory.setChangeType(ChangeType.AUTOMATIC);
        log.debug("Create statusHistory: {}", statusHistory);

        application.getStatusHistory().add(statusHistory);
        log.info("History updated: {}", application.getStatusHistory());

        return application;
    }

}
