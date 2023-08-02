package com.neoflex.conveyor.model.dto;

import com.neoflex.conveyor.model.dto.enums.EmploymentStatus;
import com.neoflex.conveyor.model.dto.enums.Position;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;

@Builder
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Сведения о работе")
public class EmploymentDTO {

    @NotNull
    @Schema(description = "Статус")
    private EmploymentStatus employmentStatus;
    @Schema(description = "ИНН работодателя")
    private String employerINN;
    @PositiveOrZero
    @Schema(description = "Размер зарплаты", example = "70000")
    private BigDecimal salary;
    @Schema(description = "Должность")
    private Position position;
    @PositiveOrZero
    @Schema(description = "Общий стаж работы (в месяцах)", example = "144")
    private Integer workExperienceTotal;
    @PositiveOrZero
    @Schema(description = "Общий стаж работы на текущем месте (в месяцах)", example = "110")
    private Integer workExperienceCurrent;

}
