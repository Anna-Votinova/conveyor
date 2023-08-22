package com.neoflex.deal.integration.conveyor;

import com.neoflex.deal.dto.LoanOfferDTO;
import com.neoflex.deal.dto.LoanApplicationRequestDTO;
import com.neoflex.deal.dto.request.CreditDTO;
import com.neoflex.deal.dto.response.ScoringDataDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(value = "conveyor", url = "${conveyor.url}", configuration = ClientConfiguration.class)
public interface ConveyorClient {

    @PostMapping(value = "/offers")
    List<LoanOfferDTO> preCalculateLoan(@RequestBody LoanApplicationRequestDTO requestDTO);

    @PostMapping(value = "/calculation")
    CreditDTO calculateLoan(@RequestBody ScoringDataDTO requestDto);
}
