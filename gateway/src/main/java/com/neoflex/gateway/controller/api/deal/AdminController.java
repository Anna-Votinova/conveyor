package com.neoflex.gateway.controller.api.deal;

import com.neoflex.gateway.dto.enums.ApplicationStatus;
import com.neoflex.gateway.dto.response.ApplicationDTO;
import com.neoflex.gateway.integration.deal.AdminClient;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.Positive;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin/application")
@Slf4j
@Validated
@Tag(name = "Приватный контроллер Админ",
     description = "Направляет запросы в контроллер Админ сервиса Deal")
public class AdminController {

    private final AdminClient adminClient;

    @Operation(summary = "Изменение статуса заявки",
               description = "Направляет админский запрос об изменении статуса заявки в Deal")
    @PostMapping("/{applicationId}/status")
    public void changeApplicationStatus(
            @Positive @PathVariable @Parameter(description = "Идентификатор заявки", example = "1", required = true)
            Long applicationId,
            @RequestParam @Parameter(description = "Статус заявки", example = "DOCUMENT_CREATED", required = true)
            ApplicationStatus status
    ) {
        log.info("Got the request for changing the application status. Parameters: applicationId = {}, status = {}",
                applicationId, status);
        adminClient.changeApplicationStatus(applicationId, status);
    }

    @Operation(summary = "Получение заявки",
               description = "Направляет админский запрос о получении заявки в Deal")
    @GetMapping("/{applicationId}")
    public ApplicationDTO findApplicationById(
            @Positive @PathVariable @Parameter(description = "Идентификатор заявки", example = "1", required = true)
            Long applicationId) {
        log.info("Got the request for finding the application by id {}", applicationId);
        return adminClient.findApplicationById(applicationId);
    }

    @Operation(summary = "Получение всех заявок",
               description = "Направляет админский запрос о получении всех заявок в Deal. Если нет ни одной заявки, " +
                       "возвращается пустой список")
    @GetMapping
    public List<ApplicationDTO> findAllApplications() {
        log.info("Got the request for finding all applications");
        return adminClient.findAllApplications();
    }
}
