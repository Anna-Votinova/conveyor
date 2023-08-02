package com.neoflex.conveyor.service;

import com.neoflex.conveyor.model.LoanApplication;
import com.neoflex.conveyor.model.LoanOffer;
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

    private final CommonCalculationLoanService commonCalculationLoanService;

    private Long idCounter = 1L;

    private Long generateId() {
        return idCounter++;
    }

    /*
    * Метод создает четыре предложения с различными ставками, итоговыми суммами заемных средсвт, месячными платежами
    * и общей суммой по кредиту с учетом процентов,связываясь с общим сервисом подсчета CommonCalculationLoanService.
    * Выдает предложения от худшего (с большей ставкой) к лучшему (с меньшей ставкой).
     * */
    public List<LoanOffer> preCalculateLoan(LoanApplication loanApplication) {

        LoanOffer plainOffer = createPreOffers(loanApplication.getAmount(), loanApplication.getTerm(), false, false);
        log.info("Plain offer equals: {}", plainOffer);

        LoanOffer offerForClient = createPreOffers(loanApplication.getAmount(), loanApplication.getTerm(), false, true);
        log.info("Offer for client equals: {}", offerForClient);

        LoanOffer offerWithInsurance = createPreOffers(loanApplication.getAmount(), loanApplication.getTerm(), true, false);
        log.info("Offer with insurance equals: {}", offerWithInsurance);

        LoanOffer offerWithInsuranceForClient = createPreOffers(loanApplication.getAmount(), loanApplication.getTerm(), true, true);
        log.info("Offer with insurance for client equals: {}", offerWithInsuranceForClient);

        List<LoanOffer> preOffers = List.of(plainOffer, offerForClient, offerWithInsurance, offerWithInsuranceForClient);
        log.info("List offers size: {}", preOffers.size());

        return preOffers.stream()
                        .sorted(Comparator.comparing(LoanOffer::getRate).reversed())
                        .toList();
    }


    private LoanOffer createPreOffers(BigDecimal requestedAmount, Integer term, Boolean isInsuranceEnabled, Boolean isSalaryClient) {
        log.info("Preparing offer with parameters: requestedAmount = {}, term = {}, isInsuranceEnabled = {}, isSalaryClient = {}"
                , requestedAmount, term, isInsuranceEnabled, isSalaryClient);

        Long applicationId = generateId();

        BigDecimal preRate = commonCalculationLoanService.preCalculateRate(isInsuranceEnabled, isSalaryClient);

        BigDecimal amount = commonCalculationLoanService.calculateAmount(requestedAmount, isInsuranceEnabled);
        log.info("Amount for application with id {}: {}", applicationId, amount);

        BigDecimal monthlyPayment = commonCalculationLoanService.calculateMonthlyPayment(preRate, term, amount);
        log.info("Monthly payment for application with id {}: {}", applicationId, monthlyPayment);

        BigDecimal totalAmount = calculateTotalAmount(monthlyPayment, term);
        log.info("Total amount for application with id {}: {}", applicationId, totalAmount);

        return new LoanOffer(applicationId, amount, totalAmount, term, monthlyPayment, preRate, isInsuranceEnabled, isSalaryClient);
    }

    private BigDecimal calculateTotalAmount(BigDecimal monthlyPayment, Integer term) {
        log.info("Calculate total amount with parameters: monthlyPayment = {}, term = {}"
                , monthlyPayment, term);
        return monthlyPayment.multiply(new BigDecimal(term)).setScale(2, RoundingMode.HALF_EVEN);
    }

}
