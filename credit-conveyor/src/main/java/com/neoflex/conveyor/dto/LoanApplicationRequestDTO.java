package com.neoflex.conveyor.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.neoflex.conveyor.util.ValidateDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;

import static com.neoflex.conveyor.util.GlobalVariables.DATE_FORMAT;
import static com.neoflex.conveyor.util.GlobalVariables.EMAIL_FORMAT;
import static com.neoflex.conveyor.util.GlobalVariables.LATIN_LANG;
import static com.neoflex.conveyor.util.GlobalVariables.PASSPORT_NUMBER_FORMAT;
import static com.neoflex.conveyor.util.GlobalVariables.PASSPORT_SERIES_FORMAT;

@Builder
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class LoanApplicationRequestDTO {

    @NotNull
    @Min(10000)
    private BigDecimal amount;
    @Min(6)
    @NotNull
    private Integer term;
    @NotBlank
    @Size(min = 2, max = 30, message = "Длина имени не может быть меньше 2 и больше 30 символов")
    @Pattern(regexp = LATIN_LANG, message = "Имя должно быть написано латинскими буквами")
    private String firstName;
    @NotBlank
    @Size(min = 2, max = 30, message = "Длина фамилии не может быть меньше 2 и больше 30 символов")
    @Pattern(regexp = LATIN_LANG, message = "Фамилия должна быть написана латинскими буквами")
    private String lastName;
    @Size(min = 2, max = 30, message = "Длина отчества не может быть меньше 2 и больше 30 символов")
    @Pattern(regexp = LATIN_LANG, message = "Отчество должно быть написано латинскими буквами")
    private String middleName;
    @NotBlank
    @Pattern(regexp = EMAIL_FORMAT, message = "Емаил должен содержать до знака @ от 2 до 50 букв, цифр, " +
            "знаков подчеркивания или символов и после от 2 до 20 букв, цифр, знаков подчеркивания или символов")
    private String email;
    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_FORMAT)
    @ValidateDate
    private LocalDate birthdate;
    @NotBlank
    @Pattern(regexp = PASSPORT_SERIES_FORMAT, message = "Серия паспорта должна содержать 4 цифры")
    private String passportSeries;
    @NotBlank
    @Pattern(regexp = PASSPORT_NUMBER_FORMAT, message = "Номер паспорта должен содержать 6 цифр")
    private String passportNumber;

}
