package com.neoflex.deal.integration.dossier.kafka;

import com.neoflex.deal.dto.response.EmailMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@Slf4j
public class EmailMessageProducer {

    private final KafkaTemplate<String, EmailMessage> emailMessageKafkaTemplate;

    public void sendEmail(String topicName, EmailMessage emailMessage) {
        log.info("Got the request for sending Email Message to the Dossier. Topic name = {}, email message = {}",
                topicName, emailMessage);
        emailMessageKafkaTemplate.send(topicName, emailMessage);
    }

}
