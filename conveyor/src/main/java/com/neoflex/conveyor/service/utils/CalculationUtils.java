package com.neoflex.conveyor.service.utils;

import com.neoflex.conveyor.service.enums.CalculationFormulaConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@Slf4j
@RequiredArgsConstructor
public class CalculationUtils {

    /**
     * <p>Calculates the monthly payment on the formula from open access
     * </p>
     * @param rate the final loan rate
     * @param term the credit period
     * @param amount the final loan amount
     * @return the monthly payment
     * @see <a href="https://journal.tinkoff.ru/guide/credit-payment/#one">...</a>
     * */
    public BigDecimal calculateMonthlyPayment(BigDecimal rate, Integer term, BigDecimal amount) {
        log.info("Calculate monthly payment with parameters: rate = {}, term = {}, amount = {}", rate, term, amount);

        BigDecimal ratePerMonth = rate.divide(CalculationFormulaConstants.MONTHS_IN_YEAR.getValue(), 5, RoundingMode.HALF_UP);
        log.debug("Rate per month equals: {}", ratePerMonth);

        BigDecimal numericRatePerMonth = ratePerMonth.multiply(CalculationFormulaConstants.NUMBER_PERCENTAGE.getValue());
        log.debug("Numeric rate per month equals: {}", numericRatePerMonth);

        BigDecimal annuityRate = numericRatePerMonth
                .multiply((CalculationFormulaConstants.ONE_RATIO.getValue()
                        .add(numericRatePerMonth))
                        .pow(term))
                .divide(((CalculationFormulaConstants.ONE_RATIO.getValue()
                                .add(numericRatePerMonth))
                                .pow(term)
                                .subtract(CalculationFormulaConstants.ONE_RATIO.getValue())),
                        5, RoundingMode.HALF_UP
                );
        log.debug("Annuity rate equals: {}", annuityRate);

        return amount.multiply(annuityRate).setScale(2, RoundingMode.HALF_EVEN);
    }

}
