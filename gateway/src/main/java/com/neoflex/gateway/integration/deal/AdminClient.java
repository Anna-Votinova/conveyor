package com.neoflex.gateway.integration.deal;

import com.neoflex.gateway.dto.enums.ApplicationStatus;
import com.neoflex.gateway.dto.response.ApplicationDTO;
import com.neoflex.gateway.integration.RestTemplateErrorHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@Slf4j
public class AdminClient {

    private static final String API_PREFIX = "/admin/application";
    private final RestTemplate restTemplate;

    @Autowired
    public AdminClient(@Value("${DEAL_URL}") String serverUrl, RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                .errorHandler(new RestTemplateErrorHandler())
                .build();
    }

    public void changeApplicationStatus(Long applicationId, ApplicationStatus status) {
        Map<String, Object> parameters = Map.of("status", status);
        restTemplate.exchange("/" + applicationId + "/status?status={status}", HttpMethod.PUT,
                null, Void.class, parameters);
    }

    public ApplicationDTO findApplicationById(Long applicationId) {
        ApplicationDTO applicationDTO = restTemplate.getForObject("/" + applicationId, ApplicationDTO.class);
        log.info("Retrieved application equals to  {}", applicationDTO);
        return applicationDTO;
    }

    public List<ApplicationDTO> findAllApplications() {
        ApplicationDTO[] applicationsArray = restTemplate.getForObject("", ApplicationDTO[].class);

        if (Objects.isNull(applicationsArray)) {
            log.info("Retrieved applications list is null");
            return Collections.emptyList();
        }
        List<ApplicationDTO> applicationList = Arrays.stream(applicationsArray).toList();
        log.info("List of applications size equals to: {}", applicationList.size());
        return applicationList;
    }
}
