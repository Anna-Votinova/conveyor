package com.neoflex.application.controller.api;

import com.neoflex.application.dto.LoanApplicationRequestDTO;
import com.neoflex.application.dto.LoanOfferDTO;
import com.neoflex.application.service.ApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/application")
@Slf4j
@Tag(name = "Контроллер Заявка",
     description = "Позволяет предварительно проверить заявку пользователя на валидность и перенаправляет " +
             "ее в сервис Сделка, помогает сохранить выбранную заявку в базу данных")
public class ApplicationController {

    private final ApplicationService applicationService;

    @Operation(summary = "Прием предварительных заявок от пользователей",
               description = "Помогает проверить заявку на валидность и связаться с сервисом Сделка. Принимает " +
                       "от него четыре кредитных предложения, отсортированных от худшего (с самой большой ставкой) " +
                       "к лучшему (с самой маленькой ставкой), и отправляет пользователю.")
    @PostMapping()
    public List<LoanOfferDTO> prepareOffers(@Valid @RequestBody LoanApplicationRequestDTO requestDTO) {
        log.info("Got the request for preparing offers {}", requestDTO);
        return applicationService.prepareOffers(requestDTO);
    }

    @Operation(summary = "Выбор одного из четырех кредитных предложений",
               description = "Принимает одну из четырех заявок, выбранную пользователем, помогает направить ее " +
                       "в сервис Сделка для сохранения в базу данных")

    @PutMapping("/offer")
    public void chooseOffer(@Valid @RequestBody LoanOfferDTO requestDTO) {
        log.info("Got the request to save the chosen offer {}", requestDTO);
        applicationService.chooseOffer(requestDTO);
    }
}
