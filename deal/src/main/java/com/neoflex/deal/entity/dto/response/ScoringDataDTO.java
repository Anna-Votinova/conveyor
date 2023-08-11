package com.neoflex.deal.entity.dto.response;

import com.neoflex.deal.entity.dto.request.EmploymentDTO;
import com.neoflex.deal.entity.enums.Gender;
import com.neoflex.deal.entity.enums.MaritalStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ScoringDataDTO {

    private BigDecimal amount;

    private Integer term;

    private String firstName;

    private String lastName;

    private String middleName;

    private Gender gender;

    private LocalDate birthdate;

    private String passportSeries;

    private String passportNumber;

    private LocalDate passportIssueDate;

    private String passportIssueBranch;

    private MaritalStatus maritalStatus;

    private Integer dependentAmount;

    private EmploymentDTO employmentServiceDTO;

    private String account;

    private Boolean isInsuranceEnabled;

    private Boolean isSalaryClient;

}
