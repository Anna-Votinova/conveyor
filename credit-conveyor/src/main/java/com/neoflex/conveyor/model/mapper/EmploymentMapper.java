package com.neoflex.conveyor.model.mapper;

import com.neoflex.conveyor.model.Employment;
import com.neoflex.conveyor.model.dto.EmploymentDTO;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

import static org.apache.commons.lang3.math.NumberUtils.INTEGER_ZERO;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class EmploymentMapper {

    public static Employment toModel(EmploymentDTO dto) {
        return Employment.builder()
                .employmentStatus(dto.getEmploymentStatus())
                .employerINN(dto.getEmployerINN())
                .salary(dto.getSalary() != null
                        ? dto.getSalary() : BigDecimal.ZERO)
                .position(dto.getPosition())
                .workExperienceTotal(dto.getWorkExperienceTotal() != null
                        ? dto.getWorkExperienceTotal() : INTEGER_ZERO)
                .workExperienceCurrent(dto.getWorkExperienceCurrent() != null
                        ? dto.getWorkExperienceCurrent() : INTEGER_ZERO)
                .build();
    }
}
