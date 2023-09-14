package com.neoflex.gateway.controller.api.application;

import com.neoflex.gateway.dto.LoanOfferDTO;
import com.neoflex.gateway.dto.request.LoanApplicationRequestDTO;
import com.neoflex.gateway.integration.application.ApplicationClient;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/application")
@Slf4j
@Validated
@Tag(name = "Публичный контроллер Документы",
     description = "Направляет запросы в сервис Application")
public class ApplicationController {

    private final ApplicationClient applicationClient;

    @Operation(summary = "Расчёт возможных условий кредита",
               description = "Помогает связаться с сервисом Application и возвращает пользователю четыре кредитных " +
                       "предложения")
    @PostMapping
    public List<LoanOfferDTO> calculateOffers(@Valid @RequestBody LoanApplicationRequestDTO requestDTO) {
        log.info("Got the request for starting registration {}", requestDTO);
        return null;
    }

    @Operation(summary = "Выбор одного из предложений",
               description = "Принимает одну из четырех заявок, выбранную пользователем, и направляет ее в сервис " +
                       "Application")
    @PostMapping("/apply")
    public void chooseOffer(@Valid @RequestBody LoanOfferDTO requestDTO) {
        log.info("Got the request to save the chosen offer {}", requestDTO);
        //вызов клиента
    }
}
