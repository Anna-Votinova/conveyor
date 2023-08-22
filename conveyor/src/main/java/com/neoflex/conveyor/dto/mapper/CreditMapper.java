package com.neoflex.conveyor.dto.mapper;

import com.neoflex.conveyor.dto.response.CreditServiceDTO;
import com.neoflex.conveyor.dto.response.CreditDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreditMapper {

    private final PaymentScheduleMapper paymentScheduleMapper;

    public CreditDTO toDto(CreditServiceDTO creditServiceDTO) {
        return CreditDTO.builder()
                .amount(creditServiceDTO.getAmount())
                .term(creditServiceDTO.getTerm())
                .monthlyPayment(creditServiceDTO.getMonthlyPayment())
                .rate(creditServiceDTO.getRate())
                .psk(creditServiceDTO.getPsk())
                .isInsuranceEnabled(creditServiceDTO.getIsInsuranceEnabled())
                .isSalaryClient(creditServiceDTO.getIsSalaryClient())
                .paymentSchedule(creditServiceDTO.getPaymentScheduleServiceElement().stream()
                                                 .map(paymentScheduleMapper::toElement)
                                                 .toList())
                .build();
    }
}
