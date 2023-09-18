package com.neoflex.deal.dto.response.element;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;

@Data
@Schema(description = "Паспорт клиента")
public class PassportInfo {

    @Schema(description = "Идентификатор паспорта", example = "1")
    private Long id;

    @Schema(description = "Серия паспорта", example = "2023")
    private String series;

    @Schema(description = "Номер паспорта", example = "600974")
    private String number;

    @Schema(description = "Ведомство, выдавшее паспорт",
            example = "The main Directorate of the MIA of the Voronezh region")
    private String issueBranch;

    @Schema(description = "Дата выдачи паспорта", example = "2021-01-12")
    private LocalDate issueDate;
}
