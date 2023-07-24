package com.neoflex.conveyor.service;

import com.neoflex.conveyor.dto.CreditDTO;
import com.neoflex.conveyor.dto.LoanApplicationRequestDTO;
import com.neoflex.conveyor.dto.LoanOfferDTO;
import com.neoflex.conveyor.dto.ScoringDataDTO;
import java.util.Collections;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@NoArgsConstructor
public class CreditConveyorService {

    public List<LoanOfferDTO> preCalculateLoan(LoanApplicationRequestDTO dto) {
        return Collections.emptyList();
    }

    public CreditDTO calculateLoan(ScoringDataDTO dto) {
        return null;
    }

}
