package com.neoflex.conveyor.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.neoflex.conveyor.model.dto.enums.Gender;
import com.neoflex.conveyor.model.dto.enums.MaritalStatus;
import com.neoflex.conveyor.controller.advice.DateIsBeforeYears;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;

import static com.neoflex.conveyor.utility.GlobalVariables.ACCOUNT_FORMAT;
import static com.neoflex.conveyor.utility.GlobalVariables.DATE_FORMAT;
import static com.neoflex.conveyor.utility.GlobalVariables.LATIN_LANG;
import static com.neoflex.conveyor.utility.GlobalVariables.PASSPORT_NUMBER_FORMAT;
import static com.neoflex.conveyor.utility.GlobalVariables.PASSPORT_SERIES_FORMAT;

@Builder
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Расширенная анкета пользователя")
public class ScoringDataDTO {

    @Min(10000)
    @NotNull
    @Schema(description = "Запрашиваемая сумма займа")
    private BigDecimal amount;
    @Min(6)
    @NotNull
    @Schema(description = "Срок кредита (в месяцах)")
    private Integer term;
    @NotBlank
    @Size(min = 2, max = 30, message = "Длина имени не может быть меньше 2 и больше 30 символов")
    @Pattern(regexp = LATIN_LANG, message = "Имя должно быть написано латинскими буквами")
    @Schema(description = "Имя", example = "Alexandra")
    private String firstName;
    @NotBlank
    @Size(min = 2, max = 30, message = "Длина фамилии не может быть меньше 2 и больше 30 символов")
    @Pattern(regexp = LATIN_LANG, message = "Фамилия должна быть написана латинскими буквами")
    @Schema(description = "Фамилия", example = "Kotova")
    private String lastName;
    @Size(min = 2, max = 30, message = "Длина отчества не может быть меньше 2 и больше 30 символов")
    @Pattern(regexp = LATIN_LANG, message = "Отчество должно быть написано латинскими буквами")
    @Schema(description = "Отчество (при наличии)", example = "Igorevna")
    private String middleName;
    @NotNull
    @Schema(description = "Пол заемщика")
    private Gender gender;
    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_FORMAT)
    @DateIsBeforeYears(years = 18, message = "Заемщик должен быть совершеннолетним")
    @Schema(description = "Дата рождения заемщика", example = "1990-01-12")
    private LocalDate birthdate;
    @NotBlank
    @Pattern(regexp = PASSPORT_SERIES_FORMAT, message = "Серия паспорта должна содержать 4 цифры")
    @Schema(description = "Серия паспорта")
    private String passportSeries;
    @NotBlank
    @Pattern(regexp = PASSPORT_NUMBER_FORMAT, message = "Номер паспорта должен содержать 6 цифр")
    @Schema(description = "Номер паспорта")
    private String passportNumber;
    @NotNull
    @Past
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_FORMAT)
    @Schema(description = "Дата выдачи паспорта", example = "2021-01-12")
    private LocalDate passportIssueDate;
    @NotBlank
    @Schema(description = "Ведомство, выдавшее паспорт")
    private String passportIssueBranch;
    @NotNull
    @Schema(description = "Семейное положение")
    private MaritalStatus maritalStatus;
    @Schema(description = "Число иждивенцев")
    private Integer dependentAmount;
    @NotNull
    @Schema(description = "Сведения о работе")
    private EmploymentDTO employment;
    @NotBlank
    @Pattern(regexp = ACCOUNT_FORMAT, message = "Счет должен содержать 20 цифр")
    @Schema(description = "Номер счета в банке")
    private String account;
    @NotNull
    @Schema(description = "Включить страховку в кредит (да/нет)")
    private Boolean isInsuranceEnabled;
    @NotNull
    @Schema(description = "Заемщик - зарплатный клиент банка (да/нет)")
    private Boolean isSalaryClient;


}
