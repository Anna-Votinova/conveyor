package com.neoflex.gateway.dto.response.element;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDate;

@Builder
@Schema(description = "Паспорт клиента")
public record PassportInfo (
        @Schema(description = "Идентификатор паспорта", example = "1")
        Long id,

        @Schema(description = "Серия паспорта", example = "2023")
        String series,

        @Schema(description = "Номер паспорта", example = "600974")
        String number,

        @Schema(description = "Ведомство, выдавшее паспорт",
                example = "The main Directorate of the MIA of the Voronezh region")
        String issueBranch,

        @Schema(description = "Дата выдачи паспорта", example = "2021-01-12")
        LocalDate issueDate
) {}
