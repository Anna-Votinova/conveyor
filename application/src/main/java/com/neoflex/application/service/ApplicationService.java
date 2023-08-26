package com.neoflex.application.service;

import com.neoflex.application.dto.LoanApplicationRequestDTO;
import com.neoflex.application.dto.LoanOfferDTO;
import com.neoflex.application.dto.error.Violation;
import com.neoflex.application.exception.PreScoringException;
import com.neoflex.application.integration.deal.DealClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ApplicationService {

    public final DealClient dealClient;
    private static final String EMAIL_FORMAT = "[\\w\\.]{2,50}@[\\w\\.]{2,20}";
    private static final String LATIN_LANG = "[a-zA-Z]+";
    private static final String PASSPORT_SERIES_FORMAT = "[\\d]{4}";
    private static final String PASSPORT_NUMBER_FORMAT = "[\\d]{6}";
    private static final BigDecimal MIN_AMOUNT = new BigDecimal("10000");
    private static final Integer MIN_TERM = 6;
    private static final int MIN_NAME_LENGTH = 2;
    private static final int MAX_NAME_LENGTH = 30;
    private static final long MIN_CLIENT_AGE = 18L;

    /**
     * <p>Checks short info about the client. If the check passes, sends info to the Deal and receives an offers list
     * </p>
     * @param loanApplicationRequestDTO short information about the client
     * @return a list with four offers
     * @throws com.neoflex.application.exception.BadRequestException - if the LoanApplicationRequestDTO sent to the Deal
     * is invalid
     */
    public List<LoanOfferDTO> preScoreOffers(LoanApplicationRequestDTO loanApplicationRequestDTO) {
        validateClientInfo(loanApplicationRequestDTO);

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

    private void validateClientInfo(LoanApplicationRequestDTO loanApplication) {
        List<Violation> violations = new ArrayList<>();

        if (loanApplication.amount().compareTo(MIN_AMOUNT) < 0) {
            violations.add(new Violation("amount",
                    "Запрашиваемая сумма займа не должна быть меньше 10000"));
        }

        if (loanApplication.term() < MIN_TERM) {
            violations.add(new Violation("term", "Срок кредита не может быть меньше 6 месяцев"));
        }

        if (loanApplication.firstName().length() < MIN_NAME_LENGTH
                || loanApplication.firstName().length() > MAX_NAME_LENGTH) {
            violations.add(new Violation("firstName",
                    "Длина имени не может быть меньше 2 и больше 30 символов"));
        }

        if (!loanApplication.firstName().matches(LATIN_LANG)) {
            violations.add(new Violation("firstName", "Имя должно быть написано латинскими буквами"));
        }

        if (loanApplication.lastName().length() < MIN_NAME_LENGTH
                || loanApplication.lastName().length() > MAX_NAME_LENGTH) {
            violations.add(new Violation("lastName",
                    "Длина фамилии не может быть меньше 2 и больше 30 символов"));
        }

        if (!loanApplication.lastName().matches(LATIN_LANG)) {
            violations.add(new Violation("lastName",
                    "Фамилия должна быть написана латинскими буквами"));
        }

        if (loanApplication.middleName().length() < MIN_NAME_LENGTH
                || loanApplication.middleName().length() > MAX_NAME_LENGTH) {
            violations.add(new Violation("middleName",
                    "Длина отчества не может быть меньше 2 и больше 30 символов"));
        }

        if (!loanApplication.middleName().matches(LATIN_LANG)) {
            violations.add(new Violation("middleName",
                    "Отчество должно быть написано латинскими буквами"));
        }

        if (!loanApplication.email().matches(EMAIL_FORMAT)) {
            violations.add(new Violation("email",
                    "Название электронной почты должно соответветствовать общепринятым стандартам"));
        }

        if (loanApplication.birthdate().isAfter(LocalDate.now().minusYears(MIN_CLIENT_AGE))) {
            violations.add(new Violation("birthdate",
                    "Заемщик должен быть совершеннолетним"));
        }

        if (!loanApplication.passportSeries().matches(PASSPORT_SERIES_FORMAT)) {
            violations.add(new Violation("passportSeries",
                    "Серия паспорта должна содержать 4 цифры"));
        }

        if (!loanApplication.passportNumber().matches(PASSPORT_NUMBER_FORMAT)) {
            violations.add(new Violation("passportNumber",
                    "Номер паспорта должен содержать 6 цифр"));
        }
        log.info("Client info parameters checked. Found {} violations", violations.size());

        if (!violations.isEmpty()) {
            throw new PreScoringException("Прескоринг не пройден", violations);
        }
    }
}
