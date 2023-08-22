package com.neoflex.deal.dto.response;

import com.neoflex.deal.dto.request.EmploymentDTO;
import com.neoflex.deal.entity.enums.Gender;
import com.neoflex.deal.entity.enums.MaritalStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Полная заявка на получение кредита")
public class ScoringDataDTO {

    @Schema(description = "Запрашиваемая сумма займа", example = "100000")
    private BigDecimal amount;

    @Schema(description = "Срок кредита (в месяцах)", example = "18")
    private Integer term;

    @Schema(description = "Имя", example = "Alexandra")
    private String firstName;

    @Schema(description = "Фамилия", example = "Kotova")
    private String lastName;

    @Schema(description = "Отчество (при наличии)", example = "Igorevna")
    private String middleName;

    @Schema(description = "Пол заемщика", example = "FEMALE")
    private Gender gender;

    @Schema(description = "Дата рождения заемщика", example = "1990-01-12")
    private LocalDate birthdate;

    @Schema(description = "Серия паспорта", example = "2023")
    private String passportSeries;

    @Schema(description = "Номер паспорта", example = "600974")
    private String passportNumber;

    @Schema(description = "Дата выдачи паспорта", example = "2021-01-12")
    private LocalDate passportIssueDate;

    @Schema(description = "Ведомство, выдавшее паспорт",
            example = "The main Directorate of the MIA of the Voronezh region")
    private String passportIssueBranch;

    @Schema(description = "Семейное положение", example = "MARRIED")
    private MaritalStatus maritalStatus;

    @Schema(description = "Число иждивенцев", example = "0")
    private Integer dependentAmount;

    @Schema(description = "Сведения о работе")
    private EmploymentDTO employment;

    @Schema(description = "Номер счета в банке", example = "70846273218496606511")
    private String account;

    @Schema(description = "Включить страховку в кредит", example = "true")
    private Boolean isInsuranceEnabled;

    @Schema(description = "Заемщик - зарплатный клиент банка", example = "false")
    private Boolean isSalaryClient;
}
