package com.neoflex.gateway.controller.api.deal;

import com.neoflex.gateway.integration.deal.DocumentClient;
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

import javax.validation.constraints.Positive;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/document/{applicationId}")
@Slf4j
@Validated
@Tag(name = "Публичный контроллер Документы",
     description = "Направляет запросы в контроллер Документы сервиса Deal")
public class DocumentController {

    private final DocumentClient documentClient;

    @Operation(summary = "Запрос на отправку документов",
               description = "Принимает от пользователя запрос на отправку документов и направляет его в сервис Deal")
    @PostMapping
    public void sendDocument(@Positive @PathVariable @Parameter(
            description = "Идентификатор заявки", example = "1", required = true) Long applicationId
    ) {
        log.info("Got the request for sending documents. Parameters: applicationId = {}", applicationId);
        documentClient.sendDocument(applicationId);
    }

    @Operation(summary = "Запрос на подписание документов",
               description = "Принимает от пользователя запрос на подписание документов и отправляет его в сервис Deal")
    @PostMapping("/sign")
    public void signDocument(@Positive @PathVariable @Parameter(
            description = "Идентификатор заявки", example = "1", required = true) Long applicationId
    ) {
        log.info("Got the request for signing documents. Parameters: applicationId = {}", applicationId);
        documentClient.signDocument(applicationId);
    }

    @Operation(summary = "Подписание документов",
               description = "Принимает код для подписания документов и передает его в сервис Deal")
    @PostMapping("/sign/code")
    public void issueCredit(@Positive @PathVariable @Parameter(
            description = "Идентификатор заявки", example = "1", required = true) Long applicationId,
                            @RequestBody Integer code
    ) {
        log.info("Got the request for signing documents with code. Parameters: applicationId = {},  sesCode = {}",
                applicationId, code);
        documentClient.issueCredit(applicationId, code);
    }
}
