package com.neoflex.deal.dto.response.element;

import com.neoflex.deal.entity.enums.CreditStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.List;

@Data
@Schema(description = "Информация о кредите")
public class CreditInfo {

    @Schema(description = "Идентификатор кредита", example = "1")
    private Long id;

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
    @ToString.Exclude
    private List<PaymentScheduleResponseElement> paymentSchedule;

    @Schema(description = "Статус кредита", example = "CALCULATED")
    private CreditStatus creditStatus;
}
