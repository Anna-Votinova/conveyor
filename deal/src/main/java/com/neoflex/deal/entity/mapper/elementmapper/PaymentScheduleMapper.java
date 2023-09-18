package com.neoflex.deal.entity.mapper.elementmapper;

import com.neoflex.deal.dto.response.element.PaymentScheduleResponseElement;
import com.neoflex.deal.entity.jsonb.element.PaymentScheduleElement;
import org.springframework.stereotype.Service;

@Service
public class PaymentScheduleMapper {

    public PaymentScheduleResponseElement toPaymentScheduleResponseElement(PaymentScheduleElement scheduleElement) {

        PaymentScheduleResponseElement scheduleResponseElement = new PaymentScheduleResponseElement();
        scheduleResponseElement.setNumber(scheduleElement.getNumber());
        scheduleResponseElement.setDate(scheduleElement.getDate());
        scheduleResponseElement.setTotalPayment(scheduleElement.getTotalPayment());
        scheduleResponseElement.setInterestPayment(scheduleElement.getInterestPayment());
        scheduleResponseElement.setDebtPayment(scheduleElement.getDebtPayment());
        scheduleResponseElement.setRemainingDebt(scheduleElement.getRemainingDebt());

        return scheduleResponseElement;
    }
}
