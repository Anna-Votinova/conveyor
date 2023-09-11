package com.neoflex.deal.service;

import com.neoflex.deal.dto.enums.EmailTheme;
import com.neoflex.deal.dto.response.DocumentDTO;
import com.neoflex.deal.dto.response.EmailMessage;
import com.neoflex.deal.entity.Application;
import com.neoflex.deal.entity.enums.ApplicationStatus;
import com.neoflex.deal.entity.enums.CreditStatus;
import com.neoflex.deal.entity.mapper.DocumentMapper;
import com.neoflex.deal.exception.ApplicationNotFoundException;
import com.neoflex.deal.exception.InvalidSesCodeException;
import com.neoflex.deal.integration.dossier.kafka.DossierKafkaConfig;
import com.neoflex.deal.integration.dossier.kafka.EmailMessageProducer;
import com.neoflex.deal.repository.ApplicationRepository;
import com.neoflex.deal.service.utils.SesCodeGeneratorUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class DocumentService {

    private static final String SAVED_APPLICATION_LOG_MESSAGE = "Saved application: {}";

    private final DossierKafkaConfig dossierKafkaConfig;
    private final ApplicationRepository applicationRepository;
    private final ChangeStatusHistoryService changeStatusHistoryService;
    private final EmailMessageProducer emailMessageProducer;
    private final DocumentMapper documentMapper;

    /**
     * <p>Updates the application status, sends to the Dossier info for preparing the documentation via Kafka
     * </p>
     * @param applicationId the application id saved in the database
     * @throws com.neoflex.deal.exception.ApplicationNotFoundException - if the application does not exist
     */
    public void sendDocument(Long applicationId) {
        Application application = getApplication(applicationId);
        Application updatedStatusHistoryApplication = changeStatusHistoryService
                .changeStatusHistory(application, ApplicationStatus.PREPARE_DOCUMENTS);
        Application savedApplication = applicationRepository.save(updatedStatusHistoryApplication);
        log.info(SAVED_APPLICATION_LOG_MESSAGE, savedApplication);

        DocumentDTO document = documentMapper.toDocumentDto(savedApplication);

        EmailMessage emailMessage = EmailMessage.builder()
                .address(savedApplication.getClient().getEmail())
                .theme(EmailTheme.SEND_DOCUMENTS)
                .applicationId(savedApplication.getId())
                .document(document)
                .build();
        emailMessageProducer.sendEmailMessage(dossierKafkaConfig.getSendDocumentsTopic(), emailMessage);
    }

    /**
     * <p>Generates the ses code, updates the application, sends email message with the ses code to the Dossier via Kafka
     * </p>
     * @param applicationId the application id saved in the database
     * @throws com.neoflex.deal.exception.ApplicationNotFoundException - if the application does not exist
     */
    public void signDocument(Long applicationId) {
        Application application = getApplication(applicationId);
        Integer sesCode = SesCodeGeneratorUtil.generateSesCode();

        application.setSesCode(sesCode);
        Application updatedApplication = applicationRepository.save(application);
        log.info(SAVED_APPLICATION_LOG_MESSAGE, updatedApplication);

        EmailMessage emailMessage = EmailMessage.builder()
                .address(updatedApplication.getClient().getEmail())
                .theme(EmailTheme.SEND_SES)
                .applicationId(updatedApplication.getId())
                .sesCode(sesCode)
                .build();
        emailMessageProducer.sendEmailMessage(dossierKafkaConfig.getSendSesCodeTopic(), emailMessage);
    }

    /**
     * <p>Verifies ses code, updates the application and credit statuses, sends the message "Credit issued"
     * to the Dossier via Kafka
     * </p>
     * @param applicationId the application id saved in the database
     * @param sesCode the ses-code saved in the database
     * @throws com.neoflex.deal.exception.ApplicationNotFoundException - if the application does not exist
     * @throws com.neoflex.deal.exception.InvalidSesCodeException - if the ses code invalid
     */
    public void issueCredit(Long applicationId, Integer sesCode) {
        Application application = getApplication(applicationId);

        if (!application.getSesCode().equals(sesCode)) {
            throw new InvalidSesCodeException("Введенная электронная подпись невалидна");
        }

        application.setSignDate(LocalDateTime.now());
        Application applicationDocumentSigned = changeStatusHistoryService
                .changeStatusHistory(application, ApplicationStatus.DOCUMENT_SIGNED);
        Application savedApplicationDocumentSigned = applicationRepository.save(applicationDocumentSigned);
        log.info(SAVED_APPLICATION_LOG_MESSAGE, savedApplicationDocumentSigned);

        savedApplicationDocumentSigned.getCredit().setCreditStatus(CreditStatus.ISSUED);
        Application applicationCreditIssued = changeStatusHistoryService
                .changeStatusHistory(savedApplicationDocumentSigned, ApplicationStatus.CREDIT_ISSUED);
        Application savedApplicationCreditIssued = applicationRepository.save(applicationCreditIssued);
        log.info(SAVED_APPLICATION_LOG_MESSAGE, savedApplicationCreditIssued);

        EmailMessage emailMessage = EmailMessage.builder()
                .address(savedApplicationCreditIssued.getClient().getEmail())
                .theme(EmailTheme.CREDIT_ISSUED)
                .applicationId(savedApplicationCreditIssued.getId())
                .build();
        emailMessageProducer.sendEmailMessage(dossierKafkaConfig.getCreditIssuedTopic(), emailMessage);
    }

    private Application getApplication(Long applicationId) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ApplicationNotFoundException("Заявка с id " + applicationId + " не существует"));
        log.info("Got the application from the repository: {}", application);
        return application;
    }
}
