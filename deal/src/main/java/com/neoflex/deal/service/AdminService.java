package com.neoflex.deal.service;

import com.neoflex.deal.dto.response.ApplicationDTO;
import com.neoflex.deal.dto.response.element.ApplicationStatusHistoryDTO;
import com.neoflex.deal.dto.response.element.AppliedOfferInfo;
import com.neoflex.deal.dto.response.element.ClientInfo;
import com.neoflex.deal.dto.response.element.CreditInfo;
import com.neoflex.deal.dto.response.element.EmploymentInfo;
import com.neoflex.deal.dto.response.element.PassportInfo;
import com.neoflex.deal.dto.response.element.PaymentScheduleResponseElement;
import com.neoflex.deal.entity.Application;
import com.neoflex.deal.entity.enums.ApplicationStatus;
import com.neoflex.deal.entity.mapper.ApplicationMapper;
import com.neoflex.deal.entity.mapper.ClientMapper;
import com.neoflex.deal.entity.mapper.CreditMapper;
import com.neoflex.deal.entity.mapper.OfferMapper;
import com.neoflex.deal.entity.mapper.elementmapper.EmploymentMapper;
import com.neoflex.deal.entity.mapper.elementmapper.PassportMapper;
import com.neoflex.deal.entity.mapper.elementmapper.PaymentScheduleMapper;
import com.neoflex.deal.entity.mapper.elementmapper.StatusHistoryMapper;
import com.neoflex.deal.exception.ApplicationNotFoundException;
import com.neoflex.deal.repository.ApplicationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminService {

    private final ApplicationRepository applicationRepository;
    private final ChangeStatusHistoryService changeStatusHistoryService;
    private final PassportMapper passportMapper;
    private final EmploymentMapper employmentMapper;
    private final ClientMapper clientMapper;
    private final PaymentScheduleMapper paymentScheduleMapper;
    private final CreditMapper creditMapper;
    private final OfferMapper offerMapper;
    private final StatusHistoryMapper statusHistoryMapper;
    private final ApplicationMapper applicationMapper;

    /**
     * <p>Updates the application status on Document Created
     * </p>
     * @param applicationId the application id saved in the database
     * @throws com.neoflex.deal.exception.ApplicationNotFoundException - if the application does not exist
     */
    @Transactional
    public void changeApplicationStatus(Long applicationId, ApplicationStatus status) {
        Application application = getApplication(applicationId);
        Application updatedStatusHistoryApplication = changeStatusHistoryService
                .changeStatusHistory(application, status);
        Application savedApplication = applicationRepository.save(updatedStatusHistoryApplication);
        log.info("Saved application: {}", savedApplication);
    }

    /**
     * <p>Finds the required application in the database by id and returns it
     * </p>
     * @param applicationId the application id saved in the database
     * @return the required application
     * @throws com.neoflex.deal.exception.ApplicationNotFoundException - if the application does not exist
     */
    @Transactional(readOnly = true)
    public ApplicationDTO getApplicationById(Long applicationId) {
        Application application = getApplication(applicationId);

        ApplicationDTO applicationDTO = buildApplicationDTO(application);
        log.info("Returned application: {}", applicationDTO);

        return applicationDTO;
    }

    /**
     * <p>Finds all applications saved in the database and returns a list of them or an empty list if no application
     * exists
     * </p>
     * @return a list of applications
     */
    @Transactional(readOnly = true)
    public List<ApplicationDTO> getAllApplications() {
        List<Application> allApplications = applicationRepository.findAll();

        if (allApplications.isEmpty()) {
            log.info("Returned an empty applications list, because it equals to: {}", allApplications);
            return Collections.emptyList();
        }

        List<ApplicationDTO> applicationDTOs = new ArrayList<>();

        allApplications.forEach(a -> {
            ApplicationDTO applicationDTO = buildApplicationDTO(a);
            applicationDTOs.add(applicationDTO);
        });

        log.info("Returned an applications list with {} elements", applicationDTOs.size());
        return applicationDTOs;
    }

    private Application getApplication(Long applicationId) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ApplicationNotFoundException("Заявка с id " + applicationId + " не существует"));
        log.info("Got the application from the repository: {}", application);
        return application;
    }

    private ApplicationDTO buildApplicationDTO(Application application) {
        PassportInfo passportInfo = passportMapper.toPassportInfo(application.getClient());
        EmploymentInfo employmentInfo = employmentMapper.toEmploymentInfo(application.getClient());
        ClientInfo clientInfo = clientMapper.toClientInfo(application.getClient(), passportInfo, employmentInfo);

        List<PaymentScheduleResponseElement> paymentSchedule = new ArrayList<>();

        if (Objects.nonNull(application.getCredit()) && Objects.nonNull(application.getCredit().getPaymentSchedule())) {
            paymentSchedule = application.getCredit().getPaymentSchedule().stream()
                                        .map(paymentScheduleMapper::toPaymentScheduleResponseElement)
                                        .toList();
        }
        CreditInfo creditInfo = creditMapper.toCreditInfo(application.getCredit(), paymentSchedule);

        AppliedOfferInfo appliedOfferInfo = offerMapper.toAppliedOfferInfo(application.getAppliedOffer());

        List<ApplicationStatusHistoryDTO> statusHistory =
                application.getStatusHistory().stream()
                           .map(statusHistoryMapper::toStatusHistoryDTO)
                           .toList();

        return applicationMapper.toApplicationDTO(application, clientInfo, creditInfo, appliedOfferInfo, statusHistory);
    }
}
