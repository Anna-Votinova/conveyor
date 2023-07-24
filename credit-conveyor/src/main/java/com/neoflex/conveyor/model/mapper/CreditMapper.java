package com.neoflex.conveyor.model.mapper;

import com.neoflex.conveyor.model.Credit;
import com.neoflex.conveyor.model.dto.CreditDTO;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CreditMapper {

    public static CreditDTO toDto(Credit credit) {
        return CreditDTO.builder()
                .amount(credit.getAmount())
                .term(credit.getTerm())
                .monthlyPayment(credit.getMonthlyPayment())
                .rate(credit.getRate())
                .psk(credit.getPsk())
                .isInsuranceEnabled(credit.getIsInsuranceEnabled())
                .isSalaryClient(credit.getIsSalaryClient())
                .paymentSchedule(credit.getPaymentSchedule().stream()
                        .map(PaymentScheduleMapper::toElement)
                        .toList())
                .build();
    }
}
