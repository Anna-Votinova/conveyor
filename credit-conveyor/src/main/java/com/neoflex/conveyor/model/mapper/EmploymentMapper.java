package com.neoflex.conveyor.model.mapper;

import com.neoflex.conveyor.model.Employment;
import com.neoflex.conveyor.model.dto.EmploymentDTO;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class EmploymentMapper {

    public static Employment fromDto(EmploymentDTO dto) {
        return Employment.builder()
                .employmentStatus(dto.getEmploymentStatus())
                .employerINN(dto.getEmployerINN())
                .salary(dto.getSalary())
                .position(dto.getPosition())
                .workExperienceTotal(dto.getWorkExperienceTotal())
                .workExperienceCurrent(dto.getWorkExperienceCurrent())
                .build();
    }
}
