package com.neoflex.deal.entity.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

@Builder
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Предложение от банка")
public class LoanOfferDTO {

    @Schema(description = "Номер предложения", example = "208340162356398400")
    private Long applicationId;

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
