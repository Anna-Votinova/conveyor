package com.neoflex.deal.dto.response.element;

import com.neoflex.deal.entity.enums.EmploymentPosition;
import com.neoflex.deal.entity.enums.EmploymentStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "Сведения о работе")
public class EmploymentInfo {

    @Schema(description = "Идентификатор сведений о работе", example = "1")
    private Long id;

    @Schema(description = "Статус", example = "EMPLOYED")
    private EmploymentStatus employmentStatus;

    @Schema(description = "ИНН работодателя", example = "325507450247")
    private String employerINN;

    @Schema(description = "Размер зарплаты", example = "70000")
    private BigDecimal salary;

    @Schema(description = "Должность", example = "WORKER")
    private EmploymentPosition position;

    @Schema(description = "Общий стаж работы (в месяцах)", example = "144")
    private Integer workExperienceTotal;

    @Schema(description = "Общий стаж работы на текущем месте (в месяцах)", example = "110")
    private Integer workExperienceCurrent;
}
