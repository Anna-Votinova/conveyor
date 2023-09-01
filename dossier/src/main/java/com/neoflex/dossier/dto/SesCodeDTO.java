package com.neoflex.dossier.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import javax.validation.constraints.NotNull;

@Builder
@Schema(description = "Код для подписания документов")
public record SesCodeDTO(@NotNull @Schema(example = "1234") Integer code) {
}
