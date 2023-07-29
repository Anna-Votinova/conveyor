package com.neoflex.conveyor.model.dto;

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

    @Schema(description = "Номер предложения")
    private Long applicationId;
    @Schema(description = "Запрашиваемая сумма займа")
    private BigDecimal requestedAmount;
    @Schema(description = "Общая сумма кредитиа (с учетом страховки)")
    private BigDecimal totalAmount;
    @Schema(description = "Срок кредита (в месяцах)")
    private Integer term;
    @Schema(description = "Ежемесячный платеж")
    private BigDecimal monthlyPayment;
    @Schema(description = "Ставка")
    private BigDecimal rate;
    @Schema(description = "Страховка включена в кредит (да/нет)")
    private Boolean isInsuranceEnabled;
    @Schema(description = "Заемщик - зарплатный клиент банка (да/нет)")
    private Boolean isSalaryClient;

}
