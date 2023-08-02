package com.neoflex.conveyor.service;

import com.neoflex.conveyor.controller.advice.NotProperClientCategoryException;
import com.neoflex.conveyor.model.Credit;
import com.neoflex.conveyor.model.PaymentSchedule;
import com.neoflex.conveyor.model.ScoringData;
import com.neoflex.conveyor.model.dto.enums.EmploymentStatus;
import com.neoflex.conveyor.model.dto.enums.Gender;
import com.neoflex.conveyor.model.dto.enums.MaritalStatus;
import com.neoflex.conveyor.model.dto.enums.Position;
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

import static com.neoflex.conveyor.config.GlobalVariables.MONTHS_IN_YEAR;
import static com.neoflex.conveyor.config.GlobalVariables.NUMBER_PERCENTAGE;
import static com.neoflex.conveyor.config.GlobalVariables.ONE_RATIO;


@Service
@RequiredArgsConstructor
@Slf4j
public class CreditService {

    private final CommonCalculationLoanService commonCalculationLoanService;

    private static final BigDecimal RATE_RATIO_FOR_BUSINESS_OWNER = new BigDecimal("3");

    private static final BigDecimal RATE_RATIO_FOR_SELF_EMPLOYED = BigDecimal.ONE;

    private static final BigDecimal RATE_RATIO_FOR_MANAGER = new BigDecimal("2");

    private static final BigDecimal RATE_RATIO_FOR_TOP_MANAGER = new BigDecimal("4");

    private static final BigDecimal RATE_RATIO_FOR_MARRIED = new BigDecimal("3");

    private static final BigDecimal RATE_RATIO_FOR_DIVORCED = BigDecimal.ONE;

    private static final BigDecimal RATE_RATIO_FOR_CLIENT_WITH_DEPENDENTS = BigDecimal.ONE;

    private static final BigDecimal RATE_RATIO_FOR_MAN = new BigDecimal("3");

    private static final BigDecimal RATE_RATIO_FOR_WOMAN = new BigDecimal("3");

    private static final BigDecimal RATE_RATIO_FOR_NON_BINARY = new BigDecimal("3");

    private static final long MIN_WOMAN_AGE_FOR_SPECIAL_RATE = 35L;

    private static final long MAX_WOMAN_AGE_FOR_SPECIAL_RATE = 60L;

    private static final long MIN_MAN_AGE_FOR_SPECIAL_RATE = 30L;

    private static final long MAX_MAN_AGE_FOR_SPECIAL_RATE = 55L;

    private static final int START_DAY_FOR_MONTHLY_PAYMENT = 12;

    private static final BigDecimal RATIO_HUNDRED_FOR_CALCULATE_PSK = new BigDecimal("100");

    private static final BigDecimal SALARY_QUANTITY = new BigDecimal("20");

    private static final String REJECT_APPLICATION = "Отказ";

    private static final long MIN_CLIENT_AGE = 20L;

    private static final long MAX_CLIENT_AGE = 60L;

    private static final Integer MIN_COMMON_WORK_EXPERIENCE = 12;

    private static final Integer MIN_CURRENT_WORK_EXPERIENCE = 3;


    /*
     * Метод создает готовое кредитное предложение, предварительно проводя полную проверку клиента на соответствие
     * требованиям. В предложении есть информация о ставке, сумме кредита, ежемесячном платеже, графике платежей и
     * полной стоимости кредита (в процентах). Для подсчета суммы кредита, месячного платежа и предварительной ставки
     * обращается к общему сервису подсчета CommonCalculationLoanService.
     * */
    public Credit calculateLoan(ScoringData fullInfoAboutClient) {

        checkEmploymentStatus(fullInfoAboutClient);
        checkPositionAndEmployerINN(fullInfoAboutClient);
        checkSalary(fullInfoAboutClient);
        checkAge(fullInfoAboutClient);
        checkWorkExperience(fullInfoAboutClient);

        BigDecimal finalRate = calculateFinalRate(fullInfoAboutClient);
        log.info("Final rate equals: {}", finalRate);

        BigDecimal amount = commonCalculationLoanService.calculateAmount(fullInfoAboutClient.getAmount(), fullInfoAboutClient.getIsInsuranceEnabled());
        log.info("Amount equals: {}", amount);

        BigDecimal monthlyPayment = commonCalculationLoanService.calculateMonthlyPayment(finalRate, fullInfoAboutClient.getTerm(), amount);
        log.info("MonthlyPayment equals: {}", monthlyPayment);

        List<PaymentSchedule> paymentSchedule = preparePaymentSchedule(fullInfoAboutClient.getTerm(), amount, finalRate, monthlyPayment);
        log.info("Payment schedule calculated with {} elements", paymentSchedule.size());

        BigDecimal psk = calculatePsk(paymentSchedule, amount, fullInfoAboutClient.getTerm());
        log.info("Psk calculated. Full credit cost in percent: {}", psk);

        Credit credit = new Credit(amount, fullInfoAboutClient.getTerm(), monthlyPayment, finalRate, psk,
                fullInfoAboutClient.getIsInsuranceEnabled(), fullInfoAboutClient.getIsSalaryClient(), paymentSchedule);
        log.info("Credit calculated: {}", credit);

        return credit;

    }

