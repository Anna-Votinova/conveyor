package com.neoflex.deal.dto.response.element;

import com.neoflex.deal.entity.enums.Gender;
import com.neoflex.deal.entity.enums.MaritalStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;

@Data
@Schema(description = "Информация о клиенте")
public class ClientInfo {

    @Schema(description = "Идентификатор клиента", example = "1")
    private Long id;

    @Schema(description = "Фамилия", example = "Kotova")
    private String lastName;

    @Schema(description = "Имя", example = "Alexandra")
    private String firstName;

    @Schema(description = "Отчество (при наличии)", example = "Igorevna")
    private String middleName;

    @Schema(description = "Дата рождения заемщика", example = "1990-01-12")
    private LocalDate birthdate;

    @Schema(description = "Электронный почтовый ящик", example = "any@yandex.ru")
    private String email;

    @Schema(description = "Пол заемщика", example = "FEMALE")
    private Gender gender;

    @Schema(description = "Семейное положение", example = "MARRIED")
    private MaritalStatus maritalStatus;

    @Schema(description = "Число иждивенцев", example = "0")
    private Integer dependentAmount;

    private PassportInfo passportInfo;

    private EmploymentInfo employmentInfo;

    @Schema(description = "Номер счета в банке", example = "70846273218496606511")
    private String account;
}
