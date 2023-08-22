package com.neoflex.conveyor.dto.mapper;

import com.neoflex.conveyor.dto.response.LoanOfferServiceDTO;
import com.neoflex.conveyor.dto.response.LoanOfferDTO;
import org.springframework.stereotype.Service;

@Service
public class LoanOfferMapper {

    public LoanOfferDTO toDto(LoanOfferServiceDTO loanOfferServiceDTO) {
        return LoanOfferDTO.builder()
                .applicationId(loanOfferServiceDTO.getApplicationId())
                .requestedAmount(loanOfferServiceDTO.getRequestedAmount())
                .totalAmount(loanOfferServiceDTO.getTotalAmount())
                .term(loanOfferServiceDTO.getTerm())
                .monthlyPayment(loanOfferServiceDTO.getMonthlyPayment())
                .rate(loanOfferServiceDTO.getRate())
                .isInsuranceEnabled(loanOfferServiceDTO.getIsInsuranceEnabled())
                .isSalaryClient(loanOfferServiceDTO.getIsSalaryClient())
                .build();
    }
}
