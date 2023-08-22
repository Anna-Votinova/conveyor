package com.neoflex.conveyor.service;

import com.neoflex.conveyor.config.ApplicationConfig;
import com.neoflex.conveyor.dto.request.LoanApplicationServiceDTO;
import com.neoflex.conveyor.dto.response.LoanOfferServiceDTO;
import com.neoflex.conveyor.service.utils.CalculationUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoanOfferService {

    private final ApplicationConfig applicationConfig;
    private final CalculationUtils calculationUtils;
    public static final BigDecimal INSURANCE_RATIO = new BigDecimal("0.1");

    /**
     * <p>Creates four loan offers with different rates, final amounts, monthly payments and totals
     * </p>
     * @param loanApplicationServiceDTO an application with minimum info about the client
     * @return a list with four loan offers sorted from worst to best depending on the rate
     * */
    public List<LoanOfferServiceDTO> preCalculateLoan(
            LoanApplicationServiceDTO loanApplicationServiceDTO) {

        LoanOfferServiceDTO plainOffer = createPreOffers(
                loanApplicationServiceDTO.getId(), loanApplicationServiceDTO.getAmount(),
                loanApplicationServiceDTO.getTerm(), applicationConfig.getGlobalRate()
        );
        plainOffer.setIsInsuranceEnabled(false);
        plainOffer.setIsSalaryClient(false);
        log.info("Plain offer equals: {}", plainOffer);

        LoanOfferServiceDTO clientOffer = createPreOffers(
                loanApplicationServiceDTO.getId(), loanApplicationServiceDTO.getAmount(),
                loanApplicationServiceDTO.getTerm(), calculateClientRate()
        );
        clientOffer.setIsInsuranceEnabled(false);
        clientOffer.setIsSalaryClient(true);
        log.info("Offer for client equals: {}", clientOffer);

        LoanOfferServiceDTO offerWithInsurance = createPreOffers(loanApplicationServiceDTO.getId(),
                calculateAmountWithInsurance(loanApplicationServiceDTO.getAmount()),
                loanApplicationServiceDTO.getTerm(), calculateRateWithInsurance()
        );
        offerWithInsurance.setIsInsuranceEnabled(true);
        offerWithInsurance.setIsSalaryClient(false);
        log.info("Offer with insurance equals: {}", offerWithInsurance);

        LoanOfferServiceDTO clientOfferWithInsurance = createPreOffers(loanApplicationServiceDTO.getId(),
                calculateAmountWithInsurance(loanApplicationServiceDTO.getAmount()),
                loanApplicationServiceDTO.getTerm(), calculateClientRateWithInsurance()
        );
        clientOfferWithInsurance.setIsInsuranceEnabled(true);
        clientOfferWithInsurance.setIsSalaryClient(true);
        log.info("Offer with insurance for client equals: {}", clientOfferWithInsurance);

        List<LoanOfferServiceDTO> preOffers = List.of(
                plainOffer, clientOffer, offerWithInsurance, clientOfferWithInsurance
        );
        log.info("List offers size: {}", preOffers.size());

        return preOffers.stream()
                        .sorted(Comparator.comparing(LoanOfferServiceDTO::getRate).reversed())
                        .toList();
    }

    private LoanOfferServiceDTO createPreOffers(Long id, BigDecimal amount, Integer term, BigDecimal rate) {
        log.info("Preparing offer with parameters: id = {}, requestedAmount = {}, term = {}, rate = {},",
                id, amount, term, rate);

        BigDecimal monthlyPayment = calculationUtils.calculateMonthlyPayment(rate, term, amount);
        log.info("Monthly payment for application: {}",  monthlyPayment);

        BigDecimal totalAmount = calculateTotalAmount(monthlyPayment, term);
        log.info("Total amount for application: {}", totalAmount);

        return LoanOfferServiceDTO.builder()
                .applicationId(id)
                .requestedAmount(amount)
                .totalAmount(totalAmount)
                .term(term)
                .monthlyPayment(monthlyPayment)
                .rate(rate)
                .build();
    }

    private BigDecimal calculateAmountWithInsurance(BigDecimal requestedAmount) {
        return requestedAmount.add(requestedAmount.multiply(INSURANCE_RATIO));
    }

    private BigDecimal calculateRateWithInsurance() {
        return applicationConfig.getGlobalRate().subtract(new BigDecimal("2"));
    }

    private BigDecimal calculateClientRate() {
        return applicationConfig.getGlobalRate().subtract(BigDecimal.ONE);
    }

    private BigDecimal calculateClientRateWithInsurance() {
        return applicationConfig.getGlobalRate()
                                .subtract(BigDecimal.ONE)
                                .subtract(new BigDecimal("2"));
    }

    private BigDecimal calculateTotalAmount(BigDecimal monthlyPayment, Integer term) {
        log.info("Calculate total amount with parameters: monthlyPayment = {}, term = {}", monthlyPayment, term);

        return monthlyPayment.multiply(new BigDecimal(term)).setScale(2, RoundingMode.HALF_EVEN);
    }
}
