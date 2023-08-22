package com.neoflex.deal.entity.mapper;

import com.neoflex.deal.dto.LoanOfferDTO;
import com.neoflex.deal.entity.jsonb.element.AppliedOffer;
import org.springframework.stereotype.Service;

@Service
public class OfferMapper {

    public AppliedOffer toAppliedOffer(LoanOfferDTO loanOfferDTO) {

        AppliedOffer appliedOffer = new AppliedOffer();
        appliedOffer.setApplicationId(loanOfferDTO.getApplicationId());
        appliedOffer.setRequestedAmount(loanOfferDTO.getRequestedAmount());
        appliedOffer.setTotalAmount(loanOfferDTO.getTotalAmount());
        appliedOffer.setTerm(loanOfferDTO.getTerm());
        appliedOffer.setMonthlyPayment(loanOfferDTO.getMonthlyPayment());
        appliedOffer.setRate(loanOfferDTO.getRate());
        appliedOffer.setIsInsuranceEnabled(loanOfferDTO.getIsInsuranceEnabled());
        appliedOffer.setIsSalaryClient(loanOfferDTO.getIsSalaryClient());

        return appliedOffer;
    }
}
