package com.neoflex.conveyor.model.mapper;

import com.neoflex.conveyor.model.LoanApplication;
import com.neoflex.conveyor.model.dto.LoanApplicationRequestDTO;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class LoanApplicationMapper {

    public static LoanApplication fromDto(LoanApplicationRequestDTO dto) {
        return LoanApplication.builder()
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
