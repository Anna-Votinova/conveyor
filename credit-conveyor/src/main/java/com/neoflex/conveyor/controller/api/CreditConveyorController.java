package com.neoflex.conveyor.controller.api;

import com.neoflex.conveyor.model.dto.CreditDTO;
import com.neoflex.conveyor.model.dto.LoanApplicationRequestDTO;
import com.neoflex.conveyor.model.dto.LoanOfferDTO;
import com.neoflex.conveyor.model.dto.ScoringDataDTO;
import com.neoflex.conveyor.service.CreditConveyorService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/conveyor")
@Validated
public class CreditConveyorController {

    private final CreditConveyorService conveyorService;


    @PostMapping("/offers")
    public List<LoanOfferDTO> preCalculateLoan(@RequestBody LoanApplicationRequestDTO dto) {
        return conveyorService.preCalculateLoan(dto);
    }

    @PostMapping("/calculation")
    public CreditDTO calculateLoan(@RequestBody ScoringDataDTO dto) {
        return conveyorService.calculateLoan(dto);
    }


}
