package com.neoflex.gateway.integration.deal;

import com.neoflex.gateway.dto.request.FinishRegistrationRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class DealClient {

    private final RestTemplate restTemplateDeal;

    public void calculateCredit(FinishRegistrationRequestDTO requestDTO, Long applicationId) {
        restTemplateDeal.put("/deal/calculate/" + applicationId, requestDTO);
    }
}
