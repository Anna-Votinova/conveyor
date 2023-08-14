package com.neoflex.deal.service;

import com.neoflex.deal.entity.Application;
import com.neoflex.deal.entity.Client;
import com.neoflex.deal.entity.dto.ApplicationStatusHistoryDTO;
import com.neoflex.deal.entity.dto.LoanOfferDTO;
import com.neoflex.deal.entity.dto.request.FinishRegistrationRequestDTO;
import com.neoflex.deal.entity.dto.request.LoanApplicationRequestDTO;
import com.neoflex.deal.entity.dto.response.ScoringDataDTO;
import com.neoflex.deal.entity.enums.ApplicationStatus;
import com.neoflex.deal.entity.enums.ChangeType;
import com.neoflex.deal.entity.jsonb.element.AppliedOffer;
import com.neoflex.deal.entity.jsonb.element.StatusHistory;
import com.neoflex.deal.entity.mapper.ClientMapper;
import com.neoflex.deal.entity.mapper.OfferMapper;
import com.neoflex.deal.entity.mapper.ScoringDataMapper;
import com.neoflex.deal.entity.mapper.StatusHistoryMapper;
import com.neoflex.deal.exception.ApplicationNotFoundException;
import com.neoflex.deal.repository.ApplicationRepository;
import com.neoflex.deal.repository.ClientRepository;
import com.neoflex.deal.repository.StatusHistoryRepository;
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

    private final ClientRepository clientRepository;

    private final ApplicationRepository applicationRepository;

    private final StatusHistoryRepository statusHistoryRepository;

    private final ClientMapper clientMapper;

    private final OfferMapper offerMapper;

    private final StatusHistoryMapper historyMapper;

    private final ScoringDataMapper scoringDataMapper;

    public LoanApplicationRequestDTO startRegistration(LoanApplicationRequestDTO requestDTO) {

        Client client = clientMapper.toClientShort(requestDTO);
        log.debug("Mapped client: {}", client);

        client = clientRepository.save(client);
        log.debug("Saved client: {}", client);

        Application application = new Application();
        application.setClient(client);
        log.debug("Application: {}", application);

        application = applicationRepository.save(application);
        log.debug("Saved application: {}", application);

        requestDTO.setId(application.getId());

        return requestDTO;

    }

    public void chooseOffer(LoanOfferDTO requestDTO) {

        Application application = applicationRepository.findById(requestDTO.getApplicationId()).orElseThrow(
                () -> new ApplicationNotFoundException("Заявка с id " + requestDTO.getApplicationId() + " не существует"));
        log.info("Got the application from the repository: {}", application);

        AppliedOffer appliedOffer = offerMapper.toAppliedOffer(requestDTO);
        log.debug("Mapped chosen offer: {}", appliedOffer);

        application.setStatus(ApplicationStatus.PREAPPROVAL);

        ApplicationStatusHistoryDTO historyDTO = ApplicationStatusHistoryDTO.builder()
                .status(ApplicationStatus.PREAPPROVAL)
                .changeType(ChangeType.AUTOMATIC).build();

        StatusHistory statusHistory = historyMapper.toStatusHistory(historyDTO);
        log.debug("Mapped statusHistory: {}", statusHistory);

        statusHistory = statusHistoryRepository.save(statusHistory);
        log.debug("Saved statusHistory: {}", statusHistory);

        List<StatusHistory> historyList = List.of(statusHistory);

        application.setStatusHistory(historyList);
        application.setAppliedOffer(appliedOffer);

        application = applicationRepository.save(application);
        log.info("Saved application: {}", application);

    }

    public ScoringDataDTO finishRegistration(FinishRegistrationRequestDTO requestDTO,
                                             Long applicationId) {
        Application application = applicationRepository.findById(applicationId).orElseThrow(
                () -> new ApplicationNotFoundException("Заявка с id " + applicationId + " не существует"));
        log.info("Got the application from the repository: {}", application);

        ScoringDataDTO scoringDataDTO = scoringDataMapper.toScoringDataDTO(application, requestDTO);
        log.debug("Mapped scoringDataDTO: {}", scoringDataDTO);

        Client client = application.getClient();

        client = clientMapper.toClientFull(client, requestDTO);
        log.debug("Mapped client: {}", client);

        client = clientRepository.save(client);
        log.debug("Saved client: {}", client);

        return scoringDataDTO;

    }

}
