package com.neoflex.dossier.kafka;

import com.neoflex.dossier.dto.EmailMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailMessageListener {

    @KafkaListener(
            topics = "finish-registration",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "emailKafkaListenerContainerFactory")
    public void listenTopicFinishRegistration(EmailMessage emailMessage) {
        log.info("Received email message - {} - in the topic finish-registration", emailMessage);
    }

    @KafkaListener(
            topics = "create-documents",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "emailKafkaListenerContainerFactory")
    public void listenTopicCreateDocuments(EmailMessage emailMessage) {
        log.info("Received email message - {} - in the topic create-documents", emailMessage);
    }
}
