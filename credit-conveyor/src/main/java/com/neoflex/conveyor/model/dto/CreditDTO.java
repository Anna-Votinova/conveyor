package com.neoflex.conveyor.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.List;

@Builder
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Одобренная заявка на кредит")
public class CreditDTO {

    @Schema(description = "Итоговая сумма займа (с учетом страховки)")
    private BigDecimal amount;
    @Schema(description = "Срок кредита (в месяцах)")
    private Integer term;
    @Schema(description = "Ежемесячный платеж")
    private BigDecimal monthlyPayment;
    @Schema(description = "Окончательная ставка")
    private BigDecimal rate;
    @Schema(description = "Полная стоимость кредита (в процентах годовых)")
    private BigDecimal psk;
    @Schema(description = "Страховка включена в кредит (да/нет)")
    private Boolean isInsuranceEnabled;
    @Schema(description = "Заемщик - зарплатный клиент банка (да/нет)")
    private Boolean isSalaryClient;
    @Schema(description = "График платежей")
    List<PaymentScheduleElement> paymentSchedule;

}
