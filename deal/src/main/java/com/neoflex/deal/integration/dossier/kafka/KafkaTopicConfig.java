package com.neoflex.deal.integration.dossier.kafka;

import com.neoflex.deal.dto.enums.EmailTheme;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaTopicConfig {

    @Value(value = "${spring.kafka.bootstrap-servers}")
    private String bootstrapAddress;

    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        return new KafkaAdmin(configs);
    }

    @Bean
    public NewTopic finishRegistrationTopic() {
        return new NewTopic(EmailTheme.FINISH_REGISTRATION.getValue(), 1, (short) 1);
    }

    @Bean
    public NewTopic createDocumentTopic() {
        return new NewTopic(EmailTheme.CREATE_DOCUMENTS.getValue(), 1, (short) 1);
    }

    @Bean
    public NewTopic sendDocumentTopic() {
        return new NewTopic(EmailTheme.SEND_DOCUMENTS.getValue(), 1, (short) 1);
    }

    @Bean
    public NewTopic sendSesTopic() {
        return new NewTopic(EmailTheme.SEND_SES.getValue(), 1, (short) 1);
    }

    @Bean
    public NewTopic creditIssuedTopic() {
        return new NewTopic(EmailTheme.CREDIT_ISSUED.getValue(), 1, (short) 1);
    }

    @Bean
    public NewTopic applicationDeniedTopic() {
        return new NewTopic(EmailTheme.APPLICATION_DENIED.getValue(), 1, (short) 1);
    }
}
