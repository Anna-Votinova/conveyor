package com.neoflex.dossier.kafka;

import com.neoflex.dossier.dto.EmailMessage;
import com.neoflex.dossier.dto.enums.ApplicationStatus;
import com.neoflex.dossier.integration.deal.DealClient;
import com.neoflex.dossier.mail.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailMessageListener {

    private static final String FINISH_REGISTRATION_TEXT = "Ваша заявка сохранена. Пожалуйста, завершите регистрацию " +
            "в Кредитном конвейере.";
    private static final String CREATE_DOCUMENTS_TEXT = "Регистрация успешно пройдена. " +
            "Запросите документы для оформления кредита.";
    private static final String SEND_SES_TEXT = "Ваш секретный код для подписания документов: %d. Во избежание " +
            "мошенничеств никому не сообщайте его.";
    private static final String CREDIT_ISSUED_TEXT = "Поздравляем! Вам выдан кредит.";
    private static final String APPLICATION_DENIED_TEXT = "К сожалению, вам отказано в выдаче кредита.";
    private static final String SENDING_EMAIL_SUCCESS_LOG_MESSAGE = "Email with application id {} was sent";
    private static final String SENDING_EMAIL_ERROR_LOG_MESSAGE = "Error with sending email with application id {} " +
            "has happened. The reason is: ";

    private final EmailService emailService;
    private final DealClient dealClient;

    @KafkaListener(
            topics = "finish-registration",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "emailKafkaListenerContainerFactory")
    public void listenTopicFinishRegistration(@Payload EmailMessage emailMessage) {
        log.info("Received email message - {} - in the topic finish-registration", emailMessage);
        try {
            emailService.sendSimpleMessage(
                    emailMessage.address(), emailMessage.theme().getValue(), FINISH_REGISTRATION_TEXT);
            log.info(SENDING_EMAIL_SUCCESS_LOG_MESSAGE, emailMessage.applicationId());
        } catch (Exception e) {
            log.error(SENDING_EMAIL_ERROR_LOG_MESSAGE, emailMessage.applicationId(), e);
        }
    }

    @KafkaListener(
            topics = "create-documents",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "emailKafkaListenerContainerFactory")
    public void listenTopicCreateDocuments(@Payload EmailMessage emailMessage) {
        log.info("Received email message - {} - in the topic create-documents", emailMessage);
        try {
            emailService.sendSimpleMessage(
                    emailMessage.address(), emailMessage.theme().getValue(), CREATE_DOCUMENTS_TEXT);
            log.info(SENDING_EMAIL_SUCCESS_LOG_MESSAGE, emailMessage.applicationId());
        } catch (Exception e) {
            log.error(SENDING_EMAIL_ERROR_LOG_MESSAGE, emailMessage.applicationId(), e);
        }
    }

    @KafkaListener(
            topics = "send-documents",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "emailKafkaListenerContainerFactory")
    public void listenTopicSendDocuments(@Payload EmailMessage emailMessage) {
        log.info("Received email message - {} - in the topic send-documents", emailMessage);
        try {
            emailService.sendComplexMessage(
                    emailMessage.address(), emailMessage.theme().getValue(), emailMessage.document());
            log.info(SENDING_EMAIL_SUCCESS_LOG_MESSAGE, emailMessage.applicationId());
            changeApplicationStatus(emailMessage.applicationId());
        } catch (Exception e) {
            log.error(SENDING_EMAIL_ERROR_LOG_MESSAGE, emailMessage.applicationId(), e);
        }
    }

    @KafkaListener(
            topics = "send-ses",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "emailKafkaListenerContainerFactory")
    public void listenSendSes(@Payload EmailMessage emailMessage) {
        log.info("Received email message - {} - in the topic send-ses", emailMessage);
        try {
            emailService.sendSimpleMessage(emailMessage.address(), emailMessage.theme().getValue(),
                    String.format(SEND_SES_TEXT, emailMessage.sesCode()));
            log.info(SENDING_EMAIL_SUCCESS_LOG_MESSAGE, emailMessage.applicationId());
        } catch (Exception e) {
            log.error(SENDING_EMAIL_ERROR_LOG_MESSAGE, emailMessage.applicationId(), e);
        }        
    }

    @KafkaListener(
            topics = "credit-issued",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "emailKafkaListenerContainerFactory")
    public void listenCreditIssued(@Payload EmailMessage emailMessage) {
        log.info("Received email message - {} - in the topic credit-issued", emailMessage);
        try {
            emailService.sendSimpleMessage(emailMessage.address(), emailMessage.theme().getValue(), CREDIT_ISSUED_TEXT);
            log.info(SENDING_EMAIL_SUCCESS_LOG_MESSAGE, emailMessage.applicationId());
        } catch (Exception e) {
            log.error(SENDING_EMAIL_ERROR_LOG_MESSAGE, emailMessage.applicationId(), e);
        }
    }

    @KafkaListener(
            topics = "application-denied",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "emailKafkaListenerContainerFactory")
    public void listenApplicationDenied(@Payload EmailMessage emailMessage) {
        log.info("Received email message - {} - in the topic application-denied", emailMessage);
        try {
            emailService.sendSimpleMessage(
                    emailMessage.address(), emailMessage.theme().getValue(), APPLICATION_DENIED_TEXT);
            log.info(SENDING_EMAIL_SUCCESS_LOG_MESSAGE, emailMessage.applicationId());
        } catch (Exception e) {
            log.error(SENDING_EMAIL_ERROR_LOG_MESSAGE, emailMessage.applicationId(), e);
        }
    }

    private void changeApplicationStatus(Long applicationId) {
        log.info("Got the request for sending application id {} to the Deal", applicationId);
        try {
            dealClient.changeApplicationStatus(applicationId, ApplicationStatus.DOCUMENT_CREATED);
            log.info("Application id {} was sent to the Deal", applicationId);
        } catch (Exception e) {
            log.error("Error with sending application id {} has happened. The reason is: ", applicationId, e);
        }
    }
}
