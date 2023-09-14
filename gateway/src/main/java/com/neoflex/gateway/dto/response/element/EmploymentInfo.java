package com.neoflex.gateway.dto.response.element;

import com.neoflex.gateway.dto.enums.EmploymentPosition;
import com.neoflex.gateway.dto.enums.EmploymentStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
@Schema(description = "Сведения о работе")
public record EmploymentInfo (
        @Schema(description = "Идентификатор сведений о работе", example = "1")
        Long id,

        @Schema(description = "Статус", example = "EMPLOYED")
        EmploymentStatus employmentStatus,

        @Schema(description = "ИНН работодателя", example = "325507450247")
        String employerINN,

        @Schema(description = "Размер зарплаты", example = "70000")
        BigDecimal salary,

        @Schema(description = "Должность", example = "WORKER")
        EmploymentPosition position,

        @Schema(description = "Общий стаж работы (в месяцах)", example = "144")
        Integer workExperienceTotal,

        @Schema(description = "Общий стаж работы на текущем месте (в месяцах)", example = "110")
        Integer workExperienceCurrent
) {}
