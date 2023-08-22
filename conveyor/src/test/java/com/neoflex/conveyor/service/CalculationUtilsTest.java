package com.neoflex.conveyor.service;

import com.neoflex.conveyor.service.utils.CalculationUtils;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class CalculationUtilsTest {

    private final CalculationUtils calculationUtils = new CalculationUtils();

    @Test
    void shouldReturnMonthlyPayment_WhenValidRateAndTermAndAmount() {

        BigDecimal rate = new BigDecimal("15");
        BigDecimal requestedAmount = new BigDecimal("10000");
        Integer term = 6;
        BigDecimal monthlyPayment = new BigDecimal("1740.30");

        assertEquals(monthlyPayment, calculationUtils.calculateMonthlyPayment(rate, term, requestedAmount));
    }

    @Test
    void shouldReturnNotEqualsMonthlyPayment_WhenWrongExpectedMonthlyPayment() {

        BigDecimal rate = new BigDecimal("15");
        BigDecimal requestedAmount = new BigDecimal("10000");
        Integer term = 6;
        BigDecimal wrongMonthlyPayment = new BigDecimal("1740.31");

        assertNotEquals(wrongMonthlyPayment, calculationUtils.calculateMonthlyPayment(rate, term, requestedAmount));
    }
}