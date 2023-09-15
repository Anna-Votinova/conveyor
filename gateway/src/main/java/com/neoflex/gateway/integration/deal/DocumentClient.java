package com.neoflex.gateway.integration.deal;

import com.neoflex.gateway.integration.RestTemplateErrorHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

@Service
public class DocumentClient {

    private static final String API_PREFIX = "/document";
    private final RestTemplate restTemplate;

    @Autowired
    public DocumentClient(@Value("${DEAL_URL}") String serverUrl, RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                .errorHandler(new RestTemplateErrorHandler())
                .build();
    }

    public void sendDocument(Long applicationId) {
        restTemplate.exchange("/" + applicationId + "/send", HttpMethod.POST, null, Void.class);
    }

    public void signDocument(Long applicationId) {
        restTemplate.exchange("/" + applicationId + "/sign", HttpMethod.POST, null, Void.class);
    }

    public void issueCredit(Long applicationId, Integer code) {
        HttpEntity<Integer> codeEntity = new HttpEntity<>(code);
        restTemplate.exchange("/" + applicationId + "/code", HttpMethod.POST, codeEntity, Void.class);
    }
}
