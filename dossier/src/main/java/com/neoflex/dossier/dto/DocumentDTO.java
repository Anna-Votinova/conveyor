package com.neoflex.dossier.dto;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

@Builder
public record DocumentDTO(
        String firstName,
        String lastName,
        String middleName,
        BigDecimal amount,
        Integer term,
        BigDecimal monthlyPayment,
        BigDecimal rate,
        BigDecimal psk,
        Boolean isInsuranceEnabled,
        Boolean isSalaryClient,
        List<PaymentScheduleElement> paymentSchedule
) {}
