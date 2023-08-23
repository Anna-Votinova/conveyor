package com.neoflex.application.service;

import com.neoflex.application.dto.LoanApplicationRequestDTO;
import com.neoflex.application.dto.LoanOfferDTO;
import com.neoflex.application.exception.BadRequestException;
import com.neoflex.application.integration.deal.DealClient;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
        var loanApplicationRequestDTO = LoanApplicationRequestDTO.builder().build();
        var loanOffers = List.of(
                LoanOfferDTO.builder().build(),
                LoanOfferDTO.builder().build(),
                LoanOfferDTO.builder().build(),
                LoanOfferDTO.builder().build());

        when(dealClient.calculateOffers(loanApplicationRequestDTO)).thenReturn(loanOffers);

        var response = applicationService.prepareOffers(loanApplicationRequestDTO);

        assertThat(response).hasSameSizeAs(loanOffers);
        verify(dealClient).calculateOffers(any());
    }

    @Test
    void shouldThrowException_WhenDealGenerateValidException() {
        var loanApplicationRequestDTO = LoanApplicationRequestDTO.builder().build();
        when(dealClient.calculateOffers(any())).thenThrow(BadRequestException.class);

        assertThatExceptionOfType(BadRequestException.class)
                .isThrownBy(() -> applicationService.prepareOffers(loanApplicationRequestDTO));
    }

    @Test
    void shouldSendLoanOffer_WhenValidInput() {
        var loanOffer = LoanOfferDTO.builder().build();

        applicationService.chooseOffer(loanOffer);

        verify(dealClient).chooseOffer(any());
    }
}