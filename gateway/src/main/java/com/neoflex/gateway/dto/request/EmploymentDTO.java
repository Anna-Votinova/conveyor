package com.neoflex.gateway.dto.request;

import com.neoflex.gateway.dto.enums.EmploymentPosition;
import com.neoflex.gateway.dto.enums.EmploymentStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;

@Builder
@Schema(description = "Сведения о работе")
public record EmploymentDTO (
        @NotNull(message = "Необходимо указать рабочий статус")
        @Schema(description = "Статус", example = "EMPLOYED")
        EmploymentStatus employmentStatus,

        @Schema(description = "ИНН работодателя", example = "325507450247")
        String employerINN,

        @NotNull(message = "Необходимо заполнить поле запрлаты")
        @PositiveOrZero(message = "Зарплата не может быть отрицательным числом")
        @Schema(description = "Размер зарплаты", example = "70000")
        BigDecimal salary,

        @Schema(description = "Должность", example = "WORKER")
        EmploymentPosition position,

        @PositiveOrZero(message = "Общий стаж не может быть отрицательным числом")
        @Schema(description = "Общий стаж работы (в месяцах)", example = "144")
        Integer workExperienceTotal,

        @PositiveOrZero(message = "Текущий стаж не может быть отрицательным числом")
        @Schema(description = "Общий стаж работы на текущем месте (в месяцах)", example = "110")
        Integer workExperienceCurrent
) {}
