package com.neoflex.conveyor.dto.response;

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

    @Schema(description = "Порядковый номер платежа", example = "1")
    private Integer number;

    @Schema(description = "Дата платежа", example = "2023-09-12")
    private LocalDate date;

    @Schema(description = "Ежемесячный платеж", example = "6606.6")
    private BigDecimal totalPayment;

    @Schema(description = "Сумма процентов", example = "904.11")
    private BigDecimal interestPayment;

    @Schema(description = "Основной долг", example = "5702.49")
    private BigDecimal debtPayment;

    @Schema(description = "Остаток долга", example = "104297.51")
    private BigDecimal remainingDebt;
}
