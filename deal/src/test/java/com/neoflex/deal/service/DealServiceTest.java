package com.neoflex.deal.service;

import com.neoflex.deal.entity.Application;
import com.neoflex.deal.entity.Client;
import com.neoflex.deal.entity.Credit;
import com.neoflex.deal.entity.Employment;
import com.neoflex.deal.entity.dto.request_responce.LoanOfferDTO;
import com.neoflex.deal.entity.dto.request.CreditDTO;
import com.neoflex.deal.entity.dto.request.EmploymentDTO;
import com.neoflex.deal.entity.dto.request.FinishRegistrationRequestDTO;
import com.neoflex.deal.entity.dto.request_responce.LoanApplicationRequestDTO;
import com.neoflex.deal.entity.dto.response.ScoringDataDTO;
import com.neoflex.deal.entity.jsonb.element.AppliedOffer;
import com.neoflex.deal.entity.mapper.ClientMapper;
import com.neoflex.deal.entity.mapper.CreditMapper;
import com.neoflex.deal.entity.mapper.EmploymentMapper;
import com.neoflex.deal.entity.mapper.OfferMapper;
import com.neoflex.deal.entity.mapper.ScoringDataMapper;
import com.neoflex.deal.exception.ApplicationNotFoundException;
import com.neoflex.deal.repository.ApplicationRepository;
import com.neoflex.deal.repository.ClientRepository;
import com.neoflex.deal.repository.CreditRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class DealServiceTest {

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private ApplicationRepository applicationRepository;

    @Mock
    private CreditRepository creditRepository;

    @Mock
    private EmploymentMapper employmentMapper;

    @Mock
    private ClientMapper clientMapper;

    @Mock
    private OfferMapper offerMapper;

    @Mock
    private ScoringDataMapper scoringDataMapper;

    @Mock
    private CreditMapper creditMapper;

    @InjectMocks
    private DealService dealService;

    @AfterEach
    public void verifyInteractions() {
        verifyNoMoreInteractions(
                clientRepository,
                applicationRepository,
                creditRepository
        );
    }

    @Test
    void shouldReturnDto_WhenValidApplication() {
        LoanApplicationRequestDTO requestDTO = new LoanApplicationRequestDTO();
        Client client = new Client();
        Application application = Application.builder()
                .id(1L).build();

        when(clientMapper.toClientShort(requestDTO)).thenReturn(client);
        when(clientRepository.save(client)).thenReturn(client);
        when(applicationRepository.save(any())).thenReturn(application);

        LoanApplicationRequestDTO responseDto = dealService.startRegistration(requestDTO);

        assertNotNull(responseDto.getId());
        assertEquals(application.getId(), responseDto.getId());

        verify(clientRepository, times(1))
                .save(any());
        verify(applicationRepository, times(1))
                .save(any());

    }

    @Test
    void shouldSaveOffer_WhenValidApplication() {
        LoanOfferDTO requestDto = LoanOfferDTO.builder().applicationId(1L).build();
        Application application = new Application();
        AppliedOffer appliedOffer = new AppliedOffer();

        when(applicationRepository.findById(anyLong())).thenReturn(Optional.of(application));
        when(offerMapper.toAppliedOffer(requestDto)).thenReturn(appliedOffer);
        when(applicationRepository.save(any())).thenReturn(application);

        dealService.chooseOffer(requestDto);

        verify(applicationRepository, times(1))
                .findById(anyLong());
        verify(applicationRepository, times(1))
                .save(any());

    }

    @Test
    void shouldThrowException_WhenInvalidApplicationId() {
        LoanOfferDTO requestDto = LoanOfferDTO.builder().applicationId(-1L).build();

        when(applicationRepository.findById(anyLong())).thenThrow(ApplicationNotFoundException.class);

        assertThrows(ApplicationNotFoundException.class, () -> dealService.chooseOffer(requestDto));

        verify(applicationRepository, times(1))
                .findById(anyLong());

    }

    @Test
    void shouldFinishRegistration_WhenValidApplicationIdAndAdditionalClientInfo() {
        Application application = new Application();
        FinishRegistrationRequestDTO clientInfo = FinishRegistrationRequestDTO.builder()
                                                                              .employment(new EmploymentDTO()).build();
        ScoringDataDTO scoringDataDTO = new ScoringDataDTO();
        Employment employment = new Employment();
        Client client = new Client();
        Long applicationId = 1L;

        when(applicationRepository.findById(anyLong())).thenReturn(Optional.of(application));
        when(scoringDataMapper.toScoringDataDTO(application, clientInfo)).thenReturn(scoringDataDTO);
        when(employmentMapper.toEmployment(any())).thenReturn(employment);
        when(clientMapper.toClientFull(any(), any(), any())).thenReturn(client);
        when(clientRepository.save(any())).thenReturn(client);

        ScoringDataDTO responseDto = dealService.finishRegistration(clientInfo, applicationId);

        assertEquals(scoringDataDTO, responseDto);

        verify(applicationRepository, times(1))
                .findById(anyLong());
        verify(clientRepository, times(1))
                .save(any());

    }

    @Test
    void shouldThrowException_WhenFinishRegistrationWithInvalidApplicationId() {
        Long applicationId = -1L;
        FinishRegistrationRequestDTO clientInfo = new FinishRegistrationRequestDTO();

        when(applicationRepository.findById(anyLong())).thenThrow(ApplicationNotFoundException.class);

        assertThrows(ApplicationNotFoundException.class,
                () -> dealService.finishRegistration(clientInfo, applicationId));

        verify(applicationRepository, times(1))
                .findById(anyLong());

    }

    @Test
    void shouldSaveCredit_WhenValidInput() {
        Application application = new Application();
        Credit credit = new Credit();
        CreditDTO creditDTO = new CreditDTO();
        Long applicationId = 1L;

        when(applicationRepository.findById(anyLong())).thenReturn(Optional.of(application));
        when(creditMapper.toCredit(creditDTO)).thenReturn(credit);
        when(creditRepository.save(any())).thenReturn(credit);
        when(applicationRepository.save(any())).thenReturn(application);

        dealService.saveCredit(creditDTO, applicationId);

        verify(applicationRepository, times(1))
                .findById(anyLong());
        verify(applicationRepository, times(1))
                .save(any());
        verify(creditRepository, times(1))
                .save(any());

    }

    @Test
    void shouldThrowException_WhenInvalidValidInput() {
        CreditDTO creditDTO = new CreditDTO();
        Long applicationId = -1L;

        when(applicationRepository.findById(anyLong())).thenThrow(ApplicationNotFoundException.class);

        assertThrows(ApplicationNotFoundException.class,
                () -> dealService.saveCredit(creditDTO, applicationId));

        verify(applicationRepository, times(1))
                .findById(anyLong());

    }

}