    private BigDecimal calculateFinalRate(ScoringData clientInfo) {
        BigDecimal rate = commonCalculationLoanService.preCalculateRate(clientInfo.getIsInsuranceEnabled(), clientInfo.getIsSalaryClient());

        if (clientInfo.getEmployment().getEmploymentStatus().equals(EmploymentStatus.SELF_EMPLOYED)) {
            log.info("Add ratio 1 for self-employed clients");
            rate = rate.add(RATE_RATIO_FOR_SELF_EMPLOYED);
        }

        if (clientInfo.getEmployment().getEmploymentStatus().equals(EmploymentStatus.BUSINESS_OWNER)) {
            log.info("Add ratio 3 for business owners");
            rate = rate.add(RATE_RATIO_FOR_BUSINESS_OWNER);
        }

        if (clientInfo.getEmployment().getPosition().equals(Position.MANAGER)) {
            log.info("Subtract ratio 2 for managers");
            rate = rate.subtract(RATE_RATIO_FOR_MANAGER);
        }

        if (clientInfo.getEmployment().getPosition().equals(Position.TOP_MANAGER)) {
            log.info("Subtract ratio 4 for top managers");
            rate = rate.subtract(RATE_RATIO_FOR_TOP_MANAGER);
        }

        if (clientInfo.getMaritalStatus().equals(MaritalStatus.MARRIED)) {
            log.info("Subtract ratio 3 for married clients");
            rate = rate.subtract(RATE_RATIO_FOR_MARRIED);
        }

        if (clientInfo.getMaritalStatus().equals(MaritalStatus.DIVORCED)) {
            log.info("Add ratio 1 for divorced clients");
            rate = rate.add(RATE_RATIO_FOR_DIVORCED);
        }

        if (clientInfo.getDependentAmount() > 1) {
            log.info("Add ratio 1 for clients with dependents");
            rate = rate.add(RATE_RATIO_FOR_CLIENT_WITH_DEPENDENTS);
        }

        if (clientInfo.getGender().equals(Gender.FEMALE)
                && minBirthdayDateForSpecialRateIsAfterOrEqualsClientBirthday(MIN_WOMAN_AGE_FOR_SPECIAL_RATE, clientInfo.getBirthdate())
                && maxAgeForSpecialRateIsBeforeClientBirthday(MAX_WOMAN_AGE_FOR_SPECIAL_RATE, clientInfo.getBirthdate())) {
            log.info("Subtract ratio 3 for women from 35 to 60");
            rate = rate.subtract(RATE_RATIO_FOR_WOMAN);

        }

        if (clientInfo.getGender().equals(Gender.MALE)
                && minBirthdayDateForSpecialRateIsAfterOrEqualsClientBirthday(MIN_MAN_AGE_FOR_SPECIAL_RATE, clientInfo.getBirthdate())
                && maxAgeForSpecialRateIsBeforeClientBirthday(MAX_MAN_AGE_FOR_SPECIAL_RATE, clientInfo.getBirthdate())) {
            log.info("Subtract ratio 3 for men from 30 to 55");
            rate = rate.subtract(RATE_RATIO_FOR_MAN);

        }

        if (clientInfo.getGender().equals(Gender.NON_BINARY)) {
            log.info("Add ratio 3 for non-binary clients");
            rate = rate.add(RATE_RATIO_FOR_NON_BINARY);

        }

        return rate;
    }


    private List<PaymentSchedule> preparePaymentSchedule(Integer term, BigDecimal amount, BigDecimal finalRate, BigDecimal monthlyPayment) {
        log.info("Preparing payment schedule list with parameters: term = {}, amount = {}, finalRate = {}, monthlyPayment = {}"
                , term, amount, finalRate, monthlyPayment);

        Integer id = 1;
        List<PaymentSchedule> paymentSchedules = new ArrayList<>();

        GregorianCalendar date = new GregorianCalendar();
        date.set(Calendar.DAY_OF_MONTH, START_DAY_FOR_MONTHLY_PAYMENT);
        date.add(Calendar.MONTH, 1);
        log.info("Date of first payment: {}", date);

        BigDecimal remainingDebt = amount;

        for (int i = 0; i < term; i++) {
            BigDecimal interestPayment;
            BigDecimal mainPayment;
            BigDecimal debtSum;
            BigDecimal totalPayment = monthlyPayment;

            PaymentSchedule scheduleElement = PaymentSchedule.builder()
                                                             .number(id)
                                                             .date(date.getTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDate())
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
            paymentSchedules.add(scheduleElement);
            remainingDebt = debtSum;
            date.add(Calendar.MONTH, 1);
            id++;


        }

        log.info("Remaining debt after all calculations equals: {}", remainingDebt);
        return paymentSchedules.stream()
                               .sorted(Comparator.comparing(PaymentSchedule::getNumber))
                               .toList();
    }

