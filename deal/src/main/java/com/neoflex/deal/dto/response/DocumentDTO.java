package com.neoflex.deal.dto.response;

import com.neoflex.deal.entity.jsonb.element.PaymentScheduleElement;
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
public class DocumentDTO {

    private String firstName;
    private String lastName;
    private String middleName;
    private BigDecimal amount;
    private Integer term;
    private BigDecimal monthlyPayment;
    private BigDecimal rate;
    private BigDecimal psk;
    private Boolean isInsuranceEnabled;
    private Boolean isSalaryClient;
    @ToString.Exclude
    private List<PaymentScheduleElement> paymentSchedule;
}
