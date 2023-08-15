package com.neoflex.deal.controller.api;

import com.neoflex.deal.integration.conveyor.ConveyorClient;
import com.neoflex.deal.entity.dto.request_responce.LoanOfferDTO;
import com.neoflex.deal.entity.dto.request.FinishRegistrationRequestDTO;
import com.neoflex.deal.entity.dto.request_responce.LoanApplicationRequestDTO;
import com.neoflex.deal.entity.dto.request.CreditDTO;
import com.neoflex.deal.entity.dto.response.ScoringDataDTO;
import com.neoflex.deal.service.DealService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/deal")
@Slf4j
@Validated
@Tag(name="Сервис Сделка",
     description="Хранит информацию о клиентах, их заявках и одобренных кредитах")
public class DealController {

    private final ConveyorClient conveyorClient;

    private final DealService dealService;

    @Operation(
            summary = "Расчёт возможных условий кредита",
            description = "Связывается с сервисом по подсчету предварительной стоимости кредита и отдает пользователю " +
                    "четыре кредитных предложения на основе введенных данных, сортируя от худшего (с самой большой " +
                    "ставкой) к лучшему (с самой маленькой ставкой). Сохраняет данные о клиенте и его заявке в базу"
    )
    @PostMapping("/application")
    public List<LoanOfferDTO> calculateOffers(@Valid @RequestBody LoanApplicationRequestDTO requestDTO) {
        log.info("Got the request for starting registration {}", requestDTO);

        LoanApplicationRequestDTO fullLoanApplicationRequestDTO = dealService.startRegistration(requestDTO);
        log.info("Got full request dto to prepare loan offers {}", fullLoanApplicationRequestDTO);

        return conveyorClient.preCalculateLoan(fullLoanApplicationRequestDTO);
    }

    @Operation(
            summary = "Выбор одного из предложений",
            description = "Принимает одну из четырех заявок, выбранную пользователем. Начинает отлеживать историю " +
                    "статусов заявки. Сохраняет заявку в базу"
    )
    @PutMapping("/offer")
    public void chooseOffer(@Valid @RequestBody LoanOfferDTO requestDTO) {
        log.info("Got the request to save the chosen offer {}", requestDTO);
        dealService.chooseOffer(requestDTO);
    }

    @Operation(
            summary = "Завершение регистрации и полный подсчёт кредита",
            description = "Принимает дополнительные сведения о пользователе. Создает полную заявку на кредит " +
                    "и связывается с сервисом по подсчету итоговой стоимости кредита. Сохраняет вернувшееся предложение " +
                    "в базу"
    )
    @PutMapping("calculate/{applicationId}")
    public void calculateCredit(@Valid @RequestBody FinishRegistrationRequestDTO requestDTO,
                                @Positive @PathVariable @Parameter(
                                        description = "Идентификатор заявки", example = "1") Long applicationId
    ) {
        log.info("Got the request for full registration and calculation loan. Parameters: requestDTO = {}, " +
                        "applicationId = {}", requestDTO, applicationId);

        ScoringDataDTO scoringDataDTO = dealService.finishRegistration(requestDTO, applicationId);

        CreditDTO creditDTO = conveyorClient.calculateLoan(scoringDataDTO);
        log.debug("Received calculated credit: {}", creditDTO);

        dealService.saveCredit(creditDTO, applicationId);

    }

}
