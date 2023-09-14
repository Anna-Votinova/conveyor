package com.neoflex.gateway.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.neoflex.gateway.config.GlobalVariables;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(description = "Анкета пользователя")
@Builder
public record LoanApplicationRequestDTO(
        @Schema(description = "Идентификатор анкеты")
        Long id,

        @NotNull
        @Min(10000)
        @Schema(description = "Запрашиваемая сумма займа", example = "100000")
        BigDecimal amount,

        @Min(6)
        @NotNull
        @Schema(description = "Срок кредита (в месяцах)", example = "18")
        Integer term,

        @NotBlank
        @Size(min = 2, max = 30, message = "Длина имени не может быть меньше 2 и больше 30 символов")
        @Pattern(regexp = GlobalVariables.LATIN_LANG, message = "Имя должно быть написано латинскими буквами")
        @Schema(description = "Имя", example = "Alexandra")
        String firstName,

        @NotBlank
        @Size(min = 2, max = 30, message = "Длина фамилии не может быть меньше 2 и больше 30 символов")
        @Pattern(regexp = GlobalVariables.LATIN_LANG, message = "Фамилия должна быть написана латинскими буквами")
        @Schema(description = "Фамилия", example = "Kotova")
        String lastName,

        @Size(min = 2, max = 30, message = "Длина отчества не может быть меньше 2 и больше 30 символов")
        @Pattern(regexp = GlobalVariables.LATIN_LANG, message = "Отчество должно быть написано латинскими буквами")
        @Schema(description = "Отчество (при наличии)", example = "Igorevna")
        String middleName,

        @NotBlank
        @Pattern(regexp = "[\\w\\.]{2,50}@[\\w\\.]{2,20}",
                 message = "Название электронной почты должно соответветствовать общепринятым стандартам")
        @Schema(description = "Электронный почтовый ящик", example = "anyvotinova@yandex.ru")
        String email,

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        @Schema(description = "Дата рождения заемщика", example = "1990-01-12")
        LocalDate birthdate,

        @NotBlank
        @Pattern(regexp = GlobalVariables.PASSPORT_SERIES_FORMAT, message = "Серия паспорта должна содержать 4 цифры")
        @Schema(description = "Серия паспорта", example = "2023")
        String passportSeries,

        @NotBlank
        @Pattern(regexp = GlobalVariables.PASSPORT_NUMBER_FORMAT, message = "Номер паспорта должен содержать 6 цифр")
        @Schema(description = "Номер паспорта", example = "600974")
        String passportNumber
) {}
