package com.neoflex.conveyor.service;

import com.neoflex.conveyor.controller.advice.NotProperClientCategoryException;
import com.neoflex.conveyor.model.Credit;
import com.neoflex.conveyor.model.LoanApplication;
import com.neoflex.conveyor.model.LoanOffer;
import com.neoflex.conveyor.model.PaymentSchedule;
import com.neoflex.conveyor.model.ScoringData;
import com.neoflex.conveyor.model.dto.CreditDTO;
import com.neoflex.conveyor.model.dto.LoanApplicationRequestDTO;
import com.neoflex.conveyor.model.dto.LoanOfferDTO;
import com.neoflex.conveyor.model.dto.ScoringDataDTO;
import com.neoflex.conveyor.model.dto.enums.EmploymentStatus;
import com.neoflex.conveyor.model.dto.enums.Gender;
import com.neoflex.conveyor.model.dto.enums.MaritalStatus;
import com.neoflex.conveyor.model.dto.enums.Position;
import com.neoflex.conveyor.model.mapper.CreditMapper;
import com.neoflex.conveyor.model.mapper.LoanApplicationMapper;
import com.neoflex.conveyor.model.mapper.LoanOfferMapper;
import com.neoflex.conveyor.model.mapper.ScoringDataMapper;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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
@NoArgsConstructor
@Slf4j
public class CreditConveyorService {

    private Long idCounter = 1L;

    private static final BigDecimal MONTH_IN_YEAR = new BigDecimal("12");

    private static final BigDecimal NUMBER_PERCENTAGE = BigDecimal.valueOf(0.01);

    private static final BigDecimal ONE_RATIO = BigDecimal.ONE;

    private static final BigDecimal INSURANCE_RATIO = BigDecimal.valueOf(0.1);

    private static final BigDecimal REDUCING_RATIO_INSURANCE_ENABLED = new BigDecimal("2");

    private static final BigDecimal REDUCING_RATIO_IS_CLIENT = BigDecimal.ONE;

    private static final String REJECT_APPLICATION = "Отказ";

    private static final String FINAL_RATE = "Final rate equals: {}";

    @Value("${rate.property}")
    private String globalRate;

    private Long generateId() {
        return idCounter++;
    }


    public List<LoanOfferDTO> preCalculateLoan(LoanApplicationRequestDTO dto) {
        log.info("Got request for creating loan offers for {}", dto);

        log.debug("Try to map dto to model");
        LoanApplication loanApplication = LoanApplicationMapper.toApplication(dto);
        log.info("Check mapping of dto to model {}", loanApplication);

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
                        .map(LoanOfferMapper::toDto)
                        .toList();
    }


    private LoanOffer createPreOffers(BigDecimal requestedAmount, Integer term, Boolean isInsuranceEnabled, Boolean isSalaryClient) {
        log.info("Preparing offer with parameters: requestedAmount = {}, term = {}, isInsuranceEnabled = {}, isSalaryClient = {}"
                , requestedAmount, term, isInsuranceEnabled, isSalaryClient);

        Long applicationId = generateId();
        log.info("Loan application id: {}", applicationId);

        BigDecimal preRate = preCalculateRate(isInsuranceEnabled, isSalaryClient);
        log.info("Loan application rate for application with id {}: {}", applicationId, preRate);

        BigDecimal amount = calculateAmount(requestedAmount, isInsuranceEnabled);
        log.info("Loan application amount for application with id {}: {}", applicationId, amount);

        BigDecimal monthlyPayment = calculateMonthlyPayment(preRate, term, amount);
        log.info("Loan application monthly payment for application with id {}: {}", applicationId, monthlyPayment);

        BigDecimal totalAmount = calculateTotalAmount(monthlyPayment, term);
        log.info("Loan application total amount for application with id {}: {}", applicationId, totalAmount);

        return new LoanOffer(applicationId, amount, totalAmount, term, monthlyPayment, preRate, isInsuranceEnabled, isSalaryClient);
    }


