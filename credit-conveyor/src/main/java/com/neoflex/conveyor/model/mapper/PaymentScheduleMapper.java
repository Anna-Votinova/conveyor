package com.neoflex.conveyor.model.mapper;

import com.neoflex.conveyor.model.PaymentSchedule;
import com.neoflex.conveyor.model.dto.PaymentScheduleElement;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PaymentScheduleMapper {

    public static PaymentScheduleElement toElement(PaymentSchedule paymentSchedule) {
        return PaymentScheduleElement.builder()
                .number(paymentSchedule.getNumber())
                .date(paymentSchedule.getDate())
                .totalPayment(paymentSchedule.getTotalPayment())
                .interestPayment(paymentSchedule.getInterestPayment())
                .debtPayment(paymentSchedule.getDebtPayment())
                .remainingDebt(paymentSchedule.getRemainingDebt())
                .build();
    }
}
