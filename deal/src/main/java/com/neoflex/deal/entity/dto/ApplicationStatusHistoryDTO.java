package com.neoflex.deal.entity.dto;

import com.neoflex.deal.entity.enums.ApplicationStatus;
import com.neoflex.deal.entity.enums.ChangeType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.sql.Timestamp;

@Builder
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Сущность для ведения истории статусов заявки")
public class ApplicationStatusHistoryDTO {

    @Schema(description = "Идентификатор статуса заявки", example = "1")
    private Long id;

    @Schema(description = "Статус заявки", example = "PREAPPROVAL")
    private ApplicationStatus status;

    @Schema(description = "Дата присваивания заявке нового статуса", example = "2023-10-12")
    private Timestamp time;

    @Schema(description = "Каким образом был изменен статус заявки", example = "AUTOMATIC")
    private ChangeType changeType;
}
