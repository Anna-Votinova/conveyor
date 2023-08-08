package com.neoflex.conveyor.service;

import com.neoflex.conveyor.config.ApplicationConfig;
import com.neoflex.conveyor.config.GlobalVariables;
import com.neoflex.conveyor.exception.NotProperClientCategoryException;
import com.neoflex.conveyor.dto.response.CreditServiceDTO;
import com.neoflex.conveyor.dto.response.PaymentScheduleServiceElement;
import com.neoflex.conveyor.dto.request.ScoringDataServiceDTO;
import com.neoflex.conveyor.dto.enums.EmploymentStatus;
import com.neoflex.conveyor.dto.enums.Gender;
import com.neoflex.conveyor.dto.enums.MaritalStatus;
import com.neoflex.conveyor.dto.enums.Position;
import com.neoflex.conveyor.service.enums.CalculationFormulaConstants;
import com.neoflex.conveyor.service.utils.CalculationUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.validation.ValidationException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreditService {

    private final CalculationUtils calculationUtils;

    private final ApplicationConfig applicationConfig;

    private static final long MIN_CLIENT_AGE = 20L;

    private static final long MAX_CLIENT_AGE = 60L;

    private static final Integer MIN_COMMON_WORK_EXPERIENCE = 12;

    private static final Integer MIN_CURRENT_WORK_EXPERIENCE = 3;

    private static final BigDecimal SALARY_QUANTITY = new BigDecimal("20");

    private static final long MIN_WOMAN_AGE_FOR_SPECIAL_RATE = 35L;

    private static final long MAX_WOMAN_AGE_FOR_SPECIAL_RATE = 60L;

    private static final long MIN_MAN_AGE_FOR_SPECIAL_RATE = 30L;

    private static final long MAX_MAN_AGE_FOR_SPECIAL_RATE = 55L;

    private static final int MONTHLY_PAYMENT_START_DAY = 12;

    private static final String REJECT_APPLICATION = "Отказ";

    /**
     *<p>Validates client info and creates a final loan offer
     *</p>
     * @param fullInfoAboutClient comprehensive information about the client
     * @return a final loan offer with info about the final loan amount, final rate, monthly payment, interest payment, psk,
     * payment schedule
     * @throws javax.validation.ValidationException - if the client info is not full
     * @throws NotProperClientCategoryException - if the client is not in the proper client category
     */
    public CreditServiceDTO calculateLoan(ScoringDataServiceDTO fullInfoAboutClient) {

        checkEmploymentStatus(fullInfoAboutClient);
        checkPositionAndEmployerInn(fullInfoAboutClient);
        checkSalary(fullInfoAboutClient);
        checkAge(fullInfoAboutClient);
        checkWorkExperience(fullInfoAboutClient);

        BigDecimal finalRate = calculateFinalRate(fullInfoAboutClient);
        log.info("Final rate equals: {}", finalRate);

        BigDecimal amount = calculateAmount(fullInfoAboutClient);
        log.info("Amount equals: {}", amount);

        BigDecimal monthlyPayment = calculationUtils.calculateMonthlyPayment(finalRate, fullInfoAboutClient.getTerm(), amount);
        log.info("MonthlyPayment equals: {}", monthlyPayment);

        List<PaymentScheduleServiceElement> paymentScheduleServiceElements = preparePaymentSchedule(
                fullInfoAboutClient.getTerm(),
                amount,
                finalRate,
                monthlyPayment
        );
        log.info("Payment schedule calculated with {} elements", paymentScheduleServiceElements.size());

        BigDecimal psk = calculatePsk(paymentScheduleServiceElements, amount, fullInfoAboutClient.getTerm());
        log.info("Psk calculated. Full credit cost in percent: {}", psk);

        CreditServiceDTO creditServiceDTO = new CreditServiceDTO(
                amount,
                fullInfoAboutClient.getTerm(),
                monthlyPayment,
                finalRate,
                psk,
                fullInfoAboutClient.getIsInsuranceEnabled(),
                fullInfoAboutClient.getIsSalaryClient(),
                paymentScheduleServiceElements
        );
        log.info("Credit calculated: {}", creditServiceDTO);

        return creditServiceDTO;
    }

    private BigDecimal calculateFinalRate(ScoringDataServiceDTO clientInfo) {
        BigDecimal rate = applicationConfig.getGlobalRate();

        if (Boolean.TRUE.equals(clientInfo.getIsInsuranceEnabled())) {
            log.info("Subtract ratio 2 for loan with insurance");
            rate = rate.subtract(new BigDecimal("2"));
        }

        if (Boolean.TRUE.equals(clientInfo.getIsSalaryClient())) {
            log.info("Subtract ratio 1 for clients");
            rate = rate.subtract(BigDecimal.ONE);
        }

        if (clientInfo.getEmploymentServiceDTO().getEmploymentStatus().equals(EmploymentStatus.SELF_EMPLOYED)) {
            log.info("Add ratio 1 for self-employed");
            rate = rate.add(BigDecimal.ONE);
        }

        if (clientInfo.getEmploymentServiceDTO().getEmploymentStatus().equals(EmploymentStatus.BUSINESS_OWNER)) {
            log.info("Add ratio 3 for business owners");
            rate = rate.add(new BigDecimal("3"));
        }

        if (clientInfo.getEmploymentServiceDTO().getPosition().equals(Position.MANAGER)) {
            log.info("Subtract ratio 2 for managers");
            rate = rate.subtract(new BigDecimal("2"));
        }

        if (clientInfo.getEmploymentServiceDTO().getPosition().equals(Position.TOP_MANAGER)) {
            log.info("Subtract ratio 4 for top managers");
            rate = rate.subtract(new BigDecimal("4"));
        }

        if (clientInfo.getMaritalStatus().equals(MaritalStatus.MARRIED)) {
            log.info("Subtract ratio 3 for married");
            rate = rate.subtract(new BigDecimal("3"));
        }

        if (clientInfo.getMaritalStatus().equals(MaritalStatus.DIVORCED)) {
            log.info("Add ratio 1 for divorced applicants");
            rate = rate.add(BigDecimal.ONE);
        }

        if (clientInfo.getDependentAmount() > 1) {
            log.info("Add ratio 1 for applicants with dependents");
            rate = rate.add(BigDecimal.ONE);
        }

        if (clientInfo.getGender().equals(Gender.FEMALE)
                && userIsOlder(MIN_WOMAN_AGE_FOR_SPECIAL_RATE, clientInfo.getBirthdate())
                && userIsYounger(MAX_WOMAN_AGE_FOR_SPECIAL_RATE, clientInfo.getBirthdate())) {
            log.info("Subtract ratio 3 for women from 35 to 60");
            rate = rate.subtract(new BigDecimal("3"));

        }

        if (clientInfo.getGender().equals(Gender.MALE)
                && userIsOlder(MIN_MAN_AGE_FOR_SPECIAL_RATE, clientInfo.getBirthdate())
                && userIsYounger(MAX_MAN_AGE_FOR_SPECIAL_RATE, clientInfo.getBirthdate())) {
            log.info("Subtract ratio 3 for men from 30 to 55");
            rate = rate.subtract(new BigDecimal("3"));

        }

        if (clientInfo.getGender().equals(Gender.NON_BINARY)) {
            log.info("Add ratio 3 for non-binary");
            rate = rate.add(new BigDecimal("3"));

        }

        return rate;
    }

    private BigDecimal calculateAmount(ScoringDataServiceDTO fullInfoAboutClient) {
        log.info("Calculate amount with parameters: requestedAmount = {}, isInsuranceEnabled = {}",
                fullInfoAboutClient.getAmount(), fullInfoAboutClient.getIsInsuranceEnabled());

        BigDecimal amount = fullInfoAboutClient.getAmount();
        BigDecimal insuranceCost = BigDecimal.ZERO;

        if (Boolean.TRUE.equals(fullInfoAboutClient.getIsInsuranceEnabled())) {
            insuranceCost = fullInfoAboutClient.getAmount().multiply(GlobalVariables.INSURANCE_RATIO);
            amount = amount.add(insuranceCost);
        }

        log.info("Insurance cost: {}", insuranceCost);
        return amount;

    }


    private List<PaymentScheduleServiceElement> preparePaymentSchedule(Integer term, BigDecimal amount, BigDecimal finalRate, BigDecimal monthlyPayment) {
        log.info("Preparing payment schedule list with parameters: term = {}, amount = {}, finalRate = {}, monthlyPayment = {}",
                term, amount, finalRate, monthlyPayment);

        Integer id = 1;
        List<PaymentScheduleServiceElement> paymentScheduleServiceElements = new ArrayList<>();

        GregorianCalendar date = new GregorianCalendar();
        date.set(Calendar.DAY_OF_MONTH, MONTHLY_PAYMENT_START_DAY);
        date.add(Calendar.MONTH, 1);
        log.info("Date of first payment: {}", date);

        BigDecimal remainingDebt = amount;

        for (int i = 0; i < term; i++) {
            BigDecimal interestPayment;
            BigDecimal mainPayment;
            BigDecimal debtSum;
            BigDecimal totalPayment = monthlyPayment;

            PaymentScheduleServiceElement
                    scheduleElement = PaymentScheduleServiceElement.builder().number(id)
                                                                   .date(date.getTime()
                                                                             .toInstant()
                                                                             .atZone(ZoneId.systemDefault())
                                                                             .toLocalDate())
                                                                   .build();
            log.debug("New payment schedule element equals: {}", scheduleElement);

            interestPayment = calculateInterestPayment(remainingDebt, finalRate,
                    new BigDecimal(date.getActualMaximum(Calendar.DAY_OF_MONTH)),
                    new BigDecimal(date.getActualMaximum(Calendar.DAY_OF_YEAR)));
            log.info("Interest payment for schedule element with id {} equals: {}", scheduleElement.getNumber(), interestPayment);

            mainPayment = monthlyPayment.subtract(interestPayment);
            log.info("Main payment for schedule element with id {} equals: {}", scheduleElement.getNumber(), mainPayment);

            debtSum = remainingDebt.subtract(mainPayment);
            log.info("Remaining debt for schedule element with id {} equals: {}", scheduleElement.getNumber(), debtSum);

            scheduleElement.setInterestPayment(interestPayment);

            if (i == term - 1) {
                log.info("This is the last payment. Add remaining debt to the last schedule element");
                mainPayment = mainPayment.add(debtSum);
                totalPayment = totalPayment.add(debtSum);
                debtSum = remainingDebt.subtract(mainPayment);
            }

            scheduleElement.setTotalPayment(totalPayment);
            scheduleElement.setDebtPayment(mainPayment);
            scheduleElement.setRemainingDebt(debtSum);

            log.info("Schedule element prepared: {}", scheduleElement);
            paymentScheduleServiceElements.add(scheduleElement);
            remainingDebt = debtSum;
            date.add(Calendar.MONTH, 1);
            id++;
        }

        log.info("Remaining debt after all calculations equals: {}", remainingDebt);
        return paymentScheduleServiceElements.stream()
                                             .sorted(Comparator.comparing(PaymentScheduleServiceElement::getNumber))
                                             .toList();
    }

    private BigDecimal calculateInterestPayment(BigDecimal remainingDebt, BigDecimal finalRate, BigDecimal daysOfMonth, BigDecimal daysOfYear) {
        log.info("Calculating interest payment with parameters: remainingDebt = {}, finalRate = {}, daysOfMonth = {}, daysOfYear = {}",
                remainingDebt, finalRate, daysOfMonth, daysOfYear);
        return remainingDebt
                .multiply(finalRate)
                .multiply(CalculationFormulaConstants.NUMBER_PERCENTAGE.getValue())
                .multiply(daysOfMonth)
                .divide(daysOfYear, 2, RoundingMode.HALF_EVEN);
    }

    private BigDecimal calculatePsk(List<PaymentScheduleServiceElement> paymentScheduleServiceElement, BigDecimal amount, Integer term) {
        log.info("Calculating PSK for payment schedule list with {} elements with parameters: amount = {}, term = {}",
                paymentScheduleServiceElement.size(), amount, term);
        BigDecimal numericPsk = BigDecimal.ZERO;
        BigDecimal percentPsk;

        for (PaymentScheduleServiceElement p : paymentScheduleServiceElement) {
            numericPsk = numericPsk.add(p.getTotalPayment());
        }

        log.info("Full credit cost: {}", numericPsk);

        percentPsk = ((numericPsk
                .divide(amount, 5, RoundingMode.HALF_UP))
                .subtract(CalculationFormulaConstants.ONE_RATIO.getValue()))
                .divide((new BigDecimal(term))
                        .divide(CalculationFormulaConstants.MONTHS_IN_YEAR.getValue(), 5, RoundingMode.HALF_UP), 5, RoundingMode.HALF_UP)
                .multiply(CalculationFormulaConstants.RATIO_HUNDRED.getValue()).setScale(3, RoundingMode.HALF_EVEN);

        return percentPsk;
    }

    private void checkEmploymentStatus(ScoringDataServiceDTO fullInfoAboutClient) {
        if (fullInfoAboutClient.getEmploymentServiceDTO().getEmploymentStatus().equals(EmploymentStatus.UNEMPLOYED)) {
            log.error("Client is out of work");
            throw new NotProperClientCategoryException(REJECT_APPLICATION);
        }
    }

    private void checkPositionAndEmployerInn(ScoringDataServiceDTO fullInfoAboutClient) {
        if (fullInfoAboutClient.getEmploymentServiceDTO().getEmployerINN() == null
                || fullInfoAboutClient.getEmploymentServiceDTO().getPosition() == null) {
            log.error("Not full information about a job - there is not info about a position and/or an employer");
            throw new ValidationException("Вы предоставили не все сведения. " +
                    "Заполните, пожалуйста, информацию о вашем работодателе и должности");
        }
    }

    private void checkSalary(ScoringDataServiceDTO fullInfoAboutClient) {
        if (fullInfoAboutClient.getEmploymentServiceDTO().getSalary().multiply(SALARY_QUANTITY)
                               .compareTo(fullInfoAboutClient.getAmount()) < 0) {
            log.error("Client salary is not enough");
            throw new NotProperClientCategoryException(REJECT_APPLICATION);
        }
    }

    private void checkAge(ScoringDataServiceDTO fullInfoAboutClient) {
        if (LocalDate.now().minusYears(MIN_CLIENT_AGE).isBefore(fullInfoAboutClient.getBirthdate())
                || LocalDate.now().minusYears(MAX_CLIENT_AGE).isAfter(fullInfoAboutClient.getBirthdate())
                || LocalDate.now().minusYears(MAX_CLIENT_AGE).equals(fullInfoAboutClient.getBirthdate())) {
            log.error("Client age is not proper");
            throw new NotProperClientCategoryException(REJECT_APPLICATION);
        }
    }

    private void checkWorkExperience(ScoringDataServiceDTO fullInfoAboutClient) {
        if (fullInfoAboutClient.getEmploymentServiceDTO().getWorkExperienceTotal() < MIN_COMMON_WORK_EXPERIENCE
                || fullInfoAboutClient.getEmploymentServiceDTO().getWorkExperienceCurrent() < MIN_CURRENT_WORK_EXPERIENCE) {
            log.error("Client work experience is not enough");
            throw new NotProperClientCategoryException(REJECT_APPLICATION);
        }
    }

    protected boolean userIsOlder(Long minAge, LocalDate clientBirthday) {
        return LocalDate.now().minusYears(minAge).isAfter(clientBirthday)
                || LocalDate.now().minusYears(minAge).equals(clientBirthday);
    }

    protected boolean userIsYounger(Long maxAge, LocalDate clientBirthday) {
        return LocalDate.now().minusYears(maxAge).isBefore(clientBirthday);
    }

}
