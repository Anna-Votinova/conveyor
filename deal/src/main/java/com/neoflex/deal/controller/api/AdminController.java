package com.neoflex.deal.controller.api;

import com.neoflex.deal.entity.enums.ApplicationStatus;
import com.neoflex.deal.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.Positive;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/deal/admin/application")
@Slf4j
@Validated
@Tag(name = "Админский контроллер",
     description = "Позволяет работать с заявками пользователей администратору")
public class AdminController {

    private final AdminService adminService;

    @Operation(summary = "Изменение статуса заявки",
               description = "Помогает изменить статус заявки из других сервисов")
    @PutMapping("/{applicationId}/status")
    public void changeApplicationStatus(
            @Positive @PathVariable @Parameter(description = "Идентификатор заявки", example = "1", required = true)
            Long applicationId,
            @RequestParam @Parameter(description = "Статус заявки", example = "DOCUMENT_CREATED", required = true)
            ApplicationStatus status
    ) {
        log.info("Got the request for changing the application status. Parameters: applicationId = {}", applicationId);
        adminService.changeApplicationStatus(applicationId, status);
    }
}
