package com.neoflex.deal.entity.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.neoflex.deal.config.GlobalVariables;
import com.neoflex.deal.entity.dto.validation.DateIsBeforeYears;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.Email;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
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
@Schema(description = "Анкета пользователя")
public class LoanApplicationRequestDTO {

    @Schema(description = "Идентификатор анкеты")
    private Long id;

    @NotNull
    @Min(10000)
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

    @NotBlank
    @Email(message = "Название электронной почты должно соответветствовать общепринятым стандартам")
    @Schema(description = "Электронный почтовый ящик", example = "kotova@yandex.ru")
    private String email;

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

}
