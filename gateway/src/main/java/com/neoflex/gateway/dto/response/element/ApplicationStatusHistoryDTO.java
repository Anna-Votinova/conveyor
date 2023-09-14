package com.neoflex.gateway.dto.response.element;

import com.neoflex.gateway.dto.enums.ApplicationStatus;
import com.neoflex.gateway.dto.enums.ChangeType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Schema(description = "Элемент истории статусов заявки")
public record ApplicationStatusHistoryDTO (
        @Schema(description = "Идентификатор элемента истории статусов заявки",
                example = "076fcb4c-22a3-4d04-b72a-b656446daa68")
        UUID id,

        @Schema(description = "Статус заявки", example = "PREAPPROVAL")
        ApplicationStatus status,

        @Schema(description = "Дата присваивания заявке нового статуса", example = "2023-09-07 23:44:32.970197")
        LocalDateTime time,

        @Schema(description = "Каким образом был изменен статус заявки", example = "AUTOMATIC")
        ChangeType changeType
) {}