    private BigDecimal preCalculateRate(Boolean isInsuranceEnabled, Boolean isSalaryClient) {
        log.info("Calculate rate with parameters: isInsuranceEnabled = {}, isSalaryClient = {}"
                , isInsuranceEnabled, isSalaryClient);

        BigDecimal rate = new BigDecimal(globalRate);
        log.info("Initial rate equals: {}", rate);

        if (Boolean.TRUE.equals(isInsuranceEnabled)) {
            rate = rate.subtract(REDUCING_RATIO_INSURANCE_ENABLED);
        }

        if (Boolean.TRUE.equals(isSalaryClient)) {
            rate = rate.subtract(REDUCING_RATIO_IS_CLIENT);
        }

        log.info("Calculated rate equals: {}", rate);
        return rate;
    }

    private BigDecimal calculateAmount(BigDecimal requestedAmount, Boolean isInsuranceEnabled) {
        log.info("Calculate amount with parameters: requestedAmount = {}, isInsuranceEnabled = {}"
                , requestedAmount, isInsuranceEnabled);

        BigDecimal amount = requestedAmount;
        BigDecimal insuranceCost = BigDecimal.ZERO;

        if (Boolean.TRUE.equals(isInsuranceEnabled)) {
            insuranceCost = requestedAmount.multiply(INSURANCE_RATIO);
            amount = requestedAmount.add(insuranceCost);
        }

        log.info("Calculated amount equals: {}, insuranceCost: {}", amount, insuranceCost);
        return amount;
    }


    private BigDecimal calculateMonthlyPayment(BigDecimal rate, Integer term, BigDecimal amount) {
        log.info("Calculate monthly payment with parameters: rate = {}, term = {}, amount = {}"
                , rate, term, amount);

        BigDecimal ratePerMonth = rate.divide(MONTH_IN_YEAR, 5, RoundingMode.HALF_UP);
        log.info("Rate per month equals: {}", ratePerMonth);

        BigDecimal numericRatePerMonth = ratePerMonth.multiply(NUMBER_PERCENTAGE);
        log.info("Numeric rate per month equals: {}", numericRatePerMonth);

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
        log.info("Annuity rate equals: {}", annuityRate);

        return amount.multiply(annuityRate).setScale(2, RoundingMode.HALF_EVEN);

    }

    private BigDecimal calculateTotalAmount(BigDecimal monthlyPayment, Integer term) {
        log.info("Calculate monthly payment with parameters: monthlyPayment = {}, term = {}"
                , monthlyPayment, term);
        return monthlyPayment.multiply(new BigDecimal(term)).setScale(2, RoundingMode.HALF_EVEN);
    }


    public CreditDTO calculateLoan(ScoringDataDTO dto) {
        log.info("Got request for creating a new credit offer: full calculating for {}", dto);

        log.debug("Try to map dto to model");
        ScoringData fullInfoAboutClient = ScoringDataMapper.toData(dto);
        log.info("Check mapping of dto to model {}", fullInfoAboutClient);

        log.info("Do checks");
        checkEmploymentStatus(fullInfoAboutClient);
        checkPositionAndEmployerINN(fullInfoAboutClient);
        checkSalary(fullInfoAboutClient);
        checkAge(fullInfoAboutClient);
        checkWorkExperience(fullInfoAboutClient);

        log.info("Get rate");
        BigDecimal finalRate = calculateFinalRate(fullInfoAboutClient);
        log.info("Final rate calculated: {}", finalRate);

        log.info("Get amount");
        BigDecimal amount = calculateAmount(fullInfoAboutClient.getAmount(), fullInfoAboutClient.getIsInsuranceEnabled());
        log.info("Amount calculated: {}", amount);

        log.info("Get monthlyPayment");
        BigDecimal monthlyPayment = calculateMonthlyPayment(finalRate, fullInfoAboutClient.getTerm(), amount);
        log.info("MonthlyPayment calculated: {}", monthlyPayment);

        log.info("Get paymentSchedule");
        List<PaymentSchedule> paymentSchedule = preparePaymentSchedule(fullInfoAboutClient.getTerm(), amount, finalRate, monthlyPayment);
        log.info("Payment schedule calculated with {} elements", paymentSchedule.size());

        log.info("Get psk");
        BigDecimal psk = calculatePsk(paymentSchedule, amount, fullInfoAboutClient.getTerm());
        log.info("Psk calculated. Full credit cost in percent: {}", psk);

        log.info("Create credit");
        Credit credit = new Credit(amount, fullInfoAboutClient.getTerm(), monthlyPayment, finalRate, psk,
                fullInfoAboutClient.getIsInsuranceEnabled(), fullInfoAboutClient.getIsSalaryClient(), paymentSchedule);
        log.info("Credit calculated: {}", credit);

        log.debug("Try to map model to dto");
        return CreditMapper.toDto(credit);

    }

