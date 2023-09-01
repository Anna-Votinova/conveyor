package com.neoflex.deal.integration.dossier.kafka;

import lombok.Getter;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.HashMap;
import java.util.Map;

@Configuration
@Getter
public class KafkaTopicConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapAddress;
    @Value("${topic.name.finish-registration}")
    private String finishRegistrationTopic;
    @Value("${topic.name.create-documents}")
    private String createDocumentsTopic;
    @Value("${topic.name.send-documents}")
    private String sendDocumentsTopic;
    @Value("${topic.name.send-ses}")
    private String sendSesCodeTopic;
    @Value("${topic.name.credit-issued}")
    private String creditIssuedTopic;
    @Value("${topic.name.application-denied}")
    private String applicationDeniedTopic;

    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        return new KafkaAdmin(configs);
    }

    @Bean
    public NewTopic finishRegistrationTopic() {
        return new NewTopic(finishRegistrationTopic, 1, (short) 1);
    }

    @Bean
    public NewTopic createDocumentTopic() {
        return new NewTopic(createDocumentsTopic, 1, (short) 1);
    }

    @Bean
    public NewTopic sendDocumentTopic() {
        return new NewTopic(sendDocumentsTopic, 1, (short) 1);
    }

    @Bean
    public NewTopic sendSesTopic() {
        return new NewTopic(sendSesCodeTopic, 1, (short) 1);
    }

    @Bean
    public NewTopic creditIssuedTopic() {
        return new NewTopic(creditIssuedTopic, 1, (short) 1);
    }

    @Bean
    public NewTopic applicationDeniedTopic() {
        return new NewTopic(applicationDeniedTopic, 1, (short) 1);
    }
}
