package com.neoflex.gateway.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.neoflex.gateway.dto.enums.Gender;
import com.neoflex.gateway.dto.enums.MaritalStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDate;

@Builder
@Schema(description = "Дополнительная информация о заемщике")
public record FinishRegistrationRequestDTO(
        @NotNull
        @Schema(description = "Пол заемщика", example = "FEMALE")
        Gender gender,

        @NotNull
        @Schema(description = "Семейное положение", example = "MARRIED")
        MaritalStatus maritalStatus,

        @Schema(description = "Число иждивенцев", example = "0")
        @PositiveOrZero
        Integer dependentAmount,

        @NotNull
        @PastOrPresent
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        @Schema(description = "Дата выдачи паспорта", example = "2021-01-12")
        LocalDate passportIssueDate,

        @NotBlank
        @Schema(description = "Ведомство, выдавшее паспорт",
                example = "The main Directorate of the MIA of the Voronezh region")
        String passportIssueBranch,

        @Valid
        @NotNull
        @Schema(description = "Сведения о работе")
        EmploymentDTO employment,

        @NotBlank
        @Pattern(regexp = "[\\d]{20}", message = "Счет должен содержать 20 цифр")
        @Schema(description = "Номер счета в банке", example = "70846273218496606511")
        String account
) {}
