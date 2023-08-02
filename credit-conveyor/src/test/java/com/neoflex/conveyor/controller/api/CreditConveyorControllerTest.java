package com.neoflex.conveyor.controller.api;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.neoflex.conveyor.model.LoanOffer;
import com.neoflex.conveyor.model.dto.LoanApplicationRequestDTO;
import com.neoflex.conveyor.service.CreditService;
import com.neoflex.conveyor.service.LoanOfferService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = CreditConveyorController.class)
class CreditConveyorControllerTest {

    @Autowired
    ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    @MockBean
    LoanOfferService loanOfferService;

    @MockBean
    CreditService creditService;

    private LoanApplicationRequestDTO dto;

    @BeforeEach
    void setUp() {
        dto = new LoanApplicationRequestDTO(new BigDecimal("10000"), 6, "Anna", "Black", "White",
                "anyvotinova@yandex.ru", LocalDate.of(1990, 12, 13), "1111", "111111");

    }

    @DisplayName("MockMvc test for preCalculateLoan method (positive scenario)")
    @Test
    void givenAnyObject_whenPreCalculateLoan_thenReturnLoanOfferDTO() throws Exception {

        LoanOffer plainOffer = new LoanOffer(1L, new BigDecimal("10000"), new BigDecimal("10441.80"),
                6, new BigDecimal("1740.30"), new BigDecimal("15"), false,false
        );

        LoanOffer offerForClient = new LoanOffer(2L, new BigDecimal("10000"), new BigDecimal("10412.40"),
                6, new BigDecimal("1735.40"), new BigDecimal("14"),false,true
        );

        LoanOffer offerWithIns = new LoanOffer(3L, new BigDecimal("11000"), new BigDecimal("11420.64"),
                6, new BigDecimal("1903.44"), new BigDecimal("13"),true,false
        );

        LoanOffer offerWithInsForClient = new LoanOffer(4L, new BigDecimal("11000"), new BigDecimal("11388.30"),
                6, new BigDecimal("1898.05"), new BigDecimal("12"), true,true
        );

        List<LoanOffer> expectedOfferList = List.of(plainOffer, offerForClient, offerWithIns, offerWithInsForClient);


        when(loanOfferService.preCalculateLoan(any())).thenReturn(expectedOfferList);

        mvc.perform(post("/conveyor/offers")
                   .content(mapper.writeValueAsString(dto))
                   .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                   .characterEncoding(StandardCharsets.UTF_8))
           .andExpect(status().isOk());

    }

    @DisplayName("MockMvc test for preCalculateLoan method (negative scenario)")
    @Test
    void givenDtoWithWrongAmount_whenPreCalculateLoan_thenThrowException() throws Exception {

        dto.setAmount(new BigDecimal("9999"));

        mvc.perform(post("/conveyor/offers")
                   .content(mapper.writeValueAsString(dto))
                   .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                   .characterEncoding(StandardCharsets.UTF_8))
           .andExpect(status().is(400));

    }

    @DisplayName("MockMvc test for preCalculateLoan method (negative scenario)")
    @Test
    void givenDtoWithWrongTerm_whenPreCalculateLoan_thenThrowException() throws Exception {

        dto.setTerm(5);

        mvc.perform(post("/conveyor/offers")
                   .content(mapper.writeValueAsString(dto))
                   .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                   .characterEncoding(StandardCharsets.UTF_8))
           .andExpect(status().is(400));

    }

    @DisplayName("MockMvc test for preCalculateLoan method (negative scenario)")
    @Test
    void givenDtoWithWrongName_whenPreCalculateLoan_thenThrowException() throws Exception {

        dto.setFirstName("Анна");

        mvc.perform(post("/conveyor/offers")
                   .content(mapper.writeValueAsString(dto))
                   .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                   .characterEncoding(StandardCharsets.UTF_8))
           .andExpect(status().is(400));

    }

    @DisplayName("MockMvc test for preCalculateLoan method (negative scenario)")
    @Test
    void givenDtoWithLongName_whenPreCalculateLoan_thenThrowException() throws Exception {

        dto.setFirstName("Annnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnna");

        mvc.perform(post("/conveyor/offers")
                   .content(mapper.writeValueAsString(dto))
                   .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                   .characterEncoding(StandardCharsets.UTF_8))
           .andExpect(status().is(400));

    }

    @DisplayName("MockMvc test for preCalculateLoan method (negative scenario)")
    @Test
    void givenDtoWithWrongEmail_whenPreCalculateLoan_thenThrowException() throws Exception {

        dto.setEmail("@yandex.ru");

        mvc.perform(post("/conveyor/offers")
                   .content(mapper.writeValueAsString(dto))
                   .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                   .characterEncoding(StandardCharsets.UTF_8))
           .andExpect(status().is(400));

    }

    @DisplayName("MockMvc test for preCalculateLoan method (negative scenario)")
    @Test
    void givenDtoWithWrongBirthday_whenPreCalculateLoan_thenThrowException() throws Exception {

        dto.setBirthdate(LocalDate.of(2020, 6, 12));

        mvc.perform(post("/conveyor/offers")
                   .content(mapper.writeValueAsString(dto))
                   .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                   .characterEncoding(StandardCharsets.UTF_8))
           .andExpect(status().is(400));

    }

    @DisplayName("MockMvc test for preCalculateLoan method (negative scenario)")
    @Test
    void givenDtoWithWrongPassportNumber_whenPreCalculateLoan_thenThrowException() throws Exception {

        dto.setPassportNumber("1");

        mvc.perform(post("/conveyor/offers")
                   .content(mapper.writeValueAsString(dto))
                   .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                   .characterEncoding(StandardCharsets.UTF_8))
           .andExpect(status().is(400));

    }

    @DisplayName("MockMvc test for preCalculateLoan method (negative scenario)")
    @Test
    void givenDtoWithWrongPassportSeries_whenPreCalculateLoan_thenThrowException() throws Exception {

        dto.setPassportSeries("11111111111");

        mvc.perform(post("/conveyor/offers")
                   .content(mapper.writeValueAsString(dto))
                   .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                   .characterEncoding(StandardCharsets.UTF_8))
           .andExpect(status().is(400));

    }


}