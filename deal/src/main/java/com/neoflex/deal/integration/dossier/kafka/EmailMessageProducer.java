package com.neoflex.deal.integration.dossier.kafka;

import com.neoflex.deal.dto.response.EmailMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;


@Service
@RequiredArgsConstructor
@Slf4j
public class EmailMessageProducer {

    private final KafkaTemplate<String, EmailMessage> emailMessageKafkaTemplate;

    public void sendEmailMessage(String topicName, EmailMessage emailMessage) {
        log.info("Got the request for sending Email Message to the Dossier. Topic name = {}, email message = {}",
                topicName, emailMessage);
        try {
            CompletableFuture<SendResult<String, EmailMessage>> future = emailMessageKafkaTemplate
                    .send(topicName, emailMessage)
                    .completable();
            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    log.info("Email message with application id {} was sent, offset: {}",
                            emailMessage.getApplicationId(), result.getRecordMetadata().offset());
                } else {
                    log.error("Unable to send email message with application id {} due to: {} ",
                            emailMessage.getApplicationId(), ex.getMessage());
                }
            });
        } catch (Exception exception) {
            log.error("Error with sending email with application id {} happened. The reason is:",
                    emailMessage.getApplicationId(), exception);
        }
    }
}
