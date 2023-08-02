package com.neoflex.conveyor.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.lang.reflect.Field;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class CommonCalculationLoanServiceTest {

    private final CommonCalculationLoanService commonCalculationLoanService = new CommonCalculationLoanService();


    @DisplayName("JUnit test for preCalculateRate method (positive scenario)")
    @ParameterizedTest
    @CsvSource({"true, true, 12", "true, false, 13", "false, true, 14", "false, false, 15"})
    void givenInsuranceEnabledAndIsSalaryClient_whenPreCalculateRate_thenReturnExpectedRate (
            boolean isInsurance, boolean isClient, String expected) throws NoSuchFieldException, IllegalAccessException {

        Field field = CommonCalculationLoanService.class.getDeclaredField("globalRate");
        field.setAccessible(true);
        field.set(commonCalculationLoanService, "15");

        assertEquals(new BigDecimal(expected), commonCalculationLoanService.preCalculateRate(isInsurance, isClient));

    }


    @Test
    @DisplayName("JUnit test for calculateAmount method (positive scenario)")
    void givenInsuranceEnabledTrue_whenCalculateAmount_thenReturnAmountConsistsInsCost() {

        BigDecimal requestedAmount = new BigDecimal("70000");
        BigDecimal finalAmount = new BigDecimal("77000.0");


        assertEquals(finalAmount, commonCalculationLoanService.calculateAmount(requestedAmount, true));

    }

    @Test
    @DisplayName("JUnit test for calculateAmount method (positive scenario)")
    void givenInsuranceEnabledFalse_whenCalculateAmount_thenReturnAmount() {

        BigDecimal requestedAmount = new BigDecimal("70000");
        BigDecimal finalAmount = new BigDecimal("70000");

        assertEquals(finalAmount, commonCalculationLoanService.calculateAmount(requestedAmount, false));

    }


   @Test
    @DisplayName("JUnit test for calculateMonthlyPayment method (positive scenario)")
    void givenRateAndTermAndAmount_whenCalculateMonthlyPayment_thenReturnMonthlyPayment() {

        BigDecimal rate = new BigDecimal("15");
        BigDecimal requestedAmount = new BigDecimal("10000");
        Integer term = 6;
        BigDecimal monthlyPayment = new BigDecimal("1740.30");

        assertEquals(monthlyPayment, commonCalculationLoanService.calculateMonthlyPayment(rate, term, requestedAmount));

    }

}