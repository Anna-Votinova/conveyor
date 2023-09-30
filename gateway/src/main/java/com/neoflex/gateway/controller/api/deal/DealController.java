package com.neoflex.gateway.controller.api.deal;

import com.neoflex.gateway.dto.request.FinishRegistrationRequestDTO;
import com.neoflex.gateway.integration.deal.DealClient;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/deal")
@Slf4j
@Validated
@Tag(name = "Публичный контроллер Сделка",
     description = "Направляет запросы в контроллер Сделка сервиса Deal")
public class DealController {

    private final DealClient dealClient;

    @Operation(summary = "Завершение регистрации и полный подсчёт кредита",
               description = "Принимает дополнительные сведения о пользователе и направляет их в сервис Deal")
    @PostMapping("registration/{applicationId}")
    public void calculateCredit(@Valid @RequestBody FinishRegistrationRequestDTO requestDTO, @Positive @PathVariable
    @Parameter(description = "Идентификатор заявки", example = "1", required = true) Long applicationId) {
        log.info("Got the request for full registration and calculation loan. Parameters: requestDTO = {}, " +
                "applicationId = {}", requestDTO, applicationId);
        dealClient.calculateCredit(requestDTO, applicationId);
    }
}
