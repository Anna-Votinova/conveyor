package com.neoflex.application.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.neoflex.application.dto.LoanApplicationRequestDTO;
import com.neoflex.application.dto.LoanOfferDTO;
import com.neoflex.application.exception.BadRequestException;
import com.neoflex.application.service.ApplicationService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ApplicationController.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class ApplicationControllerTest {

    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private MockMvc mvc;
    @MockBean
    private ApplicationService applicationService;

    @Test
    void shouldReturnStatusOk_WhenValidClientInfo() throws Exception {
        when(applicationService.prepareOffers(any())).thenReturn(Collections.emptyList());

        mvc.perform(post("/application")
                   .content(mapper.writeValueAsString(getClientInfo()))
                   .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                   .characterEncoding(StandardCharsets.UTF_8))
           .andExpect(status().isOk());
    }

    @Test
    void shouldThrowException_WhenDealThrowsValidationException() throws Exception {
        when(applicationService.prepareOffers(any())).thenThrow(BadRequestException.class);

        mvc.perform(post("/application")
                   .content(mapper.writeValueAsString(getClientInfo()))
                   .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                   .characterEncoding(StandardCharsets.UTF_8))
           .andExpect(status().isBadRequest());
    }

    @Test
    void shouldThrowException_WhenInvalidClientBirthday() throws Exception {
        var invalidClientInfo = new LoanApplicationRequestDTO(
                null,
                new BigDecimal("10000.0"),
                6,
                "Anna",
                "Black",
                "White",
                "black@yandex.ru",
                LocalDate.now().minusYears(17),
                "1111",
                "111111"
        );

        mvc.perform(post("/application")
                   .content(mapper.writeValueAsString(invalidClientInfo))
                   .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                   .characterEncoding(StandardCharsets.UTF_8))
           .andExpect(status().isBadRequest())
           .andExpect(jsonPath("violations[0].message")
                   .value("Заемщик должен быть совершеннолетним"))
           .andDo(print());
    }

    @Test
    void shouldReturnStatusOk_WhenValidLoanOffer() throws Exception {
        var validLoanOffer = new LoanOfferDTO(
                1L,
                new BigDecimal("10000"),
                new BigDecimal("10412.40"),
                6,
                new BigDecimal("1735.40"),
                new BigDecimal("14"),
                false,
                true
        );

        mvc.perform(put("/application/offer")
                   .content(mapper.writeValueAsString(validLoanOffer))
                   .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                   .characterEncoding(StandardCharsets.UTF_8))
           .andExpect(status().isOk());
    }

    @Test
    void shouldThrowException_WhenInvalidAmount() throws Exception {
        var invalidLoanOffer = new LoanOfferDTO(
                1L,
                new BigDecimal("9999"),
                new BigDecimal("10412.40"),
                6,
                new BigDecimal("1735.40"),
                new BigDecimal("14"),
                false,
                true
        );

        mvc.perform(put("/application/offer")
                   .content(mapper.writeValueAsString(invalidLoanOffer))
                   .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                   .characterEncoding(StandardCharsets.UTF_8))
           .andExpect(status().isBadRequest())
           .andExpect(jsonPath("violations[0].message")
                   .value("must be greater than or equal to 10000"))
           .andDo(print());
    }

    private LoanApplicationRequestDTO getClientInfo() {
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
}