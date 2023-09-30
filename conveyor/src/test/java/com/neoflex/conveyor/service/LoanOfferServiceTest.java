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
                1L,
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

        List<LoanOfferServiceDTO> expectedOffers = getExpectedOffers();
        BigDecimal globalRate = new BigDecimal("15");

        when(applicationConfig.getGlobalRate()).thenReturn(globalRate);
        when(calculationUtils.calculateMonthlyPayment(any(), any(), any())).thenReturn(
                expectedOffers.get(0).getMonthlyPayment(), expectedOffers.get(1).getMonthlyPayment(),
                expectedOffers.get(2).getMonthlyPayment(), expectedOffers.get(3).getMonthlyPayment()
        );

        List<LoanOfferServiceDTO> receivedOffers = loanOfferService.preCalculateLoan(
                loanApplicationServiceDTO);

        for (int i = 0; i < 4; i++) {

            assertNotNull(receivedOffers.get(i).getApplicationId());
            assertEquals(expectedOffers.get(i).getRequestedAmount(), receivedOffers.get(i).getRequestedAmount());
            assertEquals(expectedOffers.get(i).getTotalAmount(), receivedOffers.get(i).getTotalAmount());
            assertEquals(expectedOffers.get(i).getTerm(), receivedOffers.get(i).getTerm());
            assertEquals(expectedOffers.get(i).getMonthlyPayment(), receivedOffers.get(i).getMonthlyPayment());
            assertEquals(expectedOffers.get(i).getRate(), receivedOffers.get(i).getRate());
            assertEquals(expectedOffers.get(i).getIsInsuranceEnabled(), receivedOffers.get(i).getIsInsuranceEnabled());
            assertEquals(expectedOffers.get(i).getIsSalaryClient(), receivedOffers.get(i).getIsSalaryClient());

        }

        verify(calculationUtils, times(4)).calculateMonthlyPayment(any(), any(), any());
    }

    @Test
    void shouldReturnNotExpectedLoanOffersList_WhenSortOrderChanged() {

        List<LoanOfferServiceDTO> expectedOffers = getExpectedOffers();
        BigDecimal globalRate = new BigDecimal("15");

        when(applicationConfig.getGlobalRate()).thenReturn(globalRate);
        when(calculationUtils.calculateMonthlyPayment(any(), any(), any())).thenReturn(
                expectedOffers.get(0).getMonthlyPayment(), expectedOffers.get(1).getMonthlyPayment(),
                expectedOffers.get(2).getMonthlyPayment(), expectedOffers.get(3).getMonthlyPayment()
        );

        List<LoanOfferServiceDTO> receivedOffers = loanOfferService.preCalculateLoan(
                loanApplicationServiceDTO).stream().sorted(Comparator.comparing(LoanOfferServiceDTO::getRate))
                                                                            .toList();

        for (int i = 0; i < 4; i++) {

            assertNotEquals(expectedOffers.get(i).getRequestedAmount(), receivedOffers.get(i).getRequestedAmount());
            assertNotEquals(expectedOffers.get(i).getTotalAmount(), receivedOffers.get(i).getTotalAmount());
            assertNotEquals(expectedOffers.get(i).getMonthlyPayment(), receivedOffers.get(i).getMonthlyPayment());
            assertNotEquals(expectedOffers.get(i).getRate(), receivedOffers.get(i).getRate());
            assertNotEquals(expectedOffers.get(i).getIsSalaryClient(), receivedOffers.get(i).getIsSalaryClient());
            assertNotEquals(
                    expectedOffers.get(i).getIsInsuranceEnabled(), receivedOffers.get(i).getIsInsuranceEnabled()
            );
        }
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
                1L,
                new BigDecimal("10000"),
                new BigDecimal("10412.40"),
                6,
                new BigDecimal("1735.40"),
                new BigDecimal("14"),
                false,
                true
        );

        LoanOfferServiceDTO offerWithInsurance = new LoanOfferServiceDTO(
                1L,
                new BigDecimal("11000.0"),
                new BigDecimal("11420.64"),
                6,
                new BigDecimal("1903.44"),
                new BigDecimal("13"),
                true,
                false
        );

        LoanOfferServiceDTO clientOfferWithInsurance = new LoanOfferServiceDTO(
                1L,
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