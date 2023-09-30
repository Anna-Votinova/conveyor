package com.neoflex.deal.service;

import com.neoflex.deal.entity.Application;
import com.neoflex.deal.entity.enums.ApplicationStatus;
import com.neoflex.deal.entity.enums.ChangeType;
import com.neoflex.deal.entity.jsonb.element.StatusHistory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
public class ChangeStatusHistoryService {

    /**
     * <p>Changes the application status, updates the status history
     * </p>
     * @param application the application whose status should be changed
     * @param applicationStatus the application status that should be set to the application
     * @return the application with a new status and an updated status history
     */
    public Application changeStatusHistory(Application application, ApplicationStatus applicationStatus) {

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
