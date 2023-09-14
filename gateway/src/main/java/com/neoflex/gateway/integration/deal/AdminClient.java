package com.neoflex.gateway.integration.deal;

import com.neoflex.gateway.integration.BaseClient;
import com.neoflex.gateway.dto.enums.ApplicationStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.util.Map;

@Service
public class AdminClient extends BaseClient {

    private static final String API_PREFIX = "/admin/application";

    @Autowired
    public AdminClient(@Value("${deal.url}") String serverUrl, RestTemplateBuilder builder) {
        super(builder.uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                     .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                     .build());

    }

    public ResponseEntity<Object> getApplication(Long applicationId) {
        return get("/" + applicationId);
    }

    public ResponseEntity<Object> getApplications() {
        return get("");
    }

    public ResponseEntity<Object> changeApplicationStatus(Long applicationId, ApplicationStatus status) {
        Map<String, Object> parameters = Map.of("status", status);
        return put("/" + applicationId + "/status?status={status}", parameters);
    }
}
