package com.neoflex.application.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
@Schema(description = "Анкета пользователя")
public record LoanApplicationRequestDTO(
        @Schema(description = "Идентификатор анкеты")
        Long id,

        @NotNull
        @Schema(description = "Запрашиваемая сумма займа", example = "100000")
        BigDecimal amount,

        @NotNull
        @Schema(description = "Срок кредита (в месяцах)", example = "18")
        Integer term,

        @NotBlank
        @Schema(description = "Имя", example = "Alexandra")
        String firstName,

        @NotBlank
        @Schema(description = "Фамилия", example = "Kotova")
        String lastName,

        @Schema(description = "Отчество (при наличии)", example = "Igorevna")
        String middleName,

        @NotBlank
        @Schema(description = "Электронный почтовый ящик", example = "anyvotinova@yandex.ru")
        String email,

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        @Schema(description = "Дата рождения заемщика", example = "1990-01-12")
        LocalDate birthdate,

        @NotBlank
        @Schema(description = "Серия паспорта", example = "2023")
        String passportSeries,

        @NotBlank
        @Schema(description = "Номер паспорта", example = "600974")
        String passportNumber
) {}