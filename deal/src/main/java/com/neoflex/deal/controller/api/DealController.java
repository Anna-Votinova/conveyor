package com.neoflex.deal.controller.api;

import com.neoflex.deal.dto.LoanOfferDTO;
import com.neoflex.deal.dto.request.FinishRegistrationRequestDTO;
import com.neoflex.deal.dto.LoanApplicationRequestDTO;
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
@Tag(name = "Контроллер Сделка",
     description = "Позволяет сохранить информацию о клиентах, их заявках и одобренных кредитах")
public class DealController {

    private final DealService dealService;

    @Operation(summary = "Расчёт возможных условий кредита",
               description = "Помогает связаться с сервисом по подсчету предварительной стоимости кредита и " +
                       "отдает пользователю четыре кредитных предложения, отсортированных от худшего (с самой большой " +
                       "ставкой) к лучшему (с самой маленькой ставкой). Помогает сохранить данные о клиенте и его " +
                       "заявке в базу")
    @PostMapping("/application")
    public List<LoanOfferDTO> calculateOffers(@Valid @RequestBody LoanApplicationRequestDTO requestDTO) {
        log.info("Got the request for starting registration {}", requestDTO);
        return dealService.startRegistration(requestDTO);
    }

    @Operation(summary = "Выбор одного из предложений",
               description = "Принимает одну из четырех заявок, выбранную пользователем. Помогает сохранить заявку в " +
                       "базу, связаться с сервисом Dossier, чтобы отправить письмо пользователю с предложением " +
                       "завершить регистрацию")
    @PutMapping("/offer")
    public void chooseOffer(@Valid @RequestBody LoanOfferDTO requestDTO) {
        log.info("Got the request to save the chosen offer {}", requestDTO);
        dealService.chooseOffer(requestDTO);
    }

    @Operation(summary = "Завершение регистрации и полный подсчёт кредита",
               description = "Принимает дополнительные сведения о пользователе. Помогает создать полную заявку " +
                       "на кредит и связаться с сервисом по подсчету итоговой стоимости кредита. Вернувшееся " +
                       "предложение сохраняется в базу. Позволяет связаться с сервисом Dossier, чтобы отправить письмо " +
                       "пользователю с предложением создать документы для выдачи кредита")
    @PutMapping("/calculate/{applicationId}")
    public void calculateCredit(@Valid @RequestBody FinishRegistrationRequestDTO requestDTO, @Positive @PathVariable
    @Parameter(description = "Идентификатор заявки", example = "1", required = true) Long applicationId) {
        log.info("Got the request for full registration and calculation loan. Parameters: requestDTO = {}, " +
                "applicationId = {}", requestDTO, applicationId);
        dealService.finishRegistration(requestDTO, applicationId);
    }
}
