package com.neoflex.application.integration.deal;

import com.neoflex.application.dto.LoanApplicationRequestDTO;
import com.neoflex.application.dto.LoanOfferDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(value = "deal", url = "${deal.url}", configuration = ClientConfiguration.class)
public interface DealClient {

    @PostMapping(value = "/application")
    List<LoanOfferDTO> calculateOffers(@RequestBody LoanApplicationRequestDTO requestDTO);

    @PutMapping(value = "/offer")
    void chooseOffer(@RequestBody LoanOfferDTO requestDTO);
}
