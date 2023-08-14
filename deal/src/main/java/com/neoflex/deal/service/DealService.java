package com.neoflex.deal.service;

import com.neoflex.deal.entity.Application;
import com.neoflex.deal.entity.Client;
import com.neoflex.deal.entity.Credit;
import com.neoflex.deal.entity.Employment;
import com.neoflex.deal.entity.dto.LoanOfferDTO;
import com.neoflex.deal.entity.dto.request.FinishRegistrationRequestDTO;
import com.neoflex.deal.entity.dto.request.LoanApplicationRequestDTO;
import com.neoflex.deal.entity.dto.response.CreditDTO;
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

    public LoanApplicationRequestDTO startRegistration(LoanApplicationRequestDTO requestDTO) {

        Client client = clientMapper.toClientShort(requestDTO);
        log.debug("Mapped client: {}", client);

        clientRepository.save(client);
        log.debug("Saved client: {}", client);

        Application application = new Application();
        application.setClient(client);
        Application updatedStatusHistoryApplication = changeStatusHistory(application, ApplicationStatus.PREAPPROVAL);

        applicationRepository.save(updatedStatusHistoryApplication);
        log.info("Application saved: {}", updatedStatusHistoryApplication);

        requestDTO.setId(application.getId());

        return requestDTO;

    }

    public void chooseOffer(LoanOfferDTO requestDTO) {

        Application application = checkApplication(requestDTO.getApplicationId());

        AppliedOffer appliedOffer = offerMapper.toAppliedOffer(requestDTO);
        log.debug("Mapped chosen offer: {}", appliedOffer);

        application.setAppliedOffer(appliedOffer);

        Application updatedStatusHistoryApplication = changeStatusHistory(application, ApplicationStatus.APPROVED);

        applicationRepository.save(updatedStatusHistoryApplication);
        log.info("Saved application: {}", updatedStatusHistoryApplication);

    }

    public ScoringDataDTO finishRegistration(FinishRegistrationRequestDTO requestDTO,
                                             Long applicationId) {
        Application application = checkApplication(applicationId);

        ScoringDataDTO scoringDataDTO = scoringDataMapper.toScoringDataDTO(application, requestDTO);
        log.debug("Mapped scoringDataDTO: {}", scoringDataDTO);

        Employment employment = employmentMapper.toEmployment(requestDTO.getEmployment());

        Client client = application.getClient();
        Client fullClient = clientMapper.toClientFull(client, requestDTO, employment);
        log.debug("Mapped client: {}", fullClient);

        clientRepository.save(fullClient);
        log.info("Saved client: {}", fullClient);

        return scoringDataDTO;

    }

    public void saveCredit(CreditDTO creditDTO, Long applicationId) {

        Application application = checkApplication(applicationId);

        Credit credit = creditMapper.toCredit(creditDTO);
        log.debug("Mapped credit: {}", credit);

        creditRepository.save(credit);
        log.debug("Saved credit: {}", credit);

        application.setCredit(credit);

        Application updatedStatusHistoryApplication = changeStatusHistory(application, ApplicationStatus.CC_APPROVED);

        applicationRepository.save(updatedStatusHistoryApplication);
        log.info("Saved application: {}", updatedStatusHistoryApplication);

    }

    private Application checkApplication(Long applicationId) {
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
