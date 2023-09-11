package com.neoflex.deal.controller.api;

import com.neoflex.deal.service.DocumentService;
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
@RequestMapping(path = "/deal/document/{applicationId}")
@Slf4j
@Validated
@Tag(name = "Контроллер Документы",
     description = "Позволяет работать с документами пользователей для выдачи кредита")
public class DocumentController {

    private final DocumentService documentService;

    @Operation(summary = "Запрос на отправку документов",
               description = "Принимает от пользователя запрос на отправку документов, которые нужно подписать, " +
                       "чтобы получить кредит, помогает связаться с сервисом Dossier, где будут подготовлены документы " +
                       "для отправки на почту пользователю")
    @PostMapping("/send")
    public void sendDocument(@Positive @PathVariable @Parameter(
            description = "Идентификатор заявки", example = "1", required = true) Long applicationId
    ) {
        log.info("Got the request for sending documents. Parameters: applicationId = {}", applicationId);
        documentService.sendDocument(applicationId);

    }

    @Operation(summary = "Запрос на подписание документов",
               description = "Принимает от пользователя запрос на подписание документов, помогает подготовить " +
                       "специальный код для подписи документов и связаться с сервисом Dossier, где произойдет передача " +
                       "кода на почту клиента для дальнейшего подписания документов")
    @PostMapping("/sign")
    public void signDocument(@Positive @PathVariable @Parameter(
            description = "Идентификатор заявки", example = "1", required = true) Long applicationId
    ) {
        log.info("Got the request for signing documents. Parameters: applicationId = {}", applicationId);
        documentService.signDocument(applicationId);

    }

    @Operation(summary = "Подписание документов",
               description = "Принимает код для подписания документов, помогает провалидировать его и, если он верный," +
                       "завершить выдачу кредита. Позволяет связаться с сервисом Dossier для отправки финального " +
                       "письма пользователю с подтверждением выдачи кредита")
    @PostMapping("/code")
    public void issueCredit(@Positive @PathVariable @Parameter(
            description = "Идентификатор заявки", example = "1", required = true) Long applicationId,
                            @RequestBody Integer code
    ) {
        log.info("Got the request for signing documents with code. Parameters: applicationId = {},  sesCode = {}",
                applicationId, code);
        documentService.issueCredit(applicationId, code);
    }
}
