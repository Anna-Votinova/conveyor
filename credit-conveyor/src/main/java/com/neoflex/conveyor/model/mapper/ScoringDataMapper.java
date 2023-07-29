package com.neoflex.conveyor.model.mapper;

import com.neoflex.conveyor.model.ScoringData;
import com.neoflex.conveyor.model.dto.ScoringDataDTO;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import static org.apache.commons.lang3.math.NumberUtils.INTEGER_ZERO;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ScoringDataMapper {

    public static ScoringData toData(ScoringDataDTO dto) {
        return ScoringData.builder()
                .amount(dto.getAmount())
                .term(dto.getTerm())
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .middleName(dto.getMiddleName())
                .gender(dto.getGender())
                .birthdate(dto.getBirthdate())
                .passportSeries(dto.getPassportSeries())
                .passportNumber(dto.getPassportNumber())
                .passportIssueDate(dto.getPassportIssueDate())
                .passportIssueBranch(dto.getPassportIssueBranch())
                .maritalStatus(dto.getMaritalStatus())
                .dependentAmount(dto.getDependentAmount() != null ?
                                dto.getDependentAmount() : INTEGER_ZERO)
                .employment(EmploymentMapper.toModel(dto.getEmployment()))
                .account(dto.getAccount())
                .isInsuranceEnabled(dto.getIsInsuranceEnabled())
                .isSalaryClient(dto.getIsSalaryClient())
                .build();
    }
}
