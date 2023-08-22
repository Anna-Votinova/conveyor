package com.neoflex.conveyor.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.neoflex.conveyor.dto.mapper.CreditMapper;
import com.neoflex.conveyor.dto.mapper.LoanApplicationMapper;
import com.neoflex.conveyor.dto.mapper.LoanOfferMapper;
import com.neoflex.conveyor.dto.mapper.ScoringDataMapper;
import com.neoflex.conveyor.dto.request.LoanApplicationRequestDTO;
import com.neoflex.conveyor.service.CreditService;
import com.neoflex.conveyor.service.LoanOfferService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
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
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = CreditConveyorController.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class CreditConveyorControllerTest {

    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private MockMvc mvc;
    @MockBean
    private LoanOfferService loanOfferService;
    @MockBean
    private CreditService creditService;
    @MockBean
    private CreditMapper creditMapper;
    @MockBean
    private ScoringDataMapper scoringDataMapper;
    @MockBean
    private LoanApplicationMapper loanApplicationMapper;
    @MockBean
    private LoanOfferMapper loanOfferMapper;
    private LoanApplicationRequestDTO loanApplicationRequestDTO;

    @BeforeEach
    void setUp() {
        loanApplicationRequestDTO = new LoanApplicationRequestDTO(
                1L,
                new BigDecimal("10000"),
                6,
                "Anna",
                "Black",
                "White",
                "anyvotinova@yandex.ru",
                LocalDate.of(1990, 12, 13),
                "1111",
                "111111"
        );
    }

    @Test
    @SneakyThrows
    void shouldReturnStatusOk_WhenValidDto() {

        when(loanOfferService.preCalculateLoan(any())).thenReturn(Collections.emptyList());

        mvc.perform(post("/conveyor/offers")
                   .content(mapper.writeValueAsString(loanApplicationRequestDTO))
                   .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                   .characterEncoding(StandardCharsets.UTF_8))
           .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    void shouldThrowException_WhenDtoWithInvalidAmount() {

        loanApplicationRequestDTO.setAmount(new BigDecimal("9999"));

        mvc.perform(post("/conveyor/offers")
                   .content(mapper.writeValueAsString(loanApplicationRequestDTO))
                   .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                   .characterEncoding(StandardCharsets.UTF_8))
           .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    void shouldThrowException_WhenDtoWithInvalidTerm() {

        loanApplicationRequestDTO.setTerm(5);

        mvc.perform(post("/conveyor/offers")
                   .content(mapper.writeValueAsString(loanApplicationRequestDTO))
                   .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                   .characterEncoding(StandardCharsets.UTF_8))
           .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    void shouldThrowException_WhenDtoWithFirstnameInRussian() {

        loanApplicationRequestDTO.setFirstName("Анна");

        mvc.perform(post("/conveyor/offers")
                   .content(mapper.writeValueAsString(loanApplicationRequestDTO))
                   .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                   .characterEncoding(StandardCharsets.UTF_8))
           .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    void shouldThrowException_WhenDtoWithLongFirstname() {

        String longFirstName = "ItIsAVeryLongNameForTestingConstraint";

        loanApplicationRequestDTO.setFirstName(longFirstName);

        mvc.perform(post("/conveyor/offers")
                   .content(mapper.writeValueAsString(loanApplicationRequestDTO))
                   .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                   .characterEncoding(StandardCharsets.UTF_8))
           .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    void shouldThrowException_WhenDtoWithInvalidEmail() {

        loanApplicationRequestDTO.setEmail("@yandex.ru");

        mvc.perform(post("/conveyor/offers")
                   .content(mapper.writeValueAsString(loanApplicationRequestDTO))
                   .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                   .characterEncoding(StandardCharsets.UTF_8))
           .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    void shouldThrowException_WhenDtoWithInvalidBirthday() {

        loanApplicationRequestDTO.setBirthdate(LocalDate.of(2020, 6, 12));

        mvc.perform(post("/conveyor/offers")
                   .content(mapper.writeValueAsString(loanApplicationRequestDTO))
                   .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                   .characterEncoding(StandardCharsets.UTF_8))
           .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    void shouldThrowException_WhenDtoWithInvalidPassportNumber() {

        loanApplicationRequestDTO.setPassportNumber("1");

        mvc.perform(post("/conveyor/offers")
                   .content(mapper.writeValueAsString(loanApplicationRequestDTO))
                   .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                   .characterEncoding(StandardCharsets.UTF_8))
           .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    void shouldThrowException_WhenDtoWithInvalidPassportSeries() {

        loanApplicationRequestDTO.setPassportSeries("11111111111");

        mvc.perform(post("/conveyor/offers")
                   .content(mapper.writeValueAsString(loanApplicationRequestDTO))
                   .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                   .characterEncoding(StandardCharsets.UTF_8))
           .andExpect(status().isBadRequest());
    }
}