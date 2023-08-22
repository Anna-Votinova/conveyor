package com.neoflex.deal.entity.jsonb.element;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDate;

@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class PaymentScheduleElement {

    private Integer number;

    private LocalDate date;

    private BigDecimal totalPayment;

    private BigDecimal interestPayment;

    private BigDecimal debtPayment;

    private BigDecimal remainingDebt;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PaymentScheduleElement)) return false;
        return number != null && number.equals(((PaymentScheduleElement) o).getNumber());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
