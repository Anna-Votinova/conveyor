package com.neoflex.gateway.dto.response.element;

import com.neoflex.gateway.dto.enums.CreditStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.List;

@Builder
@Schema(description = "Информация о кредите")
public record CreditInfo (
        @Schema(description = "Идентификатор кредита", example = "1")
        Long id,

        @Schema(description = "Итоговая сумма займа (с учетом страховки)", example = "110000")
        BigDecimal amount,

        @Schema(description = "Срок кредита (в месяцах)", example = "18")
        Integer term,

        @Schema(description = "Ежемесячный платеж", example = "6606.6")
        BigDecimal monthlyPayment,

        @Schema(description = "Окончательная ставка", example = "10")
        BigDecimal rate,

        @Schema(description = "Полная стоимость кредита (в процентах годовых)", example = "5.399")
        BigDecimal psk,

        @Schema(description = "Страховка включена в кредит", example = "true")
        Boolean isInsuranceEnabled,

        @Schema(description = "Заемщик - зарплатный клиент банка", example = "false")
        Boolean isSalaryClient,

        @Schema(description = "График платежей")
        @ToString.Exclude
        List<PaymentScheduleResponseElement> paymentSchedule,

        @Schema(description = "Статус кредита", example = "CALCULATED")
        CreditStatus creditStatus
) {}
