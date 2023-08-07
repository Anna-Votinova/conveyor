package com.neoflex.conveyor.dto.mapper;

import com.neoflex.conveyor.dto.request.EmploymentServiceDTO;
import com.neoflex.conveyor.dto.request.EmploymentDTO;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class EmploymentMapper {

    public EmploymentServiceDTO toModel(EmploymentDTO dto) {
        return EmploymentServiceDTO.builder()
                                   .employmentStatus(dto.getEmploymentStatus())
                                   .employerINN(dto.getEmployerINN())
                                   .salary(dto.getSalary() != null
                        ? dto.getSalary() : BigDecimal.ZERO)
                                   .position(dto.getPosition())
                                   .workExperienceTotal(dto.getWorkExperienceTotal() != null
                        ? dto.getWorkExperienceTotal() : NumberUtils.INTEGER_ZERO)
                                   .workExperienceCurrent(dto.getWorkExperienceCurrent() != null
                        ? dto.getWorkExperienceCurrent() : NumberUtils.INTEGER_ZERO)
                                   .build();
    }

}
