package com.neoflex.deal.entity.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;

@Builder
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Предложение от банка")
public class LoanOfferDTO {

    @NotNull
    @Positive
    @Schema(description = "Номер предложения", example = "1")
    private Long applicationId;

    @Min(10000)
    @NotNull
    @Schema(description = "Запрашиваемая сумма займа", example = "100000")
    private BigDecimal requestedAmount;

    @Min(10000)
    @NotNull
    @Schema(description = "Общая сумма кредитиа (с учетом страховки)", example = "110000")
    private BigDecimal totalAmount;

    @Min(6)
    @NotNull
    @Schema(description = "Срок кредита (в месяцах)", example = "18")
    private Integer term;

    @NotNull
    @Positive
    @Schema(description = "Ежемесячный платеж", example = "6759.5")
    private BigDecimal monthlyPayment;

    @NotNull
    @Positive
    @Schema(description = "Ставка", example = "13")
    private BigDecimal rate;

    @NotNull
    @Schema(description = "Страховка включена в кредит", example = "true")
    private Boolean isInsuranceEnabled;

    @NotNull
    @Schema(description = "Заемщик - зарплатный клиент банка", example = "false")
    private Boolean isSalaryClient;

}
