package com.neoflex.deal.entity.mapper;

import com.neoflex.deal.entity.Credit;
import com.neoflex.deal.dto.request.CreditDTO;
import org.springframework.stereotype.Service;

@Service
public class CreditMapper {

    public Credit toCredit(CreditDTO creditDTO) {

        Credit credit = new Credit();
        credit.setAmount(creditDTO.getAmount());
        credit.setTerm(creditDTO.getTerm());
        credit.setMonthlyPayment(creditDTO.getMonthlyPayment());
        credit.setRate(creditDTO.getRate());
        credit.setPsk(creditDTO.getPsk());
        credit.setPaymentSchedule(creditDTO.getPaymentSchedule());
        credit.setInsuranceEnable(creditDTO.getIsInsuranceEnabled());
        credit.setSalaryClient(creditDTO.getIsSalaryClient());

        return credit;
    }
}
