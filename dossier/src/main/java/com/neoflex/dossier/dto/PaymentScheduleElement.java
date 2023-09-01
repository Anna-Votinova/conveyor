package com.neoflex.dossier.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
@Schema(description = "График платежей")
public record PaymentScheduleElement(
        @Schema(description = "Порядковый номер платежа", example = "1")
        Integer number,

        @Schema(description = "Дата платежа", example = "2023-09-12")
        LocalDate date,

        @Schema(description = "Ежемесячный платеж", example = "6606.6")
        BigDecimal totalPayment,

        @Schema(description = "Сумма процентов", example = "904.11")
        BigDecimal interestPayment,

        @Schema(description = "Основной долг", example = "5702.49")
        BigDecimal debtPayment,

        @Schema(description = "Остаток долга", example = "104297.51")
        BigDecimal remainingDebt
) {}
