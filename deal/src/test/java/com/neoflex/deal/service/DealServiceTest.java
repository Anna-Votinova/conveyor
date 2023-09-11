package com.neoflex.deal.service;

import com.neoflex.deal.entity.Application;
import com.neoflex.deal.entity.Client;
import com.neoflex.deal.entity.Credit;
import com.neoflex.deal.entity.Employment;
import com.neoflex.deal.dto.LoanOfferDTO;
import com.neoflex.deal.dto.request.CreditDTO;
import com.neoflex.deal.dto.request.EmploymentDTO;
import com.neoflex.deal.dto.request.FinishRegistrationRequestDTO;
import com.neoflex.deal.dto.LoanApplicationRequestDTO;
import com.neoflex.deal.dto.response.ScoringDataDTO;
import com.neoflex.deal.entity.jsonb.element.AppliedOffer;
import com.neoflex.deal.entity.jsonb.element.PaymentScheduleElement;
import com.neoflex.deal.entity.mapper.ClientMapper;
import com.neoflex.deal.entity.mapper.CreditMapper;
import com.neoflex.deal.entity.mapper.EmploymentMapper;
import com.neoflex.deal.entity.mapper.OfferMapper;
import com.neoflex.deal.entity.mapper.ScoringDataMapper;
import com.neoflex.deal.exception.ApplicationNotFoundException;
import com.neoflex.deal.exception.BadRequestConveyorException;
import com.neoflex.deal.exception.ScoringConveyorException;
import com.neoflex.deal.integration.conveyor.ConveyorClient;
import com.neoflex.deal.integration.dossier.kafka.DossierKafkaConfig;
import com.neoflex.deal.integration.dossier.kafka.EmailMessageProducer;
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

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class DealServiceTest {

    @Mock
    private EmailMessageProducer emailMessageProducer;
    @Mock
    private DossierKafkaConfig dossierKafkaConfig;
    @Mock
    private ConveyorClient conveyorClient;
    @Mock
    private ChangeStatusHistoryService changeStatusHistoryService;
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
    void shouldSaveApplication_WhenValidInput() {
        LoanApplicationRequestDTO requestDTO = new LoanApplicationRequestDTO();
        Client client = new Client();
        Application application = Application.builder()
                .id(1L).build();

        when(clientMapper.toClientShort(requestDTO)).thenReturn(client);
        when(applicationRepository.save(any())).thenReturn(application);
        when(changeStatusHistoryService.changeStatusHistory(any(), any())).thenReturn(application);
        when(conveyorClient.preCalculateLoan(any())).thenReturn(Collections.emptyList());

        dealService.startRegistration(requestDTO);

        verify(applicationRepository).save(any());
        verify(conveyorClient).preCalculateLoan(any());
    }

    @Test
    void shouldThrowException_WhenConveyorGeneratesException() {
        LoanApplicationRequestDTO requestDTO = new LoanApplicationRequestDTO();
        Client client = new Client();
        Application application = Application.builder()
                                             .id(1L).build();

        when(clientMapper.toClientShort(requestDTO)).thenReturn(client);
        when(applicationRepository.save(any())).thenReturn(application);
        when(changeStatusHistoryService.changeStatusHistory(any(), any())).thenReturn(application);
        when(conveyorClient.preCalculateLoan(any())).thenThrow(BadRequestConveyorException.class);

        assertThrows(BadRequestConveyorException.class,
                () -> dealService.startRegistration(requestDTO));
    }

    @Test
    void shouldSaveOffer_WhenValidApplicationId() {
        LoanOfferDTO requestDto = LoanOfferDTO.builder()
                 .applicationId(1L).build();
        Client client = Client.builder()
                .email("email@email.com").build();
        Application application = Application.builder()
                .client(client).build();
        AppliedOffer appliedOffer = new AppliedOffer();

        when(applicationRepository.findById(anyLong())).thenReturn(Optional.of(application));
        when(offerMapper.toAppliedOffer(requestDto)).thenReturn(appliedOffer);
        when(changeStatusHistoryService.changeStatusHistory(any(), any())).thenReturn(application);
        when(applicationRepository.save(any())).thenReturn(application);
        when(dossierKafkaConfig.getFinishRegistrationTopic()).thenReturn(any());

        dealService.chooseOffer(requestDto);

        verify(applicationRepository).findById(anyLong());
        verify(applicationRepository).save(any());
        verify(emailMessageProducer).sendEmailMessage(any(), any());
    }

    @Test
    void shouldThrowException_WhenInvalidApplicationId() {
        LoanOfferDTO requestDto = LoanOfferDTO.builder().applicationId(-1L).build();

        when(applicationRepository.findById(anyLong())).thenThrow(ApplicationNotFoundException.class);

        assertThrows(ApplicationNotFoundException.class, () -> dealService.chooseOffer(requestDto));
    }

    @Test
    void shouldFinishRegistration_WhenValidApplicationIdAndAdditionalClientInfo() {
        Application application = new Application();
        FinishRegistrationRequestDTO clientInfo = FinishRegistrationRequestDTO.builder()
                .employment(new EmploymentDTO()).build();
        ScoringDataDTO scoringDataDTO = new ScoringDataDTO();
        Employment employment = new Employment();
        Client client = new Client();
        Credit credit = new Credit();
        CreditDTO creditDTO = new CreditDTO();
        creditDTO.setPaymentSchedule(List.of(new PaymentScheduleElement()));
        Long applicationId = 1L;

        when(applicationRepository.findById(anyLong())).thenReturn(Optional.of(application));
        when(scoringDataMapper.toScoringDataDTO(application, clientInfo)).thenReturn(scoringDataDTO);
        when(employmentMapper.toEmployment(any())).thenReturn(employment);
        when(clientMapper.fillAdditionalClientInfo(any(), any(), any())).thenReturn(client);
        when(clientRepository.save(any())).thenReturn(client);
        when(conveyorClient.calculateLoan(any())).thenReturn(creditDTO);
        when(creditMapper.toCredit(creditDTO)).thenReturn(credit);
        when(changeStatusHistoryService.changeStatusHistory(any(), any())).thenReturn(application);
        when(applicationRepository.save(any())).thenReturn(application);
        when(dossierKafkaConfig.getCreateDocumentsTopic()).thenReturn(any());

        dealService.finishRegistration(clientInfo, applicationId);

        verify(applicationRepository).findById(anyLong());
        verify(clientRepository).save(any());
        verify(conveyorClient).calculateLoan(any());
        verify(applicationRepository).save(any());
        verify(emailMessageProducer).sendEmailMessage(any(), any());
    }

    @Test
    void shouldApplicationDenied_WhenConveyorGeneratesScoringException() {
        Application application = new Application();
        FinishRegistrationRequestDTO clientInfo = FinishRegistrationRequestDTO.builder()
                                                                              .employment(new EmploymentDTO()).build();
        ScoringDataDTO scoringDataDTO = new ScoringDataDTO();
        Employment employment = new Employment();
        Client client = new Client();
        CreditDTO creditDTO = new CreditDTO();
        creditDTO.setPaymentSchedule(List.of(new PaymentScheduleElement()));
        Long applicationId = 1L;

        when(applicationRepository.findById(anyLong())).thenReturn(Optional.of(application));
        when(scoringDataMapper.toScoringDataDTO(application, clientInfo)).thenReturn(scoringDataDTO);
        when(employmentMapper.toEmployment(any())).thenReturn(employment);
        when(clientMapper.fillAdditionalClientInfo(any(), any(), any())).thenReturn(client);
        when(clientRepository.save(any())).thenReturn(client);
        when(conveyorClient.calculateLoan(any())).thenThrow(ScoringConveyorException.class);
        when(changeStatusHistoryService.changeStatusHistory(any(), any())).thenReturn(application);
        when(applicationRepository.save(any())).thenReturn(application);
        when(dossierKafkaConfig.getApplicationDeniedTopic()).thenReturn(any());

        dealService.finishRegistration(clientInfo, applicationId);

        verify(applicationRepository).findById(anyLong());
        verify(clientRepository).save(any());
        verify(conveyorClient).calculateLoan(any());
        verify(applicationRepository).save(any());
        verify(emailMessageProducer).sendEmailMessage(any(), any());
    }

    @Test
    void shouldThrowException_WhenFinishRegistrationWithInvalidApplicationId() {
        Long applicationId = -1L;
        FinishRegistrationRequestDTO clientInfo = new FinishRegistrationRequestDTO();

        when(applicationRepository.findById(anyLong())).thenThrow(ApplicationNotFoundException.class);

        assertThrows(ApplicationNotFoundException.class,
                () -> dealService.finishRegistration(clientInfo, applicationId));
    }

    @Test
    void shouldThrowException_WhenConveyorGeneratesValidException() {
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
        when(clientMapper.fillAdditionalClientInfo(any(), any(), any())).thenReturn(client);
        when(clientRepository.save(any())).thenReturn(client);
        when(conveyorClient.calculateLoan(any())).thenThrow(BadRequestConveyorException.class);

        assertThrows(BadRequestConveyorException.class,
                () -> dealService.finishRegistration(clientInfo, applicationId));
    }
}