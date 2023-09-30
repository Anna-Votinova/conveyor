package com.neoflex.gateway.dto.response.element;

import com.neoflex.gateway.dto.enums.Gender;
import com.neoflex.gateway.dto.enums.MaritalStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDate;

@Builder
@Schema(description = "Информация о клиенте")
public record ClientInfo (
        @Schema(description = "Идентификатор клиента", example = "1")
        Long id,

        @Schema(description = "Фамилия", example = "Kotova")
        String lastName,

        @Schema(description = "Имя", example = "Alexandra")
        String firstName,

        @Schema(description = "Отчество (при наличии)", example = "Igorevna")
        String middleName,

        @Schema(description = "Дата рождения заемщика", example = "1990-01-12")
        LocalDate birthdate,

        @Schema(description = "Электронный почтовый ящик", example = "any@yandex.ru")
        String email,

        @Schema(description = "Пол заемщика", example = "FEMALE")
        Gender gender,

        @Schema(description = "Семейное положение", example = "MARRIED")
        MaritalStatus maritalStatus,

        @Schema(description = "Число иждивенцев", example = "0")
        Integer dependentAmount,

        PassportInfo passportInfo,

        EmploymentInfo employmentInfo,

        @Schema(description = "Номер счета в банке", example = "70846273218496606511")
        String account
) {}
