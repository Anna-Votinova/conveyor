package com.neoflex.deal.integration.dossier.kafka;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@Validated
@ConfigurationProperties(prefix = "dossier.kafka")
public class DossierKafkaConfig {

    @NotBlank
    private String bootstrapAddress;
    @NotBlank
    private String finishRegistrationTopic;
    @NotBlank
    private String createDocumentsTopic;
    @NotBlank
    private String sendDocumentsTopic;
    @NotBlank
    private String sendSesCodeTopic;
    @NotBlank
    private String creditIssuedTopic;
    @NotBlank
    private String applicationDeniedTopic;
}
