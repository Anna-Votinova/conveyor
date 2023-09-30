package com.neoflex.conveyor.service;

import com.neoflex.conveyor.exception.NotProperClientCategoryException;
import com.neoflex.conveyor.config.ApplicationConfig;
import com.neoflex.conveyor.dto.response.CreditServiceDTO;
import com.neoflex.conveyor.dto.request.EmploymentServiceDTO;
import com.neoflex.conveyor.dto.request.ScoringDataServiceDTO;
import com.neoflex.conveyor.dto.enums.EmploymentStatus;
import com.neoflex.conveyor.dto.enums.Gender;
import com.neoflex.conveyor.dto.enums.MaritalStatus;
import com.neoflex.conveyor.dto.enums.Position;
import com.neoflex.conveyor.service.utils.CalculationUtils;
import com.neoflex.conveyor.service.utils.TimeUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.validation.ValidationException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class CreditServiceTest {

    @Mock
    private CalculationUtils calculationUtils;
    @Mock
    private ApplicationConfig applicationConfig;
    @Mock
    private TimeUtil timeUtil;
    @InjectMocks
    private CreditService creditService;
    private ScoringDataServiceDTO scoringDataServiceDTO;

    @BeforeEach
    void setUp() {

        EmploymentServiceDTO employmentServiceDTO = new EmploymentServiceDTO(
                EmploymentStatus.EMPLOYED,
                "123456789012",
                new BigDecimal("70000"),
                Position.WORKER,
                144,
                110
        );

        scoringDataServiceDTO = new ScoringDataServiceDTO(
                new BigDecimal("77000.0"),
                6,
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
                employmentServiceDTO,
                "12345678901234567890",
                true,
                true
        );
    }

    private GregorianCalendar getMockDate() {
        GregorianCalendar mockDate = new GregorianCalendar();
        mockDate.set(Calendar.MONTH, Calendar.AUGUST);
        return mockDate;
    }

    @Test
    void shouldReturnExpectedDto_WhenValidScoringData() {

        CreditServiceDTO creditServiceDTO = new CreditServiceDTO(
                new BigDecimal("77000.0"),
                6,
                new BigDecimal("13172.39"),
                new BigDecimal("9"),
                new BigDecimal("5.282"),
                true,
                true,
                new ArrayList<>()
        );

        BigDecimal monthlyPayment = new BigDecimal("13172.39");
        BigDecimal globalRate = new BigDecimal("15");

        when(applicationConfig.getGlobalRate()).thenReturn(globalRate);
        when(calculationUtils.calculateMonthlyPayment(any(), any(), any())).thenReturn(monthlyPayment);
        when(timeUtil.getCurrentDate()).thenReturn(getMockDate());

        CreditServiceDTO creditServiceResponseDTO = creditService.calculateLoan(
                scoringDataServiceDTO);

        assertEquals(creditServiceDTO.getAmount(), creditServiceResponseDTO.getAmount());
        assertEquals(creditServiceDTO.getTerm(), creditServiceResponseDTO.getTerm());
        assertEquals(creditServiceDTO.getMonthlyPayment(), creditServiceResponseDTO.getMonthlyPayment());
        assertEquals(creditServiceDTO.getRate(), creditServiceResponseDTO.getRate());
        assertEquals(creditServiceDTO.getIsInsuranceEnabled(), creditServiceResponseDTO.getIsInsuranceEnabled());
        assertEquals(creditServiceDTO.getIsSalaryClient(), creditServiceResponseDTO.getIsSalaryClient());

        assertEquals(
                scoringDataServiceDTO.getTerm(), creditServiceResponseDTO.getPaymentScheduleServiceElement().size()
        );

        checkPaymentScheduleNestedTest(new BigDecimal("569.59"), new BigDecimal("12602.80"),
                new BigDecimal("64397.20"), creditServiceResponseDTO, 0);

        checkPaymentScheduleNestedTest(new BigDecimal("492.24"), new BigDecimal("12680.15"),
                new BigDecimal("51717.05"), creditServiceResponseDTO, 1);

        checkPaymentScheduleNestedTest(new BigDecimal("382.56"), new BigDecimal("12789.83"),
                new BigDecimal("38927.22"), creditServiceResponseDTO, 2);

        checkPaymentScheduleNestedTest(new BigDecimal("297.55"), new BigDecimal("12874.84"),
                new BigDecimal("26052.38"), creditServiceResponseDTO, 3);

        checkPaymentScheduleNestedTest(new BigDecimal("198.60"), new BigDecimal("12973.79"),
                new BigDecimal("13078.59"), creditServiceResponseDTO, 4);

        checkPaymentScheduleNestedTest(new BigDecimal("93.27"), new BigDecimal("13078.59"),
                new BigDecimal("00.00"), creditServiceResponseDTO, 5);

        assertEquals(creditServiceDTO.getPsk(), creditServiceResponseDTO.getPsk());

        verify(calculationUtils).calculateMonthlyPayment(any(), any(), any());
    }

    private void checkPaymentScheduleNestedTest(BigDecimal interestPayment, BigDecimal debtPayment,
                                                BigDecimal remainingDebt, CreditServiceDTO creditServiceResponseDTO,
                                                int index) {
        assertEquals(interestPayment, creditServiceResponseDTO
                    .getPaymentScheduleServiceElement()
                    .get(index)
                    .getInterestPayment());
        assertEquals(debtPayment, creditServiceResponseDTO
                    .getPaymentScheduleServiceElement()
                    .get(index)
                    .getDebtPayment());
        assertEquals(remainingDebt, creditServiceResponseDTO
                    .getPaymentScheduleServiceElement()
                    .get(index)
                    .getRemainingDebt());
    }

    @Test
    void shouldThrowNullPointerException_WhenEmptyScoringData() {

        ScoringDataServiceDTO emptyScoringDataServiceDTO = new ScoringDataServiceDTO();

        assertThrows(NullPointerException.class, () -> creditService.calculateLoan(emptyScoringDataServiceDTO));
    }

    @Test
    void shouldThrowNotProperClientCategoryException_WhenClientUnemployed() {

        scoringDataServiceDTO.getEmploymentServiceDTO().setEmploymentStatus(EmploymentStatus.UNEMPLOYED);

        assertThrows(NotProperClientCategoryException.class, () -> creditService.calculateLoan(scoringDataServiceDTO));
    }

    @Test
    void shouldThrowValidationException_WhenScoringDataWithoutEmployerINN() {

        scoringDataServiceDTO.getEmploymentServiceDTO().setEmployerINN(null);

        assertThrows(ValidationException.class, () -> creditService.calculateLoan(scoringDataServiceDTO));
    }

    @Test
    void shouldThrowValidationException_WhenScoringDataWithoutPosition() {

        scoringDataServiceDTO.getEmploymentServiceDTO().setPosition(null);

        assertThrows(ValidationException.class, () -> creditService.calculateLoan(scoringDataServiceDTO));
    }

    @Test
    void shouldThrowNotProperClientCategoryException_WhenScoringDataWithSmallSalary() {

        scoringDataServiceDTO.getEmploymentServiceDTO().setSalary(new BigDecimal("1000"));

        assertThrows(NotProperClientCategoryException.class, () -> creditService.calculateLoan(scoringDataServiceDTO));
    }

    @Test
    void shouldThrowNotProperClientCategoryException_WhenScoringDataWithMinimumClientAgeTomorrow() {

        scoringDataServiceDTO.setBirthdate(LocalDate.now().minusYears(20).plusDays(1));

        assertThrows(NotProperClientCategoryException.class, () -> creditService.calculateLoan(scoringDataServiceDTO));
    }

    @Test
    void shouldReturnExpectedDto_WhenScoringDataWithMinimumClientAgeToday() {

        BigDecimal monthlyPayment = new BigDecimal("3517.36");
        BigDecimal globalRate = new BigDecimal("15");

        scoringDataServiceDTO.setBirthdate(LocalDate.now().minusYears(20));

        when(applicationConfig.getGlobalRate()).thenReturn(globalRate);
        when(calculationUtils.calculateMonthlyPayment(any(), any(), any())).thenReturn(monthlyPayment);
        when(timeUtil.getCurrentDate()).thenReturn(getMockDate());

        CreditServiceDTO creditServiceResponseDTO = creditService.calculateLoan(
                scoringDataServiceDTO);

        assertNotNull(creditServiceResponseDTO);
    }

    @Test
    void shouldReturnExpectedDto_WhenScoringDataWithMinimumClientAgeYesterday() {

        BigDecimal monthlyPayment = new BigDecimal("3517.36");
        BigDecimal globalRate = new BigDecimal("15");

        scoringDataServiceDTO.setBirthdate(LocalDate.now().minusYears(20).minusDays(1));

        when(applicationConfig.getGlobalRate()).thenReturn(globalRate);
        when(calculationUtils.calculateMonthlyPayment(any(), any(), any())).thenReturn(monthlyPayment);
        when(timeUtil.getCurrentDate()).thenReturn(getMockDate());

        CreditServiceDTO creditServiceResponseDTO = creditService.calculateLoan(
                scoringDataServiceDTO);

        assertNotNull(creditServiceResponseDTO);
    }

    @Test
    void shouldThrowNotProperClientCategoryException_WhenScoringDataWithMaximumClientAgeYesterday() {

        scoringDataServiceDTO.setBirthdate(LocalDate.now().minusYears(60).minusDays(1));

        assertThrows(NotProperClientCategoryException.class, () -> creditService.calculateLoan(scoringDataServiceDTO));
    }

    @Test
    void shouldThrowNotProperClientCategoryException_WhenScoringDataWithMaximumClientAgeToday() {

        scoringDataServiceDTO.setBirthdate(LocalDate.now().minusYears(60));

        assertThrows(NotProperClientCategoryException.class, () -> creditService.calculateLoan(scoringDataServiceDTO));
    }

    @Test
    void shouldReturnExpectedDto_WhenScoringDataWithMaximumClientAgeTomorrow() {

        BigDecimal monthlyPayment = new BigDecimal("3517.36");
        BigDecimal globalRate = new BigDecimal("15");

        scoringDataServiceDTO.setBirthdate(LocalDate.now().minusYears(60).plusDays(1));

        when(applicationConfig.getGlobalRate()).thenReturn(globalRate);
        when(calculationUtils.calculateMonthlyPayment(any(), any(), any())).thenReturn(monthlyPayment);
        when(timeUtil.getCurrentDate()).thenReturn(getMockDate());

        CreditServiceDTO creditServiceResponseDTO = creditService.calculateLoan(
                scoringDataServiceDTO);

        assertNotNull(creditServiceResponseDTO);
    }

    @Test
    void shouldThrowNotProperClientCategoryException_WhenScoringDataWithSmallCommonWorkExperience() {

        scoringDataServiceDTO.getEmploymentServiceDTO().setWorkExperienceTotal(11);

        assertThrows(NotProperClientCategoryException.class, () -> creditService.calculateLoan(scoringDataServiceDTO));
    }

    @Test
    void shouldReturnExpectedDto_WhenScoringDataWithMinimumCommonWorkExperience() {

        BigDecimal monthlyPayment = new BigDecimal("3517.36");
        BigDecimal globalRate = new BigDecimal("15");

        scoringDataServiceDTO.getEmploymentServiceDTO().setWorkExperienceTotal(12);

        when(applicationConfig.getGlobalRate()).thenReturn(globalRate);
        when(calculationUtils.calculateMonthlyPayment(any(), any(), any())).thenReturn(monthlyPayment);
        when(timeUtil.getCurrentDate()).thenReturn(getMockDate());

        CreditServiceDTO creditServiceResponseDTO = creditService.calculateLoan(
                scoringDataServiceDTO);

        assertNotNull(creditServiceResponseDTO);
    }

    @Test
    void shouldThrowNotProperClientCategoryException_WhenScoringDataWithSmallCurrentWorkExperience() {

        scoringDataServiceDTO.getEmploymentServiceDTO().setWorkExperienceCurrent(2);

        assertThrows(NotProperClientCategoryException.class, () -> creditService.calculateLoan(scoringDataServiceDTO));
    }

    @Test
    void shouldReturnExpectedDto_WhenScoringDataWithMinimumCurrentWorkExperience() {

        BigDecimal monthlyPayment = new BigDecimal("3517.36");
        BigDecimal globalRate = new BigDecimal("15");

        scoringDataServiceDTO.getEmploymentServiceDTO().setWorkExperienceCurrent(3);

        when(applicationConfig.getGlobalRate()).thenReturn(globalRate);
        when(calculationUtils.calculateMonthlyPayment(any(), any(), any())).thenReturn(monthlyPayment);
        when(timeUtil.getCurrentDate()).thenReturn(getMockDate());

        CreditServiceDTO creditServiceResponseDTO = creditService.calculateLoan(
                scoringDataServiceDTO);

        assertNotNull(creditServiceResponseDTO);
    }

    @Test
    void shouldReturnFinalRateOne_WhenGlobalRateOne() {

        BigDecimal monthlyPayment = new BigDecimal("3517.36");
        BigDecimal globalRate = new BigDecimal("1");
        BigDecimal finalRate = new BigDecimal("1");

        scoringDataServiceDTO.getEmploymentServiceDTO().setWorkExperienceCurrent(3);

        when(applicationConfig.getGlobalRate()).thenReturn(globalRate);
        when(calculationUtils.calculateMonthlyPayment(any(), any(), any())).thenReturn(monthlyPayment);
        when(timeUtil.getCurrentDate()).thenReturn(getMockDate());

        CreditServiceDTO creditServiceResponseDTO = creditService.calculateLoan(
                scoringDataServiceDTO);

        assertEquals(finalRate, creditServiceResponseDTO.getRate());
    }

    @ParameterizedTest
    @ValueSource(longs = {0, 1})
    void shouldReturnTrue_WhenClientAgeEqualsOrMoreThanMinimumAgeForSpecialRate(long number) {

        long minAge = 35L;
        LocalDate birthday = LocalDate.now().minusYears(minAge).minusDays(number);

        assertTrue(creditService.userIsOlder(minAge, birthday));
    }

    @Test
    void shouldReturnFalse_WhenClientAgeLessThanMinimumAgeForGettingSpecialRate() {

        long minAge = 35L;
        LocalDate birthday = LocalDate.now().minusYears(minAge).plusDays(1);

        assertFalse(creditService.userIsOlder(minAge, birthday));
    }

    @Test
    void shouldReturnTrue_WhenClientAgeLessThanMaximumAgeForSpecialRate() {

        long maxAge = 60L;
        LocalDate birthday = LocalDate.now().minusYears(maxAge).plusDays(1);

        assertTrue(creditService.userIsYounger(maxAge, birthday));
    }

    @ParameterizedTest
    @ValueSource(longs = {0, 1})
    void shouldReturnFalse_WhenClientAgeEqualsOrMoreThanMaximumAgeForSpecialRate(long number) {

        long maxAgeForSpecialRate = 60L;
        LocalDate birthday = LocalDate.now().minusYears(maxAgeForSpecialRate).minusDays(number);

        assertFalse(creditService.userIsYounger(maxAgeForSpecialRate, birthday));
    }

    @ParameterizedTest
    @CsvSource({
            "NON_BINARY, DIVORCED, BUSINESS_OWNER, TOP_MANAGER, 1, 0, 15",
            "FEMALE, SINGLE, SELF_EMPLOYED, WORKER, 0, 20, 10",
            "MALE, MARRIED, EMPLOYED, MID_MANAGER, 2, 20, 5"
    })
    void shouldReturnExpectedRate_WhenGivenParametersForGettingDiscount(
            String gender, String maritalStatus, String employmentStatus, String position,
            Integer dependents, long years, String expectedRate
    ) {

        BigDecimal monthlyPayment = new BigDecimal("3517.36");
        BigDecimal globalRate = new BigDecimal("15");

        scoringDataServiceDTO.setGender(Gender.valueOf(gender));
        scoringDataServiceDTO.setMaritalStatus(MaritalStatus.valueOf(maritalStatus));
        scoringDataServiceDTO.getEmploymentServiceDTO().setEmploymentStatus(EmploymentStatus.valueOf(employmentStatus));
        scoringDataServiceDTO.getEmploymentServiceDTO().setPosition(Position.valueOf(position));
        scoringDataServiceDTO.setDependentAmount(dependents);
        scoringDataServiceDTO.setBirthdate(scoringDataServiceDTO.getBirthdate().minusYears(years));

        when(applicationConfig.getGlobalRate()).thenReturn(globalRate);
        when(calculationUtils.calculateMonthlyPayment(any(), any(), any())).thenReturn(monthlyPayment);
        when(timeUtil.getCurrentDate()).thenReturn(getMockDate());

        CreditServiceDTO creditServiceResponseDTO = creditService.calculateLoan(
                scoringDataServiceDTO);

        assertEquals(new BigDecimal(expectedRate), creditServiceResponseDTO.getRate());
    }
}