package com.neoflex.deal.dto.response.element;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "Выбранное клиентом предложение от банка")
public class AppliedOfferInfo {

    @Schema(description = "Запрашиваемая сумма займа", example = "100000")
    private BigDecimal requestedAmount;

    @Schema(description = "Общая сумма кредитиа (с учетом страховки)", example = "110000")
    private BigDecimal totalAmount;

    @Schema(description = "Срок кредита (в месяцах)", example = "18")
    private Integer term;

    @Schema(description = "Ежемесячный платеж", example = "6759.5")
    private BigDecimal monthlyPayment;

    @Schema(description = "Ставка", example = "13")
    private BigDecimal rate;

    @Schema(description = "Страховка включена в кредит", example = "true")
    private Boolean isInsuranceEnabled;

    @Schema(description = "Заемщик - зарплатный клиент банка", example = "false")
    private Boolean isSalaryClient;
}
