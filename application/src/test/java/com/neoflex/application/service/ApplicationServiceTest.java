package com.neoflex.application.service;

import com.neoflex.application.dto.LoanApplicationRequestDTO;
import com.neoflex.application.dto.LoanOfferDTO;
import com.neoflex.application.exception.BadRequestException;
import com.neoflex.application.exception.PreScoringException;
import com.neoflex.application.integration.deal.DealClient;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class ApplicationServiceTest {

    @Mock
    private DealClient dealClient;
    @InjectMocks
    private ApplicationService applicationService;

    @Test
    void shouldSendClientInfo_WhenValidInput() {
        var loanApplicationRequestDTO = getClientInfo();
        var loanOffer = LoanOfferDTO.builder().build();
        var loanOffers = List.of(loanOffer, loanOffer, loanOffer, loanOffer);

        when(dealClient.calculateOffers(loanApplicationRequestDTO)).thenReturn(loanOffers);

        var response = applicationService.preScoreOffers(loanApplicationRequestDTO);

        assertThat(response).hasSameSizeAs(loanOffers);
        verify(dealClient).calculateOffers(any());
    }

    @Test
    void shouldThrowException_WhenDealGenerateValidException() {
        var loanApplicationRequestDTO = getClientInfo();
        when(dealClient.calculateOffers(any())).thenThrow(BadRequestException.class);

        assertThatExceptionOfType(BadRequestException.class)
                .isThrownBy(() -> applicationService.preScoreOffers(loanApplicationRequestDTO));
    }

    @Test
    void shouldThrowException_WhenInvalidAmount() {
        var loanApplicationRequestDTO = new LoanApplicationRequestDTO(
                null,
                new BigDecimal("9999.0"),
                6,
                "Anna",
                "Black",
                "White",
                "black@yandex.ru",
                LocalDate.of(1990, 12, 13),
                "1111",
                "111111"
        );

        assertThatExceptionOfType(PreScoringException.class)
                .isThrownBy(() -> applicationService.preScoreOffers(loanApplicationRequestDTO));
    }

    @Test
    void shouldThrowException_WhenInvalidTerm() {
        var loanApplicationRequestDTO = new LoanApplicationRequestDTO(
                null,
                new BigDecimal("10000.0"),
                5,
                "Anna",
                "Black",
                "White",
                "black@yandex.ru",
                LocalDate.of(1990, 12, 13),
                "1111",
                "111111"
        );

        assertThatExceptionOfType(PreScoringException.class)
                .isThrownBy(() -> applicationService.preScoreOffers(loanApplicationRequestDTO));
    }

    @Test
    void shouldThrowException_WhenInvalidFirstname() {
        var loanApplicationRequestDTO = new LoanApplicationRequestDTO(
                null,
                new BigDecimal("10000.0"),
                6,
                "A",
                "Black",
                "White",
                "black@yandex.ru",
                LocalDate.of(1990, 12, 13),
                "1111",
                "111111"
        );

        assertThatExceptionOfType(PreScoringException.class)
                .isThrownBy(() -> applicationService.preScoreOffers(loanApplicationRequestDTO));
    }

    @Test
    void shouldThrowException_WhenInvalidLastname() {
        var loanApplicationRequestDTO = new LoanApplicationRequestDTO(
                null,
                new BigDecimal("10000.0"),
                6,
                "Anna",
                "Пономарева",
                "White",
                "black@yandex.ru",
                LocalDate.of(1990, 12, 13),
                "1111",
                "111111"
        );

        assertThatExceptionOfType(PreScoringException.class)
                .isThrownBy(() -> applicationService.preScoreOffers(loanApplicationRequestDTO));
    }

    @Test
    void shouldThrowException_WhenInvalidMiddleName() {
        var loanApplicationRequestDTO = new LoanApplicationRequestDTO(
                null,
                new BigDecimal("10000.0"),
                6,
                "Anna",
                "Black",
                "ItIsAVeryLongMiddleNameForTestingConstraint",
                "black@yandex.ru",
                LocalDate.of(1990, 12, 13),
                "1111",
                "111111"
        );

        assertThatExceptionOfType(PreScoringException.class)
                .isThrownBy(() -> applicationService.preScoreOffers(loanApplicationRequestDTO));
    }

    @Test
    void shouldThrowException_WhenInvalidEmail() {
        var loanApplicationRequestDTO = new LoanApplicationRequestDTO(
                null,
                new BigDecimal("10000.0"),
                6,
                "Anna",
                "Black",
                "White",
                "@yandex.ru",
                LocalDate.of(1990, 12, 13),
                "1111",
                "111111"
        );

        assertThatExceptionOfType(PreScoringException.class)
                .isThrownBy(() -> applicationService.preScoreOffers(loanApplicationRequestDTO));
    }

    @Test
    void shouldThrowException_WhenInvalidBirthday() {
        var loanApplicationRequestDTO = new LoanApplicationRequestDTO(
                null,
                new BigDecimal("10000.0"),
                6,
                "Anna",
                "Black",
                "White",
                "black@yandex.ru",
                LocalDate.now().minusYears(18).plusDays(1),
                "1111",
                "111111"
        );

        assertThatExceptionOfType(PreScoringException.class)
                .isThrownBy(() -> applicationService.preScoreOffers(loanApplicationRequestDTO));
    }

    @Test
    void shouldThrowException_WhenInvalidPassportSeries() {
        var loanApplicationRequestDTO = new LoanApplicationRequestDTO(
                null,
                new BigDecimal("10000.0"),
                6,
                "Anna",
                "Black",
                "White",
                "black@yandex.ru",
                LocalDate.of(1990, 12, 13),
                "11112",
                "111111"
        );

        assertThatExceptionOfType(PreScoringException.class)
                .isThrownBy(() -> applicationService.preScoreOffers(loanApplicationRequestDTO));
    }

    @Test
    void shouldThrowException_WhenInvalidPassportNumber() {
        var loanApplicationRequestDTO = new LoanApplicationRequestDTO(
                null,
                new BigDecimal("10000.0"),
                6,
                "Anna",
                "Black",
                "White",
                "black@yandex.ru",
                LocalDate.of(1990, 12, 13),
                "1111",
                "11111"
        );

        assertThatExceptionOfType(PreScoringException.class)
                .isThrownBy(() -> applicationService.preScoreOffers(loanApplicationRequestDTO));
    }

    @Test
    void shouldSendLoanOffer_WhenValidInput() {
        var loanOffer = LoanOfferDTO.builder().build();

        applicationService.chooseOffer(loanOffer);

        verify(dealClient).chooseOffer(any());
    }

    private LoanApplicationRequestDTO getClientInfo() {
        return new LoanApplicationRequestDTO(
                null,
                new BigDecimal("10000.0"),
                6,
                "Anna",
                "Black",
                "White",
                "black@yandex.ru",
                LocalDate.of(1990, 12, 13),
                "1111",
                "111111"
        );
    }
}