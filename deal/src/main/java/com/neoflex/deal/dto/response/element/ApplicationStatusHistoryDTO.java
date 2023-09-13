package com.neoflex.deal.dto.response.element;

import com.neoflex.deal.entity.enums.ApplicationStatus;
import com.neoflex.deal.entity.enums.ChangeType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Schema(description = "Элемент истории статусов заявки")
public class ApplicationStatusHistoryDTO {

    @Schema(description = "Идентификатор элемента истории статусов заявки",
            example = "076fcb4c-22a3-4d04-b72a-b656446daa68")
    private UUID id;

    @Schema(description = "Статус заявки", example = "PREAPPROVAL")
    private ApplicationStatus status;

    @Schema(description = "Дата присваивания заявке нового статуса", example = "2023-09-07 23:44:32.970197")
    private LocalDateTime time;

    @Schema(description = "Каким образом был изменен статус заявки", example = "AUTOMATIC")
    private ChangeType changeType;
}
