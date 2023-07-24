package com.neoflex.conveyor.model.mapper;

import com.neoflex.conveyor.model.LoanOffer;
import com.neoflex.conveyor.model.dto.LoanOfferDTO;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class LoanOfferMapper {

    public static LoanOfferDTO toDto(LoanOffer loanOffer) {
        return LoanOfferDTO.builder()
                .applicationId(loanOffer.getApplicationId())
                .requestedAmount(loanOffer.getRequestedAmount())
                .totalAmount(loanOffer.getTotalAmount())
                .term(loanOffer.getTerm())
                .monthlyPayment(loanOffer.getMonthlyPayment())
                .rate(loanOffer.getRate())
                .isInsuranceEnabled(loanOffer.getIsInsuranceEnabled())
                .isSalaryClient(loanOffer.getIsSalaryClient())
                .build();
    }

}
