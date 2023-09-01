package com.neoflex.dossier.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

@Builder
@Schema(description = "Информация о кредите и клиенте для подготовки документов")
public record DocumentDTO(
        @Schema(description = "Имя", example = "Alexandra")
        String firstName,

        @Schema(description = "Фамилия", example = "Black")
        String lastName,

        @Schema(description = "Отчество (при наличии)", example = "Igorevna")
        String middleName,

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
        List<PaymentScheduleElement> paymentSchedule
) {}
