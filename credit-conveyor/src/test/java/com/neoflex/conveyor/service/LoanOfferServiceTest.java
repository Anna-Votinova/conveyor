package com.neoflex.conveyor.service;

import com.neoflex.conveyor.model.LoanApplication;
import com.neoflex.conveyor.model.LoanOffer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class LoanOfferServiceTest {

    @Mock
    private CommonCalculationLoanService commonCalculationLoanService;


    @InjectMocks
    private LoanOfferService loanOfferService;


    @Test
    @DisplayName("JUnit test for preCalculateLoan method (positive scenario)")
    void givenLoanApplication_whenPrecalculateLoan_thenReturnListOfLoanOffers() {

        LoanApplication loanApplication = new LoanApplication(
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

        LoanOffer plainOffer = new LoanOffer(
                1L,
                new BigDecimal("10000"),
                new BigDecimal("10441.80"),
                6,
                new BigDecimal("1740.30"),
                new BigDecimal("15"),
                false,
                false
        );

        LoanOffer offerForClient = new LoanOffer(
                2L,
                new BigDecimal("10000"),
                new BigDecimal("10412.40"),
                6,
                new BigDecimal("1735.40"),
                new BigDecimal("14"),
                false,
                true
        );

        LoanOffer offerWithIns = new LoanOffer(
                3L,
                new BigDecimal("11000"),
                new BigDecimal("11420.64"),
                6,
                new BigDecimal("1903.44"),
                new BigDecimal("13"),
                true,
                false
        );

        LoanOffer offerWithInsForClient = new LoanOffer(
                4L,
                new BigDecimal("11000"),
                new BigDecimal("11388.30"),
                6,
                new BigDecimal("1898.05"),
                new BigDecimal("12"),
                true,
                true
        );

        List<LoanOffer> expectedOfferList = List.of(plainOffer, offerForClient, offerWithIns, offerWithInsForClient);


        when(commonCalculationLoanService.preCalculateRate(any(), any())).thenReturn(
                plainOffer.getRate(), offerForClient.getRate(), offerWithIns.getRate(), offerWithInsForClient.getRate()
        );

        when(commonCalculationLoanService.calculateAmount(any(), any())).thenReturn(
                plainOffer.getRequestedAmount(), offerForClient.getRequestedAmount(),
                offerWithIns.getRequestedAmount(), offerWithInsForClient.getRequestedAmount()
        );

        when(commonCalculationLoanService.calculateMonthlyPayment(any(), any(), any())).thenReturn(
                plainOffer.getMonthlyPayment(), offerForClient.getMonthlyPayment(),
                offerWithIns.getMonthlyPayment(), offerWithInsForClient.getMonthlyPayment()
        );

        List<LoanOffer> loanOfferList = loanOfferService.preCalculateLoan(loanApplication);

        assertEquals(4, loanOfferList.size());

        assertEquals(plainOffer.getTotalAmount(), loanOfferList.get(0).getTotalAmount());
        assertEquals(offerForClient.getTotalAmount(), loanOfferList.get(1).getTotalAmount());
        assertEquals(offerWithIns.getTotalAmount(), loanOfferList.get(2).getTotalAmount());
        assertEquals(offerWithInsForClient.getTotalAmount(), loanOfferList.get(3).getTotalAmount());

        assertEquals(plainOffer.getApplicationId(), loanOfferList.get(0).getApplicationId());
        assertEquals(offerForClient.getApplicationId(), loanOfferList.get(1).getApplicationId());
        assertEquals(offerWithIns.getApplicationId(), loanOfferList.get(2).getApplicationId());
        assertEquals(offerWithInsForClient.getApplicationId(), loanOfferList.get(3).getApplicationId());

        assertArrayEquals(expectedOfferList.toArray(), loanOfferList.toArray());

        verify(commonCalculationLoanService, times(4)).preCalculateRate(any(), any());
        verify(commonCalculationLoanService, times(4)).calculateAmount(any(), any());
        verify(commonCalculationLoanService, times(4)).calculateMonthlyPayment(any(), any(), any());


    }

    @Test
    @DisplayName("JUnit test for preCalculateLoan method (negative scenario)")
    void givenEmptyLoanApplication_whenPrecalculateLoan_thenThorNullPointerException() {

        LoanApplication emptyLoanApplication = new LoanApplication();

        assertThrows(NullPointerException.class, () -> loanOfferService.preCalculateLoan(emptyLoanApplication));
    }

}