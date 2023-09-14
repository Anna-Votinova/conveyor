package com.neoflex.gateway.integration.deal;

import com.neoflex.gateway.dto.request.FinishRegistrationRequestDTO;
import com.neoflex.gateway.integration.RestTemplateErrorHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;


@Service
@Slf4j
public class DealClient {

    private final RestTemplate restTemplate;

    @Autowired
    public DealClient(@Value("${DEAL_URL}") String serverUrl, RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                .errorHandler(new RestTemplateErrorHandler())
                .build();
    }

    public void calculateCredit(FinishRegistrationRequestDTO requestDTO, Long applicationId) {
        HttpEntity<FinishRegistrationRequestDTO> finishRegistrationEntity = new HttpEntity<>(requestDTO);
        restTemplate.exchange("/calculate/" + applicationId, HttpMethod.PUT, finishRegistrationEntity, Void.class);
    }
}
