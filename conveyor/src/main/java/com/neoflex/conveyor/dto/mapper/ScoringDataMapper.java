package com.neoflex.conveyor.dto.mapper;

import com.neoflex.conveyor.dto.request.ScoringDataServiceDTO;
import com.neoflex.conveyor.dto.request.ScoringDataDTO;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ScoringDataMapper {

    private final EmploymentMapper employmentMapper;

    public ScoringDataServiceDTO toData(ScoringDataDTO dto) {
        return ScoringDataServiceDTO.builder()
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
                                    .dependentAmount(dto.getDependentAmount() != null
                                            ? dto.getDependentAmount()
                                            : NumberUtils.INTEGER_ZERO)
                                    .employmentServiceDTO(employmentMapper.toModel(dto.getEmployment()))
                                    .account(dto.getAccount())
                                    .isInsuranceEnabled(dto.getIsInsuranceEnabled())
                                    .isSalaryClient(dto.getIsSalaryClient())
                                    .build();
    }
}
