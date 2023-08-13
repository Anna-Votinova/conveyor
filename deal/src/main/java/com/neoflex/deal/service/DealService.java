package com.neoflex.deal.service;

import com.neoflex.deal.entity.dto.LoanOfferDTO;
import com.neoflex.deal.entity.dto.request.FinishRegistrationRequestDTO;
import com.neoflex.deal.entity.dto.request.LoanApplicationRequestDTO;
import com.neoflex.deal.exception.NotCompletedComponentImplementation;
import com.neoflex.deal.repository.ApplicationRepository;
import com.neoflex.deal.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class DealService {

    private final ClientRepository clientRepository;

    private final ApplicationRepository applicationRepository;

    public LoanApplicationRequestDTO startRegistration(LoanApplicationRequestDTO requestDTO) {
        throw new NotCompletedComponentImplementation(
                "Реализация не закончена. Вы находитесь в слое сервера. Метод: calculateOffers"
                );
    }


    public void chooseOffer(LoanOfferDTO requestDTO) {
        throw new NotCompletedComponentImplementation("Реализация не закончена. Вы находитесь в слое сервера. " +
                "Метод: chooseOffer"
        );
    }

    public void finishRegistration(FinishRegistrationRequestDTO requestDTO,
                                   Long applicationId) {
        throw new NotCompletedComponentImplementation("Реализация не закончена. Вы находитесь в слое сервера. " +
                "Метод: calculateCredit"
        );
    }


}