    private List<PaymentSchedule> preparePaymentSchedule(Integer term, BigDecimal amount, BigDecimal finalRate, BigDecimal monthlyPayment) {
        log.info("Preparing payment schedule list with parameters: term = {}, amount = {}, finalRate = {}, monthlyPayment = {}"
                , term, amount, finalRate, monthlyPayment);

        Integer id = 1;
        List<PaymentSchedule> paymentSchedules = new ArrayList<>();

        GregorianCalendar date = new GregorianCalendar();
        date.set(Calendar.DAY_OF_MONTH, 12);
        date.add(Calendar.MONTH, 1);
        log.info("Date of first payment: {}", date);

        BigDecimal remainingDebt = amount;
        log.info("Remaining debt equals: {}", remainingDebt);


        for (int i = 0; i < term; i++) {
            BigDecimal interestPayment;
            BigDecimal mainPayment;
            BigDecimal debtSum;
            BigDecimal totalPayment = monthlyPayment;

            log.debug("Create new payment schedule element");
            PaymentSchedule scheduleElement = PaymentSchedule.builder()
                                                .number(id)
                                                .date(date.getTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDate())
                                                .build();
            log.info("New payment schedule element equals: {}", scheduleElement);

            log.debug("Calculate interest payment for schedule element with id: {}", scheduleElement.getNumber());
            interestPayment = calculateInterestPayment(remainingDebt, finalRate,
                    new BigDecimal(date.getActualMaximum(Calendar.DAY_OF_MONTH)),
                    new BigDecimal(date.getActualMaximum(Calendar.DAY_OF_YEAR)));
            log.info("Interest payment for schedule element with id {} equals: {}", scheduleElement.getNumber(), interestPayment);

            log.debug("Calculate main payment for schedule element with id: {}", scheduleElement.getNumber());
            mainPayment = monthlyPayment.subtract(interestPayment);
            log.info("Main payment for schedule element with id {} equals: {}", scheduleElement.getNumber(), mainPayment);

            log.debug("Calculate remaining debt for schedule element with id: {}", scheduleElement.getNumber());
            debtSum = remainingDebt.subtract(mainPayment);
            log.info("Remaining debt for schedule element with id {} equals: {}", scheduleElement.getNumber(), debtSum);

            scheduleElement.setInterestPayment(interestPayment);


            if (i == term - 1) {
                log.info("This is the last payment. Add remain debt to the last schedule element");
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
        log.info("Calculating Interest payment with parameters: remainingDebt = {}, finalRate = {}, daysOfMonth = {}, daysOfYear = {}"
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
                .divide(MONTH_IN_YEAR, 5, RoundingMode.HALF_UP), 5, RoundingMode.HALF_UP)
                .multiply(new BigDecimal(100)).setScale(3, RoundingMode.HALF_EVEN);

        return pskInPercent;
    }


    private BigDecimal calculateFinalRate(ScoringData clientInfo) {
        BigDecimal rate = preCalculateRate(clientInfo.getIsInsuranceEnabled(), clientInfo.getIsSalaryClient());

        if (clientInfo.getEmployment().getEmploymentStatus().equals(EmploymentStatus.SELF_EMPLOYED)) {
            log.info("Add ratio 1 for self-employed clients");
            rate = rate.add(BigDecimal.ONE);
            log.info(FINAL_RATE, rate);
        }

        if (clientInfo.getEmployment().getEmploymentStatus().equals(EmploymentStatus.BUSINESS_OWNER)) {
            log.info("Add ratio 3 for business owners");
            rate = rate.add(new BigDecimal(3));
            log.info(FINAL_RATE, rate);
        }

        if (clientInfo.getEmployment().getPosition().equals(Position.MANAGER)) {
            log.info("Subtract ratio 2 for managers");
            rate = rate.subtract(new BigDecimal(2));
            log.info(FINAL_RATE, rate);
        }

        if (clientInfo.getEmployment().getPosition().equals(Position.TOP_MANAGER)) {
            log.info("Subtract ratio 4 for top managers");
            rate = rate.subtract(new BigDecimal(4));
            log.info(FINAL_RATE, rate);
        }

        if (clientInfo.getMaritalStatus().equals(MaritalStatus.MARRIED)) {
            log.info("Subtract ratio 3 for married clients");
            rate = rate.subtract(new BigDecimal(3));
            log.info(FINAL_RATE, rate);
        }

        if (clientInfo.getMaritalStatus().equals(MaritalStatus.DIVORCED)) {
            log.info("Add ratio 1 for divorced clients");
            rate = rate.add(BigDecimal.ONE);
            log.info(FINAL_RATE, rate);
        }

        if (clientInfo.getDependentAmount() > 1) {
            log.info("Add ratio 1 for clients with dependents");
            rate = rate.add(BigDecimal.ONE);
            log.info(FINAL_RATE, rate);
        }

        if (clientInfo.getGender().equals(Gender.FEMALE)
                && LocalDate.now().minusYears(35).isAfter(clientInfo.getBirthdate())
                && LocalDate.now().minusYears(60).isBefore(clientInfo.getBirthdate())) {
            log.info("Subtract ratio 3 for women from 35 to 60");
            rate = rate.subtract(new BigDecimal(3));
            log.info(FINAL_RATE, rate);

        }

        if (clientInfo.getGender().equals(Gender.MALE)
                && LocalDate.now().minusYears(30).isAfter(clientInfo.getBirthdate())
                && LocalDate.now().minusYears(55).isBefore(clientInfo.getBirthdate())) {
            log.info("Subtract ratio 3 for men from 30 to 55");
            rate = rate.subtract(new BigDecimal(3));
            log.info(FINAL_RATE, rate);

        }

        if (clientInfo.getGender().equals(Gender.NON_BINARY)) {
            log.info("Add ratio 3 for non-binary clients");
            rate = rate.add(new BigDecimal(3));
            log.info(FINAL_RATE, rate);

        }

        return rate;
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
        if (fullInfoAboutClient.getEmployment().getSalary().multiply(new BigDecimal(20))
                               .compareTo(fullInfoAboutClient.getAmount()) < 0) {
            log.error("Client salary is not enough");
            throw new NotProperClientCategoryException(REJECT_APPLICATION);
        }
    }

    private void checkAge(ScoringData fullInfoAboutClient) {
        if (LocalDate.now().minusYears(20).isBefore(fullInfoAboutClient.getBirthdate())
                || LocalDate.now().minusYears(60).isAfter(fullInfoAboutClient.getBirthdate())) {
            log.error("Client age is not proper");
            throw new NotProperClientCategoryException(REJECT_APPLICATION);
        }
    }

    private void checkWorkExperience(ScoringData fullInfoAboutClient) {
        if (fullInfoAboutClient.getEmployment().getWorkExperienceTotal() < 12
                || fullInfoAboutClient.getEmployment().getWorkExperienceCurrent() < 3) {
            log.error("Client work experience is not enough");
            throw new NotProperClientCategoryException(REJECT_APPLICATION);
        }
    }


}
