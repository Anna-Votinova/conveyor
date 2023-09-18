package com.neoflex.gateway.integration.deal;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class DocumentClient {

    private static final String API_PREFIX = "/deal/document";

    private final RestTemplate restTemplateDeal;

    public void sendDocument(Long applicationId) {
        restTemplateDeal.postForObject(API_PREFIX + "/" + applicationId + "/send", null, Void.class);
    }

    public void signDocument(Long applicationId) {
        restTemplateDeal.postForObject(API_PREFIX + "/" + applicationId + "/sign", null, Void.class);
    }

    public void issueCredit(Long applicationId, Integer code) {
        HttpEntity<Integer> codeEntity = new HttpEntity<>(code);
        restTemplateDeal.postForObject(API_PREFIX + "/" + applicationId + "/code", codeEntity, Void.class);
    }
}
