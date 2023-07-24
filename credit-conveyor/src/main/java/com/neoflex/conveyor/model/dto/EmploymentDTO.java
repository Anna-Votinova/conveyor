package com.neoflex.conveyor.model.dto;

import com.neoflex.conveyor.model.util.EmploymentStatus;
import com.neoflex.conveyor.model.util.Position;
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
public class EmploymentDTO {

    @NotNull
    private EmploymentStatus employmentStatus;
    private String employerINN;
    @PositiveOrZero
    @Builder.Default
    private BigDecimal salary = new BigDecimal("00.00");
    private Position position;
    @PositiveOrZero
    @Builder.Default
    private Integer workExperienceTotal = 0;
    @PositiveOrZero
    @Builder.Default
    private Integer workExperienceCurrent = 0;

}
