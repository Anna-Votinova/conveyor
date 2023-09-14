package com.neoflex.gateway.dto.response.element;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
@Schema(description = "Выбранное клиентом предложение от банка")
public record AppliedOfferInfo (
        @Schema(description = "Запрашиваемая сумма займа", example = "100000")
        BigDecimal requestedAmount,

        @Schema(description = "Общая сумма кредитиа (с учетом страховки)", example = "110000")
        BigDecimal totalAmount,

        @Schema(description = "Срок кредита (в месяцах)", example = "18")
        Integer term,

        @Schema(description = "Ежемесячный платеж", example = "6759.5")
        BigDecimal monthlyPayment,

        @Schema(description = "Ставка", example = "13")
        BigDecimal rate,

        @Schema(description = "Страховка включена в кредит", example = "true")
        Boolean isInsuranceEnabled,

        @Schema(description = "Заемщик - зарплатный клиент банка", example = "false")
        Boolean isSalaryClient
) {}
