package com.neoflex.conveyor.dto.mapper;

import com.neoflex.conveyor.dto.request.LoanApplicationServiceDTO;
import com.neoflex.conveyor.dto.request.LoanApplicationRequestDTO;
import org.springframework.stereotype.Service;

@Service
public class LoanApplicationMapper {

    public LoanApplicationServiceDTO toApplication(LoanApplicationRequestDTO dto) {
        return LoanApplicationServiceDTO.builder()
                                        .id(dto.getId())
                                        .amount(dto.getAmount())
                                        .term(dto.getTerm())
                                        .firstName(dto.getFirstName())
                                        .lastName(dto.getLastName())
                                        .middleName(dto.getMiddleName())
                                        .email(dto.getEmail())
                                        .birthdate(dto.getBirthdate())
                                        .passportSeries(dto.getPassportSeries())
                                        .passportNumber(dto.getPassportNumber())
                                        .build();
    }

}
