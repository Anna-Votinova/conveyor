package com.neoflex.deal.service;

import com.neoflex.deal.entity.dto.LoanOfferDTO;
import com.neoflex.deal.entity.dto.request.FinishRegistrationRequestDTO;
import com.neoflex.deal.entity.dto.request.LoanApplicationRequestDTO;
import com.neoflex.deal.exception.NotCompletedComponentImplementation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DealService {


    public List<LoanOfferDTO> calculateOffers(LoanApplicationRequestDTO requestDTO) {
        throw new NotCompletedComponentImplementation(
                "Реализация не закончена. Вы находитесь в слое сервера. Метод: calculateOffers"
                );
    }


    public void chooseOffer(LoanOfferDTO requestDTO) {
        throw new NotCompletedComponentImplementation("Реализация не закончена. Вы находитесь в слое сервера. " +
                "Метод: chooseOffer"
        );
    }

    public void calculateCredit(FinishRegistrationRequestDTO requestDTO,
                                Long applicationId) {
        throw new NotCompletedComponentImplementation("Реализация не закончена. Вы находитесь в слое сервера. " +
                "Метод: calculateCredit"
        );
    }


}
