package com.neoflex.deal.entity.mapper;

import com.neoflex.deal.dto.response.element.CreditInfo;
import com.neoflex.deal.dto.response.element.PaymentScheduleResponseElement;
import com.neoflex.deal.entity.Credit;
import com.neoflex.deal.dto.request.CreditDTO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

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

    public CreditInfo toCreditInfo(Credit credit, List<PaymentScheduleResponseElement> paymentSchedule) {

        CreditInfo creditInfo = new CreditInfo();

        if (Objects.nonNull(credit)) {
            creditInfo.setId(credit.getId());
            creditInfo.setAmount(credit.getAmount());
            creditInfo.setTerm(credit.getTerm());
            creditInfo.setMonthlyPayment(credit.getMonthlyPayment());
            creditInfo.setRate(credit.getRate());
            creditInfo.setPsk(credit.getPsk());
            creditInfo.setIsInsuranceEnabled(credit.getInsuranceEnable());
            creditInfo.setIsSalaryClient(credit.getSalaryClient());
            creditInfo.setPaymentSchedule(paymentSchedule);
            creditInfo.setCreditStatus(credit.getCreditStatus());
        }
        return creditInfo;
    }
}
