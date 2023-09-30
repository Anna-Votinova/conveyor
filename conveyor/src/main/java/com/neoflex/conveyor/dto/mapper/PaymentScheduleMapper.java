package com.neoflex.conveyor.dto.mapper;

import com.neoflex.conveyor.dto.response.PaymentScheduleServiceElement;
import com.neoflex.conveyor.dto.response.PaymentScheduleElement;
import org.springframework.stereotype.Service;

@Service
public class PaymentScheduleMapper {

    public PaymentScheduleElement toElement(PaymentScheduleServiceElement paymentScheduleServiceElement) {
        return PaymentScheduleElement.builder()
                .number(paymentScheduleServiceElement.getNumber())
                .date(paymentScheduleServiceElement.getDate())
                .totalPayment(paymentScheduleServiceElement.getTotalPayment())
                .interestPayment(paymentScheduleServiceElement.getInterestPayment())
                .debtPayment(paymentScheduleServiceElement.getDebtPayment())
                .remainingDebt(paymentScheduleServiceElement.getRemainingDebt())
                .build();
    }
}
