package com.neoflex.conveyor.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "График платежей")
public class PaymentScheduleElement {

    @Schema(description = "Порядковый номер платежа")
    private Integer number;
    @Schema(description = "Дата платежа")
    private LocalDate date;
    @Schema(description = "Ежемесячный платеж")
    private BigDecimal totalPayment;
    @Schema(description = "Сумма процентов")
    private BigDecimal interestPayment;
    @Schema(description = "Основной долг")
    private BigDecimal debtPayment;
    @Schema(description = "Остаток долга")
    private BigDecimal remainingDebt;

}
