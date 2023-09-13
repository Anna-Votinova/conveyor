package com.neoflex.deal.controller.api;

import com.neoflex.deal.dto.response.ApplicationDTO;
import com.neoflex.deal.entity.enums.ApplicationStatus;
import com.neoflex.deal.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.Positive;
import java.util.List;

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
        log.info("Got the request for changing the application status. Parameters: applicationId = {}, status = {}",
                applicationId, status);
        adminService.changeApplicationStatus(applicationId, status);
    }

    @Operation(summary = "Получение заявки",
               description = "Помогает получить заявку по ее идентификатору")
    @GetMapping("/{applicationId}")
    public ApplicationDTO findApplicationById(
            @Positive @PathVariable @Parameter(description = "Идентификатор заявки", example = "1", required = true)
            Long applicationId) {
        log.info("Got the request for finding the application by id {}", applicationId);
        return adminService.getApplicationById(applicationId);
    }

    @Operation(summary = "Получение всех заявок",
               description = "Помогает получить все заявки. Если нет ни одной, возвращается пустой список")
    @GetMapping
    public List<ApplicationDTO> findAllApplications() {
        log.info("Got the request for finding all applications");
        return adminService.getAllApplications();
    }
}
