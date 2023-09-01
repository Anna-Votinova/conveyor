package com.neoflex.deal.dto.response;

import com.neoflex.deal.entity.jsonb.element.PaymentScheduleElement;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.List;

@Setter
@Getter
@ToString
@AllArgsConstructor
@Schema(description = "Информация о кредите и клиенте для подготовки документов")
public class DocumentDTO {

    @Schema(description = "Имя", example = "Alexandra")
    private String firstName;

    @Schema(description = "Фамилия", example = "Black")
    String lastName;

    @Schema(description = "Отчество (при наличии)", example = "Igorevna")
    private String middleName;

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
