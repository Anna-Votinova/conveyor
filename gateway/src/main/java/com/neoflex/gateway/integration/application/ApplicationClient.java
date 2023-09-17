package com.neoflex.gateway.integration.application;

import com.neoflex.gateway.dto.LoanOfferDTO;
import com.neoflex.gateway.dto.request.LoanApplicationRequestDTO;
import com.neoflex.gateway.exception.UnknownServerException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class ApplicationClient {

    private static final String API_PREFIX = "/application";

    private final RestTemplate restTemplateApplication;

    public List<LoanOfferDTO> prepareOffers(LoanApplicationRequestDTO requestDTO) {
        LoanOfferDTO[] offerArray = restTemplateApplication.postForObject(API_PREFIX, requestDTO, LoanOfferDTO[].class);

        if (Objects.isNull(offerArray)) {
            log.error("Retrieved list is null");
            throw new UnknownServerException("проблемы на стороне удаленного сервера.");
        }
        List<LoanOfferDTO> offerList = Arrays.stream(offerArray).toList();
        log.info("List of offers size equals to: {}", offerList.size());
        return offerList;
    }

    public void chooseOffer(LoanOfferDTO requestDTO) {
        restTemplateApplication.put(API_PREFIX + "/offer", requestDTO);
    }
}
