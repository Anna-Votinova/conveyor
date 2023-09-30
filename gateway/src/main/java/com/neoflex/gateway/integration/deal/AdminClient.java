package com.neoflex.gateway.integration.deal;

import com.neoflex.gateway.dto.enums.ApplicationStatus;
import com.neoflex.gateway.dto.response.ApplicationDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class AdminClient {

    private static final String API_PREFIX = "/deal/admin/application";

    private final RestTemplate restTemplateDeal;

    public void changeApplicationStatus(Long applicationId, ApplicationStatus status) {
        Map<String, Object> parameters = Map.of("status", status);
        restTemplateDeal.put(API_PREFIX + "/" + applicationId + "/status?status={status}", null, parameters);
    }

    public ApplicationDTO findApplicationById(Long applicationId) {
        ApplicationDTO applicationDTO =
                restTemplateDeal.getForObject(API_PREFIX + "/" + applicationId, ApplicationDTO.class);
        log.info("Retrieved application equals to  {}", applicationDTO);
        return applicationDTO;
    }

    public List<ApplicationDTO> findAllApplications() {
        ApplicationDTO[] applicationsArray = restTemplateDeal.getForObject(API_PREFIX, ApplicationDTO[].class);

        if (Objects.isNull(applicationsArray)) {
            log.info("Retrieved applications list is null");
            return Collections.emptyList();
        }
        List<ApplicationDTO> applicationList = Arrays.stream(applicationsArray).toList();
        log.info("List of applications size equals to: {}", applicationList.size());
        return applicationList;
    }
}
