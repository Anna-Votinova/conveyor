package com.neoflex.deal.dto.response.element;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Schema(description = "График платежей")
public class PaymentScheduleResponseElement {

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
