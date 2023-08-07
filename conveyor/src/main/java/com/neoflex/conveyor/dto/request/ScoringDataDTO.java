package com.neoflex.conveyor.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.neoflex.conveyor.config.GlobalVariables;
import com.neoflex.conveyor.dto.enums.Gender;
import com.neoflex.conveyor.dto.enums.MaritalStatus;
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
    @Schema(description = "Запрашиваемая сумма займа", example = "100000")
    private BigDecimal amount;

    @Min(6)
    @NotNull
    @Schema(description = "Срок кредита (в месяцах)", example = "18")
    private Integer term;

    @NotBlank
    @Size(min = 2, max = 30, message = "Длина имени не может быть меньше 2 и больше 30 символов")
    @Pattern(regexp = GlobalVariables.LATIN_LANG, message = "Имя должно быть написано латинскими буквами")
    @Schema(description = "Имя", example = "Alexandra")
    private String firstName;

    @NotBlank
    @Size(min = 2, max = 30, message = "Длина фамилии не может быть меньше 2 и больше 30 символов")
    @Pattern(regexp = GlobalVariables.LATIN_LANG, message = "Фамилия должна быть написана латинскими буквами")
    @Schema(description = "Фамилия", example = "Kotova")
    private String lastName;

    @Size(min = 2, max = 30, message = "Длина отчества не может быть меньше 2 и больше 30 символов")
    @Pattern(regexp = GlobalVariables.LATIN_LANG, message = "Отчество должно быть написано латинскими буквами")
    @Schema(description = "Отчество (при наличии)", example = "Igorevna")
    private String middleName;

    @NotNull
    @Schema(description = "Пол заемщика", example = "FEMALE")
    private Gender gender;

    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @DateIsBeforeYears(years = 18, message = "Заемщик должен быть совершеннолетним")
    @Schema(description = "Дата рождения заемщика", example = "1990-01-12")
    private LocalDate birthdate;

    @NotBlank
    @Pattern(regexp = GlobalVariables.PASSPORT_SERIES_FORMAT, message = "Серия паспорта должна содержать 4 цифры")
    @Schema(description = "Серия паспорта", example = "2023")
    private String passportSeries;

    @NotBlank
    @Pattern(regexp = GlobalVariables.PASSPORT_NUMBER_FORMAT, message = "Номер паспорта должен содержать 6 цифр")
    @Schema(description = "Номер паспорта", example = "600974")
    private String passportNumber;

    @NotNull
    @Past
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @Schema(description = "Дата выдачи паспорта", example = "2021-01-12")
    private LocalDate passportIssueDate;

    @NotBlank
    @Schema(description = "Ведомство, выдавшее паспорт", example = "The main Directorate of the MIA of the Voronezh region")
    private String passportIssueBranch;

    @NotNull
    @Schema(description = "Семейное положение", example = "MARRIED")
    private MaritalStatus maritalStatus;

    @Schema(description = "Число иждивенцев", example = "0")
    private Integer dependentAmount;

    @NotNull
    @Schema(description = "Сведения о работе")
    private EmploymentDTO employment;

    @NotBlank
    @Pattern(regexp = "[\\d]{20}", message = "Счет должен содержать 20 цифр")
    @Schema(description = "Номер счета в банке", example = "70846273218496606511")
    private String account;

    @NotNull
    @Schema(description = "Включить страховку в кредит", example = "true")
    private Boolean isInsuranceEnabled;

    @NotNull
    @Schema(description = "Заемщик - зарплатный клиент банка", example = "false")
    private Boolean isSalaryClient;

}