    private BigDecimal calculateInterestPayment(BigDecimal remainingDebt, BigDecimal finalRate, BigDecimal daysOfMonth, BigDecimal daysOfYear) {
        log.info("Calculating interest payment with parameters: remainingDebt = {}, finalRate = {}, daysOfMonth = {}, daysOfYear = {}"
                , remainingDebt, finalRate, daysOfMonth, daysOfYear);
        return remainingDebt
                .multiply(finalRate)
                .multiply(NUMBER_PERCENTAGE)
                .multiply(daysOfMonth)
                .divide(daysOfYear, 2, RoundingMode.HALF_EVEN);
    }

    private BigDecimal calculatePsk(List<PaymentSchedule> paymentSchedule, BigDecimal amount, Integer term) {
        log.info("Calculating PSK for payment schedule list with {} elements with parameters: " +
                "amount = {}, term = {}", paymentSchedule.size(), amount, term);
        BigDecimal numericPsk = BigDecimal.ZERO;
        BigDecimal pskInPercent;

        for (PaymentSchedule p : paymentSchedule) {
            numericPsk = numericPsk.add(p.getTotalPayment());
        }

        log.info("Full credit cost: {}", numericPsk);

        pskInPercent = ((numericPsk
                .divide(amount, 5, RoundingMode.HALF_UP))
                .subtract(ONE_RATIO))
                .divide((new BigDecimal(term))
                        .divide(MONTHS_IN_YEAR, 5, RoundingMode.HALF_UP), 5, RoundingMode.HALF_UP)
                .multiply(RATIO_HUNDRED_FOR_CALCULATE_PSK).setScale(3, RoundingMode.HALF_EVEN);

        return pskInPercent;
    }


    private void checkEmploymentStatus(ScoringData fullInfoAboutClient) {
        if (fullInfoAboutClient.getEmployment().getEmploymentStatus().equals(EmploymentStatus.UNEMPLOYED)) {
            log.error("Client is out of work");
            throw new NotProperClientCategoryException(REJECT_APPLICATION);
        }
    }

    private void checkPositionAndEmployerINN(ScoringData fullInfoAboutClient) {
        if (fullInfoAboutClient.getEmployment().getEmployerINN() == null
                || fullInfoAboutClient.getEmployment().getPosition() == null) {
            log.error("Not full information about a job - there is not info about a position and/or an employer");
            throw new ValidationException("Вы предоставили не все сведения. " +
                    "Заполните, пожалуйста, информацию о вашем работодателе и должности");
        }
    }

    private void checkSalary(ScoringData fullInfoAboutClient) {
        if (fullInfoAboutClient.getEmployment().getSalary().multiply(SALARY_QUANTITY)
                               .compareTo(fullInfoAboutClient.getAmount()) < 0) {
            log.error("Client salary is not enough");
            throw new NotProperClientCategoryException(REJECT_APPLICATION);
        }
    }

    private void checkAge(ScoringData fullInfoAboutClient) {
        if (minAgeIsBeforeClientBirthday(fullInfoAboutClient.getBirthdate())
                || maxAgeIsAfterClientBirthday(fullInfoAboutClient.getBirthdate())
                || maxAgeIsEqualClientBirthday(fullInfoAboutClient.getBirthdate())) {
            log.error("Client age is not proper");
            throw new NotProperClientCategoryException(REJECT_APPLICATION);
        }
    }

    private void checkWorkExperience(ScoringData fullInfoAboutClient) {
        if (fullInfoAboutClient.getEmployment().getWorkExperienceTotal() < MIN_COMMON_WORK_EXPERIENCE
                || fullInfoAboutClient.getEmployment().getWorkExperienceCurrent() < MIN_CURRENT_WORK_EXPERIENCE) {
            log.error("Client work experience is not enough");
            throw new NotProperClientCategoryException(REJECT_APPLICATION);
        }
    }

    private boolean minBirthdayDateForSpecialRateIsAfterOrEqualsClientBirthday(Long minAgeForSpecialRate, LocalDate clientBirthday) {
        return LocalDate.now().minusYears(minAgeForSpecialRate).isAfter(clientBirthday)
                || LocalDate.now().minusYears(minAgeForSpecialRate).equals(clientBirthday);
    }

    private boolean maxAgeForSpecialRateIsBeforeClientBirthday(Long maxAgeForSpecialRate, LocalDate clientBirthday) {
        return LocalDate.now().minusYears(maxAgeForSpecialRate).isBefore(clientBirthday);
    }

    private boolean minAgeIsBeforeClientBirthday(LocalDate clientBirthday) {
        return LocalDate.now().minusYears(MIN_CLIENT_AGE).isBefore(clientBirthday);
    }

    private boolean maxAgeIsAfterClientBirthday(LocalDate clientBirthday) {
        return LocalDate.now().minusYears(MAX_CLIENT_AGE).isAfter(clientBirthday);
    }

    private boolean maxAgeIsEqualClientBirthday(LocalDate clientBirthday) {
        return LocalDate.now().minusYears(MAX_CLIENT_AGE).equals(clientBirthday);
    }

}
