package com.neoflex.gateway.integration.application;

import com.neoflex.gateway.dto.LoanOfferDTO;
import com.neoflex.gateway.dto.request.LoanApplicationRequestDTO;
import com.neoflex.gateway.exception.UnknownServerException;
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

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class ApplicationClient {

    private static final String API_PREFIX = "/application";
    private final RestTemplate restTemplate;

    @Autowired
    public ApplicationClient(@Value("${APPLICATION_URL}") String serverUrl, RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                .errorHandler(new RestTemplateErrorHandler())
                .build();
    }

    public List<LoanOfferDTO> prepareOffers(LoanApplicationRequestDTO requestDTO) {
        LoanOfferDTO[] offerArray = restTemplate.postForObject("", requestDTO, LoanOfferDTO[].class);

        if (Objects.isNull(offerArray)) {
            log.error("Retrieved list is null");
            throw new UnknownServerException("проблемы на стороне удаленного сервера.");
        }
        List<LoanOfferDTO> offerList = Arrays.stream(offerArray).toList();
        log.info("List of offers size equals to: {}", offerList.size());
        return offerList;
    }

    public void chooseOffer(LoanOfferDTO requestDTO) {
        HttpEntity<LoanOfferDTO> offerEntity = new HttpEntity<>(requestDTO);
        restTemplate.exchange("/offer", HttpMethod.PUT, offerEntity, Void.class);
    }
}
