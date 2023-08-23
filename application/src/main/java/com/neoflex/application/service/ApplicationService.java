package com.neoflex.application.service;

import com.neoflex.application.dto.LoanApplicationRequestDTO;
import com.neoflex.application.dto.LoanOfferDTO;
import com.neoflex.application.exception.NotCompletedImplementationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ApplicationService {

    public List<LoanOfferDTO> prepareOffers(LoanApplicationRequestDTO loanApplicationRequestDTO) {
        throw new NotCompletedImplementationException("Вы находитесь в слое сервиса, метод: prepareOffers");
    }

    public void chooseOffer(LoanOfferDTO loanOfferDTO) {
        throw new NotCompletedImplementationException("Вы находитесь в слое сервиса, метод: chooseOffer");
    }
}
