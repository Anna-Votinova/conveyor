package com.neoflex.deal.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.neoflex.deal.entity.enums.Gender;
import com.neoflex.deal.entity.enums.MaritalStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDate;

@Builder
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Дополнительная информация о заемщике")
public class FinishRegistrationRequestDTO {

    @NotNull
    @Schema(description = "Пол заемщика", example = "FEMALE")
    private Gender gender;

    @NotNull
    @Schema(description = "Семейное положение", example = "MARRIED")
    private MaritalStatus maritalStatus;

    @Schema(description = "Число иждивенцев", example = "0")
    @PositiveOrZero
    private Integer dependentAmount;

    @NotNull
    @PastOrPresent
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @Schema(description = "Дата выдачи паспорта", example = "2021-01-12")
    private LocalDate passportIssueDate;

    @NotBlank
    @Schema(description = "Ведомство, выдавшее паспорт",
            example = "The main Directorate of the MIA of the Voronezh region")
    private String passportIssueBranch;

    @NotNull
    @Schema(description = "Сведения о работе")
    @Valid
    private EmploymentDTO employment;

    @NotBlank
    @Pattern(regexp = "[\\d]{20}", message = "Счет должен содержать 20 цифр")
    @Schema(description = "Номер счета в банке", example = "70846273218496606511")
    private String account;
}
