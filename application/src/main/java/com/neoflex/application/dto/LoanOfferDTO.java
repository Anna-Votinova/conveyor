package com.neoflex.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;

@Builder
@Schema(description = "Предложение от банка")
public record LoanOfferDTO(
        @NotNull
        @Positive
        @Schema(description = "Номер предложения", example = "1")
        Long applicationId,

        @Min(10000)
        @NotNull
        @Schema(description = "Запрашиваемая сумма займа", example = "100000")
        BigDecimal requestedAmount,

        @Min(10000)
        @NotNull
        @Schema(description = "Общая сумма кредитиа (с учетом страховки)", example = "110000")
        BigDecimal totalAmount,

        @Min(6)
        @NotNull
        @Schema(description = "Срок кредита (в месяцах)", example = "18")
        Integer term,

        @NotNull
        @Positive
        @Schema(description = "Ежемесячный платеж", example = "6759.5")
        BigDecimal monthlyPayment,

        @NotNull
        @Positive
        @Schema(description = "Ставка", example = "13")
        BigDecimal rate,

        @NotNull
        @Schema(description = "Страховка включена в кредит", example = "true")
        Boolean isInsuranceEnabled,

        @NotNull
        @Schema(description = "Заемщик - зарплатный клиент банка", example = "false")
        Boolean isSalaryClient
) {}
