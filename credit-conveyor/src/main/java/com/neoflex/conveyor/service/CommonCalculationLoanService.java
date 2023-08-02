package com.neoflex.conveyor.service;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static com.neoflex.conveyor.config.GlobalVariables.MONTHS_IN_YEAR;
import static com.neoflex.conveyor.config.GlobalVariables.NUMBER_PERCENTAGE;
import static com.neoflex.conveyor.config.GlobalVariables.ONE_RATIO;

@Service
@NoArgsConstructor
@Slf4j
public class CommonCalculationLoanService {

    private static final BigDecimal REDUCING_RATIO_APPLICANT_IS_CLIENT = BigDecimal.ONE;

    private static final BigDecimal INSURANCE_RATIO = BigDecimal.valueOf(0.1);

    private static final BigDecimal REDUCING_RATIO_INSURANCE_ENABLED = new BigDecimal("2");

    @Value("${rate.property}")
    private String globalRate;

    /*
     * Метод высчитывает предварительную ставку в зависимости от того, является ли завитель клиентом банка и включена
     * ли в предложение страховка. Если страховка есть, ставка снижается на 2 процентных пункта, если заявитель - клиент
     * банка, то на 1 процент. Дисконты не взаимоисключают друг друга, а накапливаются.
     * */
    public BigDecimal preCalculateRate(Boolean isInsuranceEnabled, Boolean isSalaryClient) {
        log.info("Precalculate rate with parameters: isInsuranceEnabled = {}, isSalaryClient = {}"
                , isInsuranceEnabled, isSalaryClient);

        BigDecimal rate = new BigDecimal(globalRate);

        if (Boolean.TRUE.equals(isInsuranceEnabled)) {
            rate = rate.subtract(REDUCING_RATIO_INSURANCE_ENABLED);
        }

        if (Boolean.TRUE.equals(isSalaryClient)) {
            rate = rate.subtract(REDUCING_RATIO_APPLICANT_IS_CLIENT);
        }

        log.info("Precalculated rate equals: {}", rate);
        return rate;
    }

    /*
     * Метод высчитывает сумму кредита в зависимости от того, включена ли в предложение страховка. Если страховка есть,
     * то ее сумма вносится в тело кредита.
     * */
    public BigDecimal calculateAmount(BigDecimal requestedAmount, Boolean isInsuranceEnabled) {
        log.info("Calculate amount with parameters: requestedAmount = {}, isInsuranceEnabled = {}"
                , requestedAmount, isInsuranceEnabled);

        BigDecimal amount = requestedAmount;
        BigDecimal insuranceCost = BigDecimal.ZERO;

        if (Boolean.TRUE.equals(isInsuranceEnabled)) {
            insuranceCost = requestedAmount.multiply(INSURANCE_RATIO);
            amount = requestedAmount.add(insuranceCost);
        }

        log.info("Insurance cost: {}", insuranceCost);
        return amount;
    }

    /*
     * Метод высчитывает сумму ежемесячного платежа по формуле из открытого доступа
     * (https://journal.tinkoff.ru/guide/credit-payment/#one)
     * */
    public BigDecimal calculateMonthlyPayment(BigDecimal rate, Integer term, BigDecimal amount) {
        log.info("Calculate monthly payment with parameters: rate = {}, term = {}, amount = {}"
                , rate, term, amount);

        BigDecimal ratePerMonth = rate.divide(MONTHS_IN_YEAR, 5, RoundingMode.HALF_UP);
        log.debug("Rate per month equals: {}", ratePerMonth);

        BigDecimal numericRatePerMonth = ratePerMonth.multiply(NUMBER_PERCENTAGE);
        log.debug("Numeric rate per month equals: {}", numericRatePerMonth);

        BigDecimal annuityRate = numericRatePerMonth
                .multiply((ONE_RATIO
                        .add(numericRatePerMonth))
                        .pow(term))
                .divide(((ONE_RATIO
                                .add(numericRatePerMonth))
                                .pow(term)
                                .subtract(ONE_RATIO)),
                        5, RoundingMode.HALF_UP
                );
        log.debug("Annuity rate equals: {}", annuityRate);

        return amount.multiply(annuityRate).setScale(2, RoundingMode.HALF_EVEN);

    }

}
