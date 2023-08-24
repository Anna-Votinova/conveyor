package com.neoflex.deal.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.neoflex.deal.dto.LoanOfferDTO;
import com.neoflex.deal.dto.request.EmploymentDTO;
import com.neoflex.deal.dto.request.FinishRegistrationRequestDTO;
import com.neoflex.deal.dto.LoanApplicationRequestDTO;
import com.neoflex.deal.entity.enums.EmploymentPosition;
import com.neoflex.deal.entity.enums.EmploymentStatus;
import com.neoflex.deal.entity.enums.Gender;
import com.neoflex.deal.entity.enums.MaritalStatus;
import com.neoflex.deal.service.DealService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = DealController.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class DealControllerTest {

    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private MockMvc mvc;
    @MockBean
    private DealService dealService;

    @Test
    @SneakyThrows
    @DisplayName("Testing method calculateCredit - positive scenario")
    void shouldReturnOk_WhenValidIdAndAdditionalClientInfo() {
        mvc.perform(put("/deal/calculate/1")
                   .content(mapper.writeValueAsString(getFullClientInfo()))
                   .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                   .characterEncoding(StandardCharsets.UTF_8))
           .andExpect(status().isOk());
    }

    @SneakyThrows
    @Test
    void shouldThrowException_WhenNegativeId() {
        mvc.perform(put("/deal/calculate/-1")
                   .content(mapper.writeValueAsString(getFullClientInfo()))
                   .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                   .characterEncoding(StandardCharsets.UTF_8))
           .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void shouldThrowException_WhenZeroId() {
        mvc.perform(put("/deal/calculate/0")
                   .content(mapper.writeValueAsString(getFullClientInfo()))
                   .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                   .characterEncoding(StandardCharsets.UTF_8))
           .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void shouldThrowException_WhenNullId() {
        mvc.perform(put("/deal/calculate/")
                   .content(mapper.writeValueAsString(getFullClientInfo()))
                   .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                   .characterEncoding(StandardCharsets.UTF_8))
           .andExpect(status().isNotFound());
    }

    @SneakyThrows
    @Test
    void shouldThrowException_WhenNullGender() {
        FinishRegistrationRequestDTO clientInfo = getFullClientInfo();
        clientInfo.setGender(null);

        mvc.perform(put("/deal/calculate/1")
                   .content(mapper.writeValueAsString(clientInfo))
                   .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                   .characterEncoding(StandardCharsets.UTF_8))
           .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void shouldThrowException_WhenNullMaritalStatus() {
        FinishRegistrationRequestDTO clientInfo = getFullClientInfo();
        clientInfo.setMaritalStatus(null);

        mvc.perform(put("/deal/calculate/1")
                   .content(mapper.writeValueAsString(clientInfo))
                   .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                   .characterEncoding(StandardCharsets.UTF_8))
           .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void shouldThrowException_WhenNegativeDependents() {
        FinishRegistrationRequestDTO clientInfo = getFullClientInfo();
        clientInfo.setDependentAmount(-1);

        mvc.perform(put("/deal/calculate/1")
                   .content(mapper.writeValueAsString(clientInfo))
                   .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                   .characterEncoding(StandardCharsets.UTF_8))
           .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void shouldThrowException_WhenNullPassportIssueDate() {
        FinishRegistrationRequestDTO clientInfo = getFullClientInfo();
        clientInfo.setPassportIssueDate(null);

        mvc.perform(put("/deal/calculate/1")
                   .content(mapper.writeValueAsString(clientInfo))
                   .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                   .characterEncoding(StandardCharsets.UTF_8))
           .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void shouldThrowException_WhenFuturePassportIssueDate() {
        FinishRegistrationRequestDTO clientInfo = getFullClientInfo();
        clientInfo.setPassportIssueDate(LocalDate.now().plusDays(1));

        mvc.perform(put("/deal/calculate/1")
                   .content(mapper.writeValueAsString(clientInfo))
                   .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                   .characterEncoding(StandardCharsets.UTF_8))
           .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @ParameterizedTest
    @ValueSource(strings = {"", " "})
    void shouldThrowException_WhenBlankPassportIssueBranch(String branch) {
        FinishRegistrationRequestDTO clientInfo = getFullClientInfo();
        clientInfo.setPassportIssueBranch(branch);

        mvc.perform(put("/deal/calculate/1")
                   .content(mapper.writeValueAsString(clientInfo))
                   .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                   .characterEncoding(StandardCharsets.UTF_8))
           .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @ParameterizedTest
    @ValueSource(strings = {"", " ", "1234567890123456789", "123456789012345678901", "TwentyAccountStrings"})
    void shouldThrowException_WhenInvalidAccount(String account) {
        FinishRegistrationRequestDTO clientInfo = getFullClientInfo();
        clientInfo.setAccount(account);

        mvc.perform(put("/deal/calculate/1")
                   .content(mapper.writeValueAsString(clientInfo))
                   .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                   .characterEncoding(StandardCharsets.UTF_8))
           .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void shouldThrowException_WhenNullEmploymentStatus() {
        FinishRegistrationRequestDTO clientInfo = getFullClientInfo();
        clientInfo.getEmployment().setEmploymentStatus(null);

        mvc.perform(put("/deal/calculate/1")
                   .content(mapper.writeValueAsString(clientInfo))
                   .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                   .characterEncoding(StandardCharsets.UTF_8))
           .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void shouldThrowException_WhenNullSalary() {
        FinishRegistrationRequestDTO clientInfo = getFullClientInfo();
        clientInfo.getEmployment().setSalary(null);

        mvc.perform(put("/deal/calculate/1")
                   .content(mapper.writeValueAsString(clientInfo))
                   .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                   .characterEncoding(StandardCharsets.UTF_8))
           .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void shouldThrowException_WhenNegativeSalary() {
        FinishRegistrationRequestDTO clientInfo = getFullClientInfo();
        clientInfo.getEmployment().setSalary(new BigDecimal("-1"));

        mvc.perform(put("/deal/calculate/1")
                   .content(mapper.writeValueAsString(clientInfo))
                   .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                   .characterEncoding(StandardCharsets.UTF_8))
           .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void shouldThrowException_WhenNegativeWorkExperienceTotal() {
        FinishRegistrationRequestDTO clientInfo = getFullClientInfo();
        clientInfo.getEmployment().setWorkExperienceTotal(-1);

        mvc.perform(put("/deal/calculate/1")
                   .content(mapper.writeValueAsString(clientInfo))
                   .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                   .characterEncoding(StandardCharsets.UTF_8))
           .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void shouldThrowException_WhenNegativeWorkExperienceCurrent() {
        FinishRegistrationRequestDTO clientInfo = getFullClientInfo();
        clientInfo.getEmployment().setWorkExperienceCurrent(-1);

        mvc.perform(put("/deal/calculate/1")
                   .content(mapper.writeValueAsString(clientInfo))
                   .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                   .characterEncoding(StandardCharsets.UTF_8))
           .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    @DisplayName("Testing method chooseOffer - positive scenario")
    void shouldReturnOk_WhenValidRequestDto() {
        mvc.perform(put("/deal/offer")
                   .content(mapper.writeValueAsString(getLoanOffer()))
                   .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                   .characterEncoding(StandardCharsets.UTF_8))
           .andExpect(status().isOk());

        verify(dealService).chooseOffer(any());
    }

    @SneakyThrows
    @Test
    void shouldThrowException_WhenNullApplicationId() {
        LoanOfferDTO loanOfferDTO = getLoanOffer();
        loanOfferDTO.setApplicationId(null);

        mvc.perform(put("/deal/offer")
                   .content(mapper.writeValueAsString(loanOfferDTO))
                   .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                   .characterEncoding(StandardCharsets.UTF_8))
           .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @ParameterizedTest
    @ValueSource(longs = {0, -1})
    void shouldThrowException_WhenNegativeOrZeroApplicationId(long id) {
        LoanOfferDTO loanOfferDTO = getLoanOffer();
        loanOfferDTO.setApplicationId(id);

        mvc.perform(put("/deal/offer")
                   .content(mapper.writeValueAsString(loanOfferDTO))
                   .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                   .characterEncoding(StandardCharsets.UTF_8))
           .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void shouldThrowException_WhenRequestedAmountLessThenTenThousand() {
        LoanOfferDTO loanOfferDTO = getLoanOffer();
        loanOfferDTO.setRequestedAmount(new BigDecimal("9999"));

        mvc.perform(put("/deal/offer")
                   .content(mapper.writeValueAsString(loanOfferDTO))
                   .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                   .characterEncoding(StandardCharsets.UTF_8))
           .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void shouldThrowException_WhenNullRequestedAmount() {
        LoanOfferDTO loanOfferDTO = getLoanOffer();
        loanOfferDTO.setRequestedAmount(null);

        mvc.perform(put("/deal/offer")
                   .content(mapper.writeValueAsString(loanOfferDTO))
                   .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                   .characterEncoding(StandardCharsets.UTF_8))
           .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void shouldThrowException_WhenTotalAmountLessThenTenThousand() {
        LoanOfferDTO loanOfferDTO = getLoanOffer();
        loanOfferDTO.setTotalAmount(new BigDecimal("9999"));

        mvc.perform(put("/deal/offer")
                   .content(mapper.writeValueAsString(loanOfferDTO))
                   .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                   .characterEncoding(StandardCharsets.UTF_8))
           .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void shouldThrowException_WhenNullTotalAmount() {
        LoanOfferDTO loanOfferDTO = getLoanOffer();
        loanOfferDTO.setTotalAmount(null);

        mvc.perform(put("/deal/offer")
                   .content(mapper.writeValueAsString(loanOfferDTO))
                   .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                   .characterEncoding(StandardCharsets.UTF_8))
           .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void shouldThrowException_WhenNullTerm() {
        LoanOfferDTO loanOfferDTO = getLoanOffer();
        loanOfferDTO.setTerm(null);

        mvc.perform(put("/deal/offer")
                   .content(mapper.writeValueAsString(loanOfferDTO))
                   .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                   .characterEncoding(StandardCharsets.UTF_8))
           .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void shouldThrowException_WhenTermLessThenSix() {
        LoanOfferDTO loanOfferDTO = getLoanOffer();
        loanOfferDTO.setTerm(5);

        mvc.perform(put("/deal/offer")
                   .content(mapper.writeValueAsString(loanOfferDTO))
                   .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                   .characterEncoding(StandardCharsets.UTF_8))
           .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void shouldThrowException_WhenNullMonthlyPayment() {
        LoanOfferDTO loanOfferDTO = getLoanOffer();
        loanOfferDTO.setMonthlyPayment(null);

        mvc.perform(put("/deal/offer")
                   .content(mapper.writeValueAsString(loanOfferDTO))
                   .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                   .characterEncoding(StandardCharsets.UTF_8))
           .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @ParameterizedTest
    @ValueSource(strings = {"0", "-1"})
    void shouldThrowException_WhenNegativeOrZeroMonthlyPayment(String id) {
        LoanOfferDTO loanOfferDTO = getLoanOffer();
        loanOfferDTO.setMonthlyPayment(new BigDecimal(id));

        mvc.perform(put("/deal/offer")
                   .content(mapper.writeValueAsString(loanOfferDTO))
                   .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                   .characterEncoding(StandardCharsets.UTF_8))
           .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @ParameterizedTest
    @ValueSource(strings = {"0", "-1"})
    void shouldThrowException_WhenNegativeOrZeroRate(String id) {
        LoanOfferDTO loanOfferDTO = getLoanOffer();
        loanOfferDTO.setRate(new BigDecimal(id));

        mvc.perform(put("/deal/offer")
                   .content(mapper.writeValueAsString(loanOfferDTO))
                   .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                   .characterEncoding(StandardCharsets.UTF_8))
           .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void shouldThrowException_WhenNullRate() {
        LoanOfferDTO loanOfferDTO = getLoanOffer();
        loanOfferDTO.setRate(null);

        mvc.perform(put("/deal/offer")
                   .content(mapper.writeValueAsString(loanOfferDTO))
                   .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                   .characterEncoding(StandardCharsets.UTF_8))
           .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void shouldThrowException_WhenNullInsuranceEnabled() {
        LoanOfferDTO loanOfferDTO = getLoanOffer();
        loanOfferDTO.setIsInsuranceEnabled(null);

        mvc.perform(put("/deal/offer")
                   .content(mapper.writeValueAsString(loanOfferDTO))
                   .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                   .characterEncoding(StandardCharsets.UTF_8))
           .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void shouldThrowException_WhenNullSalaryClient() {
        LoanOfferDTO loanOfferDTO = getLoanOffer();
        loanOfferDTO.setIsSalaryClient(null);

        mvc.perform(put("/deal/offer")
                   .content(mapper.writeValueAsString(loanOfferDTO))
                   .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                   .characterEncoding(StandardCharsets.UTF_8))
           .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    @DisplayName("Testing method calculateOffers - positive scenario")
    void shouldReturnStatusOk_WhenValidStartClientInfo() {
        when(dealService.startRegistration(any())).thenReturn(Collections.emptyList());

        mvc.perform(post("/deal/application")
                   .content(mapper.writeValueAsString(getStartClientInfo()))
                   .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                   .characterEncoding(StandardCharsets.UTF_8))
           .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    void shouldThrowException_WhenDtoWithSmallAmount() {
        LoanApplicationRequestDTO startClientInfo = getStartClientInfo();
        startClientInfo.setAmount(new BigDecimal("9999"));

        mvc.perform(post("/deal/application")
                   .content(mapper.writeValueAsString(startClientInfo))
                   .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                   .characterEncoding(StandardCharsets.UTF_8))
           .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void shouldThrowException_WhenNullAmount() {
        LoanApplicationRequestDTO startClientInfo = getStartClientInfo();
        startClientInfo.setAmount(null);

        mvc.perform(post("/deal/application")
                   .content(mapper.writeValueAsString(startClientInfo))
                   .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                   .characterEncoding(StandardCharsets.UTF_8))
           .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void shouldThrowException_WhenStartInfoNullTerm() {
        LoanApplicationRequestDTO startClientInfo = getStartClientInfo();
        startClientInfo.setTerm(null);

        mvc.perform(post("/deal/application")
                   .content(mapper.writeValueAsString(startClientInfo))
                   .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                   .characterEncoding(StandardCharsets.UTF_8))
           .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void shouldThrowException_WhenStartInfoTermLessThenSix() {
        LoanApplicationRequestDTO startClientInfo = getStartClientInfo();
        startClientInfo.setTerm(5);

        mvc.perform(post("/deal/application")
                   .content(mapper.writeValueAsString(startClientInfo))
                   .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                   .characterEncoding(StandardCharsets.UTF_8))
           .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @ParameterizedTest
    @ValueSource(strings = {"    ", "111", "A", "ItIsAVeryLongNameForTestingConstraint", "Анна"})
    void shouldThrowException_WhenInvalidFormStartInfoFirstname(String name) {
        LoanApplicationRequestDTO startClientInfo = getStartClientInfo();
        startClientInfo.setFirstName(name);

        mvc.perform(post("/deal/application")
                   .content(mapper.writeValueAsString(startClientInfo))
                   .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                   .characterEncoding(StandardCharsets.UTF_8))
           .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void shouldThrowException_WhenNullStartInfoFirstname() {
        LoanApplicationRequestDTO startClientInfo = getStartClientInfo();
        startClientInfo.setFirstName(null);

        mvc.perform(post("/deal/application")
                   .content(mapper.writeValueAsString(startClientInfo))
                   .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                   .characterEncoding(StandardCharsets.UTF_8))
           .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @ParameterizedTest
    @ValueSource(strings = {"    ", "111", "A", "ItIsAVeryLongLastNameForTestingConstraint", "Блэк"})
    void shouldThrowException_WhenInvalidFormStartInfoLastname(String name) {
        LoanApplicationRequestDTO startClientInfo = getStartClientInfo();
        startClientInfo.setLastName(name);

        mvc.perform(post("/deal/application")
                   .content(mapper.writeValueAsString(startClientInfo))
                   .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                   .characterEncoding(StandardCharsets.UTF_8))
           .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void shouldThrowException_WhenNullStartInfoLastname() {
        LoanApplicationRequestDTO startClientInfo = getStartClientInfo();
        startClientInfo.setLastName(null);

        mvc.perform(post("/deal/application")
                   .content(mapper.writeValueAsString(startClientInfo))
                   .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                   .characterEncoding(StandardCharsets.UTF_8))
           .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @ParameterizedTest
    @ValueSource(strings = {"    ", "111", "A", "ItIsAVeryLongMiddleNameForTestingConstraint", "Игоревна"})
    void shouldThrowException_WhenInvalidFormStartInfoMiddleName(String name) {
        LoanApplicationRequestDTO startClientInfo = getStartClientInfo();
        startClientInfo.setMiddleName(name);

        mvc.perform(post("/deal/application")
                   .content(mapper.writeValueAsString(startClientInfo))
                   .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                   .characterEncoding(StandardCharsets.UTF_8))
           .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @ParameterizedTest
    @ValueSource(strings = {"", " ", "emailyandex.ru", "@yandex.ru", "emailyandex"})
    void shouldThrowException_WhenDtoWithInvalidEmail(String email) {
        LoanApplicationRequestDTO startClientInfo = getStartClientInfo();
        startClientInfo.setEmail(email);

        mvc.perform(post("/deal/application")
                   .content(mapper.writeValueAsString(startClientInfo))
                   .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                   .characterEncoding(StandardCharsets.UTF_8))
           .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    void shouldThrowException_WhenMinorClient() {
        LoanApplicationRequestDTO startClientInfo = getStartClientInfo();
        startClientInfo.setBirthdate(LocalDate.now().minusYears(17));

        mvc.perform(post("/deal/application")
                   .content(mapper.writeValueAsString(startClientInfo))
                   .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                   .characterEncoding(StandardCharsets.UTF_8))
           .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    void shouldThrowException_WhenNullClientBirthday() {
        LoanApplicationRequestDTO startClientInfo = getStartClientInfo();
        startClientInfo.setBirthdate(null);

        mvc.perform(post("/deal/application")
                   .content(mapper.writeValueAsString(startClientInfo))
                   .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                   .characterEncoding(StandardCharsets.UTF_8))
           .andExpect(status().isInternalServerError());
    }

    @SneakyThrows
    @ParameterizedTest
    @ValueSource(strings = {"", " ", "123", "12345", "Four"})
    void shouldThrowException_WhenInvalidPassportSeries(String series) {
        LoanApplicationRequestDTO startClientInfo = getStartClientInfo();
        startClientInfo.setPassportSeries(series);

        mvc.perform(post("/deal/application")
                   .content(mapper.writeValueAsString(startClientInfo))
                   .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                   .characterEncoding(StandardCharsets.UTF_8))
           .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @ParameterizedTest
    @ValueSource(strings = {"", " ", "12345", "1234567", "SixNum"})
    void shouldThrowException_WhenInvalidPassportNumber(String number) {
        LoanApplicationRequestDTO startClientInfo = getStartClientInfo();
        startClientInfo.setPassportNumber(number);

        mvc.perform(post("/deal/application")
                   .content(mapper.writeValueAsString(startClientInfo))
                   .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                   .characterEncoding(StandardCharsets.UTF_8))
           .andExpect(status().isBadRequest());
    }

    private LoanApplicationRequestDTO getStartClientInfo() {
        return new LoanApplicationRequestDTO(
                null,
                new BigDecimal("10000.0"),
                6,
                "Anna",
                "Black",
                "White",
                "black@yandex.ru",
                LocalDate.of(1990, 12, 13),
                "1111",
                "111111"
        );
    }

    private LoanOfferDTO getLoanOffer() {
        return new LoanOfferDTO(
                1L,
                new BigDecimal("10000"),
                new BigDecimal("10412.40"),
                6,
                new BigDecimal("1735.40"),
                new BigDecimal("14"),
                false,
                true
        );
    }

    private FinishRegistrationRequestDTO getFullClientInfo() {

        EmploymentDTO employment = new EmploymentDTO(
                EmploymentStatus.EMPLOYED,
                "325507450247",
                new BigDecimal("70000"),
                EmploymentPosition.MID_MANAGER,
                144,
                110
        );

        return new FinishRegistrationRequestDTO(
                Gender.FEMALE,
                MaritalStatus.MARRIED,
                0,
                LocalDate.of(2021, 1, 12),
                "The main Directorate of the MIA of the Moscow",
                employment,
                "12345678901234567890"
        );
    }
}