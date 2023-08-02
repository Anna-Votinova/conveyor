package com.neoflex.conveyor.service;

import com.neoflex.conveyor.controller.advice.NotProperClientCategoryException;
import com.neoflex.conveyor.model.Credit;
import com.neoflex.conveyor.model.Employment;
import com.neoflex.conveyor.model.ScoringData;
import com.neoflex.conveyor.model.dto.enums.EmploymentStatus;
import com.neoflex.conveyor.model.dto.enums.Gender;
import com.neoflex.conveyor.model.dto.enums.MaritalStatus;
import com.neoflex.conveyor.model.dto.enums.Position;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.validation.ValidationException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreditServiceTest {

    @Mock
    private CommonCalculationLoanService commonCalculationLoanService;


    @InjectMocks
    private CreditService creditService;

    private ScoringData scoringData;


    @BeforeEach
    void setUp() {

        Employment employment = new Employment(
                EmploymentStatus.EMPLOYED,
                "123456789012",
                new BigDecimal("70000"),
                Position.WORKER,
                144,
                110
        );

        scoringData = new ScoringData(
                new BigDecimal("70000"),
                24,
                "Anna",
                "Black",
                "White",
                Gender.FEMALE,
                LocalDate.of(1990, 12, 13),
                "1111",
                "111111",
                LocalDate.of(2021, 4, 12),
                "MVD",
                MaritalStatus.MARRIED,
                0,
                employment,
                "12345678901234567890",
                true,
                true
        );

    }


    @Test
    @DisplayName("JUnit test for calculateLoan method (positive scenario)")
    void givenScoringData_whenCalculateLoan_thenReturnCredit() {

        Credit credit = new Credit(
                new BigDecimal("77000"),
                24,
                new BigDecimal("3517.36"),
                new BigDecimal("9"),
                new BigDecimal("4.819"),
                true,
                true,
                new ArrayList<>()
        );

        BigDecimal preRate = new BigDecimal("12");
        BigDecimal amountWithIns = new BigDecimal("77000");
        BigDecimal monthlyPayment = new BigDecimal("3517.36");

        when(commonCalculationLoanService.preCalculateRate(any(), any())).thenReturn(preRate);
        when(commonCalculationLoanService.calculateAmount(any(), any())).thenReturn(amountWithIns);
        when(commonCalculationLoanService.calculateMonthlyPayment(any(), any(), any())).thenReturn(monthlyPayment);

        Credit creditForResponse = creditService.calculateLoan(scoringData);


        assertEquals(credit.getAmount(), creditForResponse.getAmount());
        assertEquals(credit.getTerm(), creditForResponse.getTerm());
        assertEquals(credit.getMonthlyPayment(), creditForResponse.getMonthlyPayment());
        assertEquals(credit.getRate(), creditForResponse.getRate());

        assertEquals(credit.getPsk(), creditForResponse.getPsk());
        assertEquals(credit.getIsInsuranceEnabled(), creditForResponse.getIsInsuranceEnabled());
        assertEquals(credit.getIsSalaryClient(), creditForResponse.getIsSalaryClient());


        assertEquals(scoringData.getTerm(), creditForResponse.getPaymentSchedule().size());

        assertNotNull(creditForResponse.getPaymentSchedule().get(0));
        assertNotNull(creditForResponse.getPaymentSchedule().get(23));

        assertNotNull(creditForResponse.getPaymentSchedule().get(0).getNumber());
        assertNotNull(creditForResponse.getPaymentSchedule().get(0).getDate());
        assertNotNull(creditForResponse.getPaymentSchedule().get(0).getTotalPayment());
        assertNotNull(creditForResponse.getPaymentSchedule().get(0).getInterestPayment());
        assertNotNull(creditForResponse.getPaymentSchedule().get(0).getDebtPayment());
        assertNotNull(creditForResponse.getPaymentSchedule().get(0).getRemainingDebt());

        assertNotNull(creditForResponse.getPaymentSchedule().get(23).getNumber());
        assertNotNull(creditForResponse.getPaymentSchedule().get(23).getDate());
        assertNotNull(creditForResponse.getPaymentSchedule().get(23).getTotalPayment());
        assertNotNull(creditForResponse.getPaymentSchedule().get(23).getInterestPayment());
        assertNotNull(creditForResponse.getPaymentSchedule().get(23).getDebtPayment());
        assertNotNull(creditForResponse.getPaymentSchedule().get(23).getRemainingDebt());

        verify(commonCalculationLoanService, times(1)).preCalculateRate(any(), any());
        verify(commonCalculationLoanService, times(1)).calculateAmount(any(), any());
        verify(commonCalculationLoanService, times(1)).calculateMonthlyPayment(any(), any(), any());


    }

    @Test
    @DisplayName("JUnit test for calculateLoan method (negative scenario)")
    void givenEmptyScoringData_whenCalculateLoan_thenThrowNullPointerException() {

        ScoringData emptyScoringData = new ScoringData();

        assertThrows(NullPointerException.class, () -> creditService.calculateLoan(emptyScoringData));

    }

    @Test
    @DisplayName("JUnit test for calculateLoan method (negative scenario)")
    void givenScoringDataWithEmploymentStatusUnemployed_whenCalculateLoan_thenThrowNotProperClientCategoryException() {

        scoringData.getEmployment().setEmploymentStatus(EmploymentStatus.UNEMPLOYED);

        assertThrows(NotProperClientCategoryException.class, () -> creditService.calculateLoan(scoringData));

    }

    @Test
    @DisplayName("JUnit test for calculateLoan method (negative scenario)")
    void givenScoringDataWithoutEmployerINN_whenCalculateLoan_thenThrowValidationException() {

        scoringData.getEmployment().setEmployerINN(null);

        assertThrows(ValidationException.class, () -> creditService.calculateLoan(scoringData));

    }

    @Test
    @DisplayName("JUnit test for calculateLoan method (negative scenario)")
    void givenScoringDataWithoutPosition_whenCalculateLoan_thenThrowValidationException() {

        scoringData.getEmployment().setPosition(null);

        assertThrows(ValidationException.class, () -> creditService.calculateLoan(scoringData));

    }

    @Test
    @DisplayName("JUnit test for calculateLoan method (negative scenario)")
    void givenScoringDataWithSmallSalary_whenCalculateLoan_thenThrowNotProperClientCategoryException() {

        scoringData.getEmployment().setSalary(new BigDecimal("1000"));

        assertThrows(NotProperClientCategoryException.class, () -> creditService.calculateLoan(scoringData));

    }

    @Test
    @DisplayName("JUnit test for calculateLoan method (negative scenario)")
    void givenScoringDataWithMinClientAgeTomorrow_whenCalculateLoan_thenThrowNotProperClientCategoryException() {

        scoringData.setBirthdate(LocalDate.now().minusYears(20).plusDays(1));

        assertThrows(NotProperClientCategoryException.class, () -> creditService.calculateLoan(scoringData));

    }

    @Test
    @DisplayName("JUnit test for calculateLoan method (positive scenario)")
    void givenScoringDataWithMinClientAge_whenCalculateLoan_thenReturnCredit() {

        BigDecimal preRate = new BigDecimal("12");
        BigDecimal amountWithIns = new BigDecimal("77000");
        BigDecimal monthlyPayment = new BigDecimal("3517.36");

        scoringData.setBirthdate(LocalDate.now().minusYears(20));

        when(commonCalculationLoanService.preCalculateRate(any(), any())).thenReturn(preRate);
        when(commonCalculationLoanService.calculateAmount(any(), any())).thenReturn(amountWithIns);
        when(commonCalculationLoanService.calculateMonthlyPayment(any(), any(), any())).thenReturn(monthlyPayment);

        Credit creditForResponse = creditService.calculateLoan(scoringData);

        assertNotNull(creditForResponse);

    }

    @Test
    @DisplayName("JUnit test for calculateLoan method (positive scenario)")
    void givenScoringDataWithMinClientAgeYesterday_whenCalculateLoan_thenReturnCredit() {

        BigDecimal preRate = new BigDecimal("12");
        BigDecimal amountWithIns = new BigDecimal("77000");
        BigDecimal monthlyPayment = new BigDecimal("3517.36");

        scoringData.setBirthdate(LocalDate.now().minusYears(20).minusDays(1));

        when(commonCalculationLoanService.preCalculateRate(any(), any())).thenReturn(preRate);
        when(commonCalculationLoanService.calculateAmount(any(), any())).thenReturn(amountWithIns);
        when(commonCalculationLoanService.calculateMonthlyPayment(any(), any(), any())).thenReturn(monthlyPayment);

        Credit creditForResponse = creditService.calculateLoan(scoringData);

        assertNotNull(creditForResponse);

    }


    @Test
    @DisplayName("JUnit test for calculateLoan method (negative scenario)")
    void givenScoringDataWithMaxClientAgeYesterday_whenCalculateLoan_thenThrowNotProperClientCategoryException() {

        scoringData.setBirthdate(LocalDate.now().minusYears(60).minusDays(1));

        assertThrows(NotProperClientCategoryException.class, () -> creditService.calculateLoan(scoringData));

    }

    @Test
    @DisplayName("JUnit test for calculateLoan method (negative scenario)")
    void givenScoringDataWithMaxClientAgeToday_whenCalculateLoan_thenThrowNotProperClientCategoryException() {

        scoringData.setBirthdate(LocalDate.now().minusYears(60));

        assertThrows(NotProperClientCategoryException.class, () -> creditService.calculateLoan(scoringData));

    }

    @Test
    @DisplayName("JUnit test for calculateLoan method (positive scenario)")
    void givenScoringDataWithMaxClientAgeTomorrow_whenCalculateLoan_thenReturnCredit() {

        BigDecimal preRate = new BigDecimal("12");
        BigDecimal amountWithIns = new BigDecimal("77000");
        BigDecimal monthlyPayment = new BigDecimal("3517.36");

        scoringData.setBirthdate(LocalDate.now().minusYears(60).plusDays(1));

        when(commonCalculationLoanService.preCalculateRate(any(), any())).thenReturn(preRate);
        when(commonCalculationLoanService.calculateAmount(any(), any())).thenReturn(amountWithIns);
        when(commonCalculationLoanService.calculateMonthlyPayment(any(), any(), any())).thenReturn(monthlyPayment);

        Credit creditForResponse = creditService.calculateLoan(scoringData);

        assertNotNull(creditForResponse);

    }

    @Test
    @DisplayName("JUnit test for calculateLoan method (negative scenario)")
    void givenScoringDataWithSmallCommonWorkExperience_whenCalculateLoan_thenThrowNotProperClientCategoryException() {

        scoringData.getEmployment().setWorkExperienceTotal(11);

        assertThrows(NotProperClientCategoryException.class, () -> creditService.calculateLoan(scoringData));

    }

    @Test
    @DisplayName("JUnit test for calculateLoan method (positive scenario)")
    void givenScoringDataWithMinCommonWorkExperience_whenCalculateLoan_thenReturnCredit() {

        BigDecimal preRate = new BigDecimal("12");
        BigDecimal amountWithIns = new BigDecimal("77000");
        BigDecimal monthlyPayment = new BigDecimal("3517.36");

        scoringData.getEmployment().setWorkExperienceTotal(12);

        when(commonCalculationLoanService.preCalculateRate(any(), any())).thenReturn(preRate);
        when(commonCalculationLoanService.calculateAmount(any(), any())).thenReturn(amountWithIns);
        when(commonCalculationLoanService.calculateMonthlyPayment(any(), any(), any())).thenReturn(monthlyPayment);

        Credit creditForResponse = creditService.calculateLoan(scoringData);

        assertNotNull(creditForResponse);

    }

    @Test
    @DisplayName("JUnit test for calculateLoan method (negative scenario)")
    void givenScoringDataWithSmallCurrentWorkExperience_whenCalculateLoan_thenThrowNotProperClientCategoryException() {

        scoringData.getEmployment().setWorkExperienceCurrent(2);

        assertThrows(NotProperClientCategoryException.class, () -> creditService.calculateLoan(scoringData));

    }


    @Test
    @DisplayName("JUnit test for calculateLoan method (positive scenario)")
    void givenScoringDataWithMinCurrentWorkExperience_whenCalculateLoan_thenReturnCredit() {

        BigDecimal preRate = new BigDecimal("12");
        BigDecimal amountWithIns = new BigDecimal("77000");
        BigDecimal monthlyPayment = new BigDecimal("3517.36");

        scoringData.getEmployment().setWorkExperienceCurrent(3);

        when(commonCalculationLoanService.preCalculateRate(any(), any())).thenReturn(preRate);
        when(commonCalculationLoanService.calculateAmount(any(), any())).thenReturn(amountWithIns);
        when(commonCalculationLoanService.calculateMonthlyPayment(any(), any(), any())).thenReturn(monthlyPayment);

        Credit creditForResponse = creditService.calculateLoan(scoringData);

        assertNotNull(creditForResponse);

    }


    @DisplayName("JUnit test for minBirthdayDateForSpecialRateIsAfterOrEqualsClientBirthday method (positive scenario)")
    @ParameterizedTest
    @ValueSource(longs = {0, 1})
    void givenClientAgeAfterOrEqualsMinAgeForSpecialRate_whenMinBirthdayDateForSpecialRateIsAfterOrEqualsClientBirthday_thenReturnTrue(long number)
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        long minAgeForSpecialRate = 35L;

        LocalDate birthday = LocalDate.now().minusYears(minAgeForSpecialRate).minusDays(number);

        Method method = CreditService.class.getDeclaredMethod(
                "minBirthdayDateForSpecialRateIsAfterOrEqualsClientBirthday", Long.class, LocalDate.class
        );
        method.setAccessible(true);

        assertTrue((Boolean) method.invoke(creditService, minAgeForSpecialRate, birthday));

    }

    @Test
    @DisplayName("JUnit test for minBirthdayDateForSpecialRateIsAfterOrEqualsClientBirthday method (negative scenario)")
    void givenClientAgeBeforeMinAgeForSpecialRate_whenMinBirthdayDateForSpecialRateIsAfterOrEqualsClientBirthday_thenReturnFalse()
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException
    {

        long minAgeForSpecialRate = 35L;

        LocalDate birthday = LocalDate.now().minusYears(minAgeForSpecialRate).plusDays(1);

        Method method = CreditService.class.getDeclaredMethod(
                "minBirthdayDateForSpecialRateIsAfterOrEqualsClientBirthday", Long.class, LocalDate.class
        );
        method.setAccessible(true);

        assertFalse((Boolean) method.invoke(creditService, minAgeForSpecialRate, birthday));

    }

    @Test
    @DisplayName("JUnit test for maxAgeForSpecialRateIsBeforeClientBirthday method (positive scenario)")
    void givenClientAgeBeforeMaxAgeForSpecialRate_whenMaxAgeForSpecialRateIsBeforeClientBirthday_thenReturnTrue()
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        long maxAgeForSpecialRate = 60L;

        LocalDate birthday = LocalDate.now().minusYears(maxAgeForSpecialRate).plusDays(1);

        Method method = CreditService.class.getDeclaredMethod(
                "maxAgeForSpecialRateIsBeforeClientBirthday", Long.class, LocalDate.class
        );
        method.setAccessible(true);

        assertTrue((Boolean) method.invoke(creditService, maxAgeForSpecialRate, birthday));

    }


    @DisplayName("JUnit test for maxAgeForSpecialRateIsBeforeClientBirthday method (positive scenario)")
    @ParameterizedTest
    @ValueSource(longs = {0, 1})
    void givenClientAgeAfterOrEqualsMaxAgeForSpecialRate_whenMaxAgeForSpecialRateIsBeforeClientBirthday_thenReturnFalse(long number)
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        long maxAgeForSpecialRate = 60L;

        LocalDate birthday = LocalDate.now().minusYears(maxAgeForSpecialRate).minusDays(number);

        Method method = CreditService.class.getDeclaredMethod(
                "maxAgeForSpecialRateIsBeforeClientBirthday", Long.class, LocalDate.class
        );
        method.setAccessible(true);

        assertFalse((Boolean) method.invoke(creditService, maxAgeForSpecialRate, birthday));

    }



    @DisplayName("JUnit test for preCalculateRate method (positive scenario)")
    @ParameterizedTest
    @CsvSource({
            "NON_BINARY, DIVORCED, BUSINESS_OWNER, TOP_MANAGER, 1, 0, 15",
            "FEMALE, NOT_MARRIED, SELF_EMPLOYED, WORKER, 0, 20, 10",
            "MALE, MARRIED, EMPLOYED, MANAGER, 2, 20, 5"
    })
    void givenParametersForGettingDiscount_whenCalculateLoan_thenReturnExpectedRate(
            String gender, String maritalStatus, String employmentStatus, String position,
            Integer dependents, long years, String expectedRate
    ) {

        BigDecimal preRate = new BigDecimal("12");
        BigDecimal amountWithIns = new BigDecimal("77000");
        BigDecimal monthlyPayment = new BigDecimal("3517.36");

        scoringData.setGender(Gender.valueOf(gender));
        scoringData.setMaritalStatus(MaritalStatus.valueOf(maritalStatus));
        scoringData.getEmployment().setEmploymentStatus(EmploymentStatus.valueOf(employmentStatus));
        scoringData.getEmployment().setPosition(Position.valueOf(position));
        scoringData.setDependentAmount(dependents);
        scoringData.setBirthdate(scoringData.getBirthdate().minusYears(years));

        when(commonCalculationLoanService.preCalculateRate(any(), any())).thenReturn(preRate);
        when(commonCalculationLoanService.calculateAmount(any(), any())).thenReturn(amountWithIns);
        when(commonCalculationLoanService.calculateMonthlyPayment(any(), any(), any())).thenReturn(monthlyPayment);

        Credit creditForResponse = creditService.calculateLoan(scoringData);

        assertEquals(new BigDecimal(expectedRate), creditForResponse.getRate());

    }

}