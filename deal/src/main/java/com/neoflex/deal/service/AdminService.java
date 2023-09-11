package com.neoflex.deal.service;

import com.neoflex.deal.entity.Application;
import com.neoflex.deal.entity.enums.ApplicationStatus;
import com.neoflex.deal.exception.ApplicationNotFoundException;
import com.neoflex.deal.repository.ApplicationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AdminService {

    private final ApplicationRepository applicationRepository;
    private final ChangeStatusHistoryService changeStatusHistoryService;

    /**
     * <p>Updates the application status on Document Created
     * </p>
     * @param applicationId the application id saved in the database
     * @throws com.neoflex.deal.exception.ApplicationNotFoundException - if the application does not exist
     */
    public void changeApplicationStatus(Long applicationId, ApplicationStatus status) {
        Application application = getApplication(applicationId);
        Application updatedStatusHistoryApplication = changeStatusHistoryService
                .changeStatusHistory(application, status);
        Application savedApplication = applicationRepository.save(updatedStatusHistoryApplication);
        log.info("Saved application: {}", savedApplication);
    }

    private Application getApplication(Long applicationId) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ApplicationNotFoundException("Заявка с id " + applicationId + " не существует"));
        log.info("Got the application from the repository: {}", application);
        return application;
    }
}
