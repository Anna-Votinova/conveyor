package com.neoflex.deal.entity.mapper;

import com.neoflex.deal.dto.LoanOfferDTO;
import com.neoflex.deal.dto.response.element.AppliedOfferInfo;
import com.neoflex.deal.entity.jsonb.element.AppliedOffer;
import org.springframework.stereotype.Service;

import java.util.Objects;

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

    public AppliedOfferInfo toAppliedOfferInfo(AppliedOffer appliedOffer) {

        AppliedOfferInfo appliedOfferInfo = new AppliedOfferInfo();

        if (Objects.nonNull(appliedOffer)) {
            appliedOfferInfo.setTotalAmount(appliedOffer.getTotalAmount());
            appliedOfferInfo.setTerm(appliedOffer.getTerm());
            appliedOfferInfo.setRequestedAmount(appliedOffer.getRequestedAmount());
            appliedOfferInfo.setMonthlyPayment(appliedOffer.getMonthlyPayment());
            appliedOfferInfo.setRate(appliedOffer.getRate());
            appliedOfferInfo.setIsInsuranceEnabled(appliedOffer.getIsInsuranceEnabled());
            appliedOfferInfo.setIsSalaryClient(appliedOffer.getIsSalaryClient());
        }
        return appliedOfferInfo;
    }
}
