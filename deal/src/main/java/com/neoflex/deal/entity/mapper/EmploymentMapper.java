package com.neoflex.deal.entity.mapper;

import com.neoflex.deal.entity.dto.request.EmploymentDTO;
import com.neoflex.deal.entity.Employment;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class EmploymentMapper {

    public Employment toEmployment(EmploymentDTO employmentDTO) {
         Employment employment =  new Employment();

         employment.setStatus(employmentDTO.getEmploymentStatus());
         employment.setEmployerINN(employmentDTO.getEmployerINN());
         employment.setSalary(employmentDTO.getSalary()
                           != null ? employmentDTO.getSalary() : BigDecimal.ZERO);
         employment.setPosition(employmentDTO.getPosition());
         employment.setWorkExperienceTotal(employmentDTO.getWorkExperienceTotal()
                                           != null ? employmentDTO.getWorkExperienceTotal() : NumberUtils.INTEGER_ZERO);
         employment.setWorkExperienceCurrent(employmentDTO.getWorkExperienceCurrent()
                                           != null ? employmentDTO.getWorkExperienceCurrent() : NumberUtils.INTEGER_ZERO);

         return employment;

    }

}

