package com.neoflex.application.service;

import com.neoflex.application.dto.LoanApplicationRequestDTO;
import com.neoflex.application.dto.LoanOfferDTO;
import com.neoflex.application.integration.deal.DealClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ApplicationService {

    public final DealClient dealClient;

    /**
     * <p>Sends short info about the client to the Deal and receives an offers list
     * </p>
     * @param loanApplicationRequestDTO short information about the client
     * @return a list with four offers
     * @throws com.neoflex.application.exception.BadRequestException - if the LoanApplicationRequestDTO sent to the Deal
     * is invalid
     */
    public List<LoanOfferDTO> prepareOffers(LoanApplicationRequestDTO loanApplicationRequestDTO) {
        List<LoanOfferDTO> offers = dealClient.calculateOffers(loanApplicationRequestDTO);
        log.info("Deal saved application and prepared {} offers", offers.size());
        return offers;
    }

    /**
     * <p>Sends the loan offer chosen by the client to the Deal
     * </p>
     * @param loanOfferDTO the loan offer chosen by the client
     * @throws com.neoflex.application.exception.NotFoundException - if the application does not exist
     */
    public void chooseOffer(LoanOfferDTO loanOfferDTO) {
        dealClient.chooseOffer(loanOfferDTO);
    }
}
