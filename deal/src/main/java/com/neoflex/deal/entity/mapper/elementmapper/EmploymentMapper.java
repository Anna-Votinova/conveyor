package com.neoflex.deal.entity.mapper.elementmapper;

import com.neoflex.deal.dto.EmploymentDTO;
import com.neoflex.deal.dto.response.element.EmploymentInfo;
import com.neoflex.deal.entity.Client;
import com.neoflex.deal.entity.Employment;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Objects;

@Service
public class EmploymentMapper {

    public Employment toEmployment(EmploymentDTO employmentDTO) {

         Employment employment =  new Employment();
         employment.setStatus(employmentDTO.getEmploymentStatus());
         employment.setEmployerINN(employmentDTO.getEmployerINN());
         employment.setSalary(employmentDTO.getSalary() != null
                 ? employmentDTO.getSalary()
                 : BigDecimal.ZERO);
         employment.setPosition(employmentDTO.getPosition());
         employment.setWorkExperienceTotal(employmentDTO.getWorkExperienceTotal() != null
                 ? employmentDTO.getWorkExperienceTotal()
                 : NumberUtils.INTEGER_ZERO);
         employment.setWorkExperienceCurrent(employmentDTO.getWorkExperienceCurrent() != null
                 ? employmentDTO.getWorkExperienceCurrent()
                 : NumberUtils.INTEGER_ZERO);
         return employment;
    }

    public EmploymentInfo toEmploymentInfo(Client client) {

        EmploymentInfo employmentInfo = new EmploymentInfo();

        if (Objects.nonNull(client) && Objects.nonNull(client.getEmployment())) {
            employmentInfo.setId(client.getEmployment().getId());
            employmentInfo.setEmploymentStatus(client.getEmployment().getStatus());
            employmentInfo.setEmployerINN(client.getEmployment().getEmployerINN());
            employmentInfo.setSalary(client.getEmployment().getSalary());
            employmentInfo.setPosition(client.getEmployment().getPosition());
            employmentInfo.setWorkExperienceTotal(client.getEmployment().getWorkExperienceTotal());
            employmentInfo.setWorkExperienceCurrent(client.getEmployment().getWorkExperienceCurrent());
        }
        return employmentInfo;
    }
}

