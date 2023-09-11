package com.neoflex.conveyor.service;

import com.neoflex.conveyor.config.ApplicationConfig;
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
import com.neoflex.conveyor.service.utils.TimeUtil;
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

    private final CalculationUtils calculationUtils;
    private final ApplicationConfig applicationConfig;
    private final TimeUtil timeUtil;

    /**
     * <p>Validates client info and creates a final loan offer
     * </p>
     * @param fullInfoAboutClient comprehensive information about the client
     * @return a final loan offer with info about the final loan amount, final rate, monthly payment, interest payment,
     * psk, payment schedule
     * @throws javax.validation.ValidationException - if the client info is not full
     * @throws com.neoflex.conveyor.exception.NotProperClientCategoryException - if the client is not in the proper
     * client category
     */
    public CreditServiceDTO calculateLoan(ScoringDataServiceDTO fullInfoAboutClient) {

        checkEmploymentStatus(fullInfoAboutClient);
        checkPositionAndEmployerInn(fullInfoAboutClient);
        checkSalary(fullInfoAboutClient);
        checkAge(fullInfoAboutClient);
        checkWorkExperience(fullInfoAboutClient);

        BigDecimal finalRate = calculateFinalRate(fullInfoAboutClient);
        log.info("Final rate equals: {}", finalRate);

        BigDecimal amount = fullInfoAboutClient.getAmount();
        log.info("Amount equals: {}", amount);

        BigDecimal monthlyPayment = calculationUtils.calculateMonthlyPayment(finalRate, fullInfoAboutClient.getTerm(),
                amount);
        log.info("MonthlyPayment equals: {}", monthlyPayment);

        List<PaymentScheduleServiceElement> paymentScheduleServiceElements = preparePaymentSchedule(
                fullInfoAboutClient.getTerm(),
                amount,
                finalRate,
                monthlyPayment
        );
        log.info("Payment schedule calculated with {} elements", paymentScheduleServiceElements.size());

        BigDecimal totalAmount = getTotalAmount(paymentScheduleServiceElements);
        log.info("Full credit cost: {}", totalAmount);

        BigDecimal psk = calculatePsk(totalAmount, amount, fullInfoAboutClient.getTerm());
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
        log.debug("Credit calculated: {}", creditServiceDTO);

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

        if (EmploymentStatus.SELF_EMPLOYED.equals(clientInfo.getEmploymentServiceDTO().getEmploymentStatus())) {
            log.info("Add ratio 1 for self-employed");
            rate = rate.add(BigDecimal.ONE);
        }

        if (EmploymentStatus.BUSINESS_OWNER.equals(clientInfo.getEmploymentServiceDTO().getEmploymentStatus())) {
            log.info("Add ratio 3 for business owners");
            rate = rate.add(new BigDecimal("3"));
        }

        if (Position.MID_MANAGER.equals(clientInfo.getEmploymentServiceDTO().getPosition())) {
            log.info("Subtract ratio 2 for managers");
            rate = rate.subtract(new BigDecimal("2"));
        }

        if (Position.TOP_MANAGER.equals(clientInfo.getEmploymentServiceDTO().getPosition())) {
            log.info("Subtract ratio 4 for top managers");
            rate = rate.subtract(new BigDecimal("4"));
        }

        if (MaritalStatus.MARRIED.equals(clientInfo.getMaritalStatus())) {
            log.info("Subtract ratio 3 for married");
            rate = rate.subtract(new BigDecimal("3"));
        }

        if (MaritalStatus.DIVORCED.equals(clientInfo.getMaritalStatus())) {
            log.info("Add ratio 1 for divorced applicants");
            rate = rate.add(BigDecimal.ONE);
        }

        if (clientInfo.getDependentAmount() > 1) {
            log.info("Add ratio 1 for applicants with dependents");
            rate = rate.add(BigDecimal.ONE);
        }

        if (Gender.FEMALE.equals(clientInfo.getGender())
                && userIsOlder(MIN_WOMAN_AGE_FOR_SPECIAL_RATE, clientInfo.getBirthdate())
                && userIsYounger(MAX_WOMAN_AGE_FOR_SPECIAL_RATE, clientInfo.getBirthdate())) {
            log.info("Subtract ratio 3 for women from 35 to 60");
            rate = rate.subtract(new BigDecimal("3"));
        }

        if (Gender.MALE.equals(clientInfo.getGender())
                && userIsOlder(MIN_MAN_AGE_FOR_SPECIAL_RATE, clientInfo.getBirthdate())
                && userIsYounger(MAX_MAN_AGE_FOR_SPECIAL_RATE, clientInfo.getBirthdate())) {
            log.info("Subtract ratio 3 for men from 30 to 55");
            rate = rate.subtract(new BigDecimal("3"));
        }

        if (Gender.NON_BINARY.equals(clientInfo.getGender())) {
            log.info("Add ratio 3 for non-binary");
            rate = rate.add(new BigDecimal("3"));
        }

        if (rate.compareTo(new BigDecimal("1")) < 0) {
            log.info("Rate after calculation is: {}", rate);
            rate = new BigDecimal("1");
        }

        return rate;
    }

    private List<PaymentScheduleServiceElement> preparePaymentSchedule(
            Integer term, BigDecimal amount, BigDecimal finalRate, BigDecimal monthlyPayment
    ) {
        log.info("Preparing payment schedule list with parameters: term = {}, amount = {}, finalRate = {}," +
                        " monthlyPayment = {}", term, amount, finalRate, monthlyPayment);

        Integer id = 1;
        List<PaymentScheduleServiceElement> paymentScheduleServiceElements = new ArrayList<>();

        GregorianCalendar date = timeUtil.getCurrentDate();
        log.info("Current date: {}", date);
        date.set(Calendar.DAY_OF_MONTH, MONTHLY_PAYMENT_START_DAY);
        date.add(Calendar.MONTH, 1);
        log.info("Date of first payment: {}", date);

        BigDecimal remainingDebt = amount;

        for (int i = 0; i < term; i++) {
            BigDecimal interestPayment;
            BigDecimal mainPayment;
            BigDecimal debtSum;
            BigDecimal totalPayment = monthlyPayment;

            PaymentScheduleServiceElement scheduleElement = PaymentScheduleServiceElement.builder()
                    .number(id)
                    .date(date.getTime()
                              .toInstant()
                              .atZone(ZoneId.systemDefault())
                              .toLocalDate())
                    .build();
            log.debug("New payment schedule element equals: {}", scheduleElement);

            interestPayment = calculateInterestPayment(remainingDebt, finalRate,
                    new BigDecimal(date.getActualMaximum(Calendar.DAY_OF_MONTH)),
                    new BigDecimal(date.getActualMaximum(Calendar.DAY_OF_YEAR)));
            log.info("Interest payment for schedule element with id {} equals: {}", scheduleElement.getNumber(),
                    interestPayment);

            mainPayment = monthlyPayment.subtract(interestPayment);
            log.info("Main payment for schedule element with id {} equals: {}", scheduleElement.getNumber(),
                    mainPayment);

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

    private BigDecimal calculateInterestPayment(
            BigDecimal remainingDebt, BigDecimal finalRate, BigDecimal daysOfMonth, BigDecimal daysOfYear
    ) {
        log.info("Calculating interest payment with parameters: remainingDebt = {}, finalRate = {}, daysOfMonth = {}, " +
                        "daysOfYear = {}", remainingDebt, finalRate, daysOfMonth, daysOfYear);
        return remainingDebt
                .multiply(finalRate)
                .multiply(new BigDecimal(CalculationFormulaConstants.NUMBER_PERCENTAGE.getValue()))
                .multiply(daysOfMonth)
                .divide(daysOfYear, 2, RoundingMode.HALF_EVEN);
    }

    private BigDecimal calculatePsk(BigDecimal totalAmount, BigDecimal amount, Integer term) {
        log.info("Calculating PSK with parameters: totalAmount = {}, amount = {}, term = {}",
                totalAmount, amount, term);
        return ((totalAmount
                .divide(amount, 5, RoundingMode.HALF_UP))
                .subtract(new BigDecimal(CalculationFormulaConstants.ONE_RATIO.getValue())))
                .divide((new BigDecimal(term))
                        .divide(new BigDecimal(CalculationFormulaConstants.MONTHS_IN_YEAR.getValue()),
                                5, RoundingMode.HALF_UP), 5, RoundingMode.HALF_UP)
                .multiply(new BigDecimal(CalculationFormulaConstants.RATIO_HUNDRED.getValue()))
                .setScale(3, RoundingMode.HALF_EVEN);
    }

    private BigDecimal getTotalAmount(List<PaymentScheduleServiceElement> paymentScheduleServiceElement) {
        return paymentScheduleServiceElement.stream()
                .map(PaymentScheduleServiceElement::getTotalPayment)
                .reduce(BigDecimal::add)
                .orElseThrow();
    }

    private void checkEmploymentStatus(ScoringDataServiceDTO fullInfoAboutClient) {
        if (EmploymentStatus.UNEMPLOYED.equals(fullInfoAboutClient.getEmploymentServiceDTO().getEmploymentStatus())) {
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
                || fullInfoAboutClient.getEmploymentServiceDTO().getWorkExperienceCurrent()
                < MIN_CURRENT_WORK_EXPERIENCE) {
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
