package com.neoflex.deal.entity.dto.response;

import com.neoflex.deal.entity.jsonb.element.PaymentScheduleElement;
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

    @Schema(description = "Итоговая сумма займа (с учетом страховки)", example = "110000")
    private BigDecimal amount;

    @Schema(description = "Срок кредита (в месяцах)", example = "18")
    private Integer term;

    @Schema(description = "Ежемесячный платеж", example = "6606.6")
    private BigDecimal monthlyPayment;

    @Schema(description = "Окончательная ставка", example = "10")
    private BigDecimal rate;

    @Schema(description = "Полная стоимость кредита (в процентах годовых)", example = "5.399")
    private BigDecimal psk;

    @Schema(description = "Страховка включена в кредит", example = "true")
    private Boolean isInsuranceEnabled;

    @Schema(description = "Заемщик - зарплатный клиент банка", example = "false")
    private Boolean isSalaryClient;

    @Schema(description = "График платежей")
    private List<PaymentScheduleElement> paymentSchedule;

}
