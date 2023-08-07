package com.neoflex.conveyor.service;

import com.neoflex.conveyor.config.ApplicationConfig;
import com.neoflex.conveyor.dto.request.LoanApplicationServiceDTO;
import com.neoflex.conveyor.dto.response.LoanOfferServiceDTO;
import com.neoflex.conveyor.service.utils.CalculationUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class LoanOfferServiceTest {

    @Mock
    private CalculationUtils calculationUtils;

    @Mock
    private ApplicationConfig applicationConfig;

    @InjectMocks
    private LoanOfferService loanOfferService;

    private LoanApplicationServiceDTO loanApplicationServiceDTO;

    @BeforeEach
    void setUp() {
        loanApplicationServiceDTO = new LoanApplicationServiceDTO(
                new BigDecimal("10000"),
                6,
                "Anna",
                "Black",
                "White",
                "anyvotinova@yandex.ru",
                LocalDate.of(1990, 12, 13),
                "1111",
                "111111"
        );
    }

    @Test
    void shouldReturnLoanOffersList_WhenValidLoanApplication() {

        List<LoanOfferServiceDTO> expectedOffersList = getExpectedOffers();
        BigDecimal globalRate = new BigDecimal("15");

        when(applicationConfig.getGlobalRate()).thenReturn(globalRate);
        when(calculationUtils.calculateMonthlyPayment(any(), any(), any())).thenReturn(
                expectedOffersList.get(0).getMonthlyPayment(), expectedOffersList.get(1).getMonthlyPayment(),
                expectedOffersList.get(2).getMonthlyPayment(), expectedOffersList.get(3).getMonthlyPayment()
        );

        List<LoanOfferServiceDTO> loanOfferServiceDTOList = loanOfferService.preCalculateLoan(
                loanApplicationServiceDTO);

        for (int i = 0; i < 4; i++) {

            assertNotNull(loanOfferServiceDTOList.get(i).getApplicationId());
            assertEquals(expectedOffersList.get(i).getRequestedAmount(), loanOfferServiceDTOList.get(i).getRequestedAmount());
            assertEquals(expectedOffersList.get(i).getTotalAmount(), loanOfferServiceDTOList.get(i).getTotalAmount());
            assertEquals(expectedOffersList.get(i).getTerm(), loanOfferServiceDTOList.get(i).getTerm());
            assertEquals(expectedOffersList.get(i).getMonthlyPayment(), loanOfferServiceDTOList.get(i).getMonthlyPayment());
            assertEquals(expectedOffersList.get(i).getRate(), loanOfferServiceDTOList.get(i).getRate());
            assertEquals(expectedOffersList.get(i).getIsInsuranceEnabled(), loanOfferServiceDTOList.get(i).getIsInsuranceEnabled());
            assertEquals(expectedOffersList.get(i).getIsSalaryClient(), loanOfferServiceDTOList.get(i).getIsSalaryClient());

        }

        verify(calculationUtils, times(4)).calculateMonthlyPayment(any(), any(), any());

    }

    @Test
    void shouldReturnNotExpectedLoanOffersList_WhenSortOrderChanged() {

        List<LoanOfferServiceDTO> expectedOffersList = getExpectedOffers();
        BigDecimal globalRate = new BigDecimal("15");

        when(applicationConfig.getGlobalRate()).thenReturn(globalRate);
        when(calculationUtils.calculateMonthlyPayment(any(), any(), any())).thenReturn(
                expectedOffersList.get(0).getMonthlyPayment(), expectedOffersList.get(1).getMonthlyPayment(),
                expectedOffersList.get(2).getMonthlyPayment(), expectedOffersList.get(3).getMonthlyPayment()
        );

        List<LoanOfferServiceDTO> loanOfferServiceDTOList = loanOfferService.preCalculateLoan(
                loanApplicationServiceDTO).stream().sorted(Comparator.comparing(LoanOfferServiceDTO::getRate))
                                                                            .toList();

        for (int i = 0; i < 4; i++) {

            assertNotEquals(expectedOffersList.get(i).getRequestedAmount(), loanOfferServiceDTOList.get(i).getRequestedAmount());
            assertNotEquals(expectedOffersList.get(i).getTotalAmount(), loanOfferServiceDTOList.get(i).getTotalAmount());
            assertNotEquals(expectedOffersList.get(i).getMonthlyPayment(), loanOfferServiceDTOList.get(i).getMonthlyPayment());
            assertNotEquals(expectedOffersList.get(i).getRate(), loanOfferServiceDTOList.get(i).getRate());
            assertNotEquals(expectedOffersList.get(i).getIsInsuranceEnabled(), loanOfferServiceDTOList.get(i).getIsInsuranceEnabled());
            assertNotEquals(expectedOffersList.get(i).getIsSalaryClient(), loanOfferServiceDTOList.get(i).getIsSalaryClient());

        }

    }

    @Test
    void shouldThrowNullPointerException_WhenLoanApplicationEmpty() {

        LoanApplicationServiceDTO emptyLoanApplicationServiceDTO = new LoanApplicationServiceDTO();

        assertThrows(NullPointerException.class, () -> loanOfferService.preCalculateLoan(emptyLoanApplicationServiceDTO));

    }

    private List<LoanOfferServiceDTO> getExpectedOffers() {

        LoanOfferServiceDTO plainOffer = new LoanOfferServiceDTO(
                1L,
                new BigDecimal("10000"),
                new BigDecimal("10441.80"),
                6,
                new BigDecimal("1740.30"),
                new BigDecimal("15"),
                false,
                false
        );

        LoanOfferServiceDTO clientOffer = new LoanOfferServiceDTO(
                2L,
                new BigDecimal("10000"),
                new BigDecimal("10412.40"),
                6,
                new BigDecimal("1735.40"),
                new BigDecimal("14"),
                false,
                true
        );

        LoanOfferServiceDTO offerWithInsurance = new LoanOfferServiceDTO(
                3L,
                new BigDecimal("11000.0"),
                new BigDecimal("11420.64"),
                6,
                new BigDecimal("1903.44"),
                new BigDecimal("13"),
                true,
                false
        );

        LoanOfferServiceDTO clientOfferWithInsurance = new LoanOfferServiceDTO(
                4L,
                new BigDecimal("11000.0"),
                new BigDecimal("11388.30"),
                6,
                new BigDecimal("1898.05"),
                new BigDecimal("12"),
                true,
                true
        );

        return Stream.of(plainOffer, clientOffer, offerWithInsurance, clientOfferWithInsurance)
                     .sorted(Comparator.comparing(LoanOfferServiceDTO::getRate).reversed())
                     .toList();

    }

}