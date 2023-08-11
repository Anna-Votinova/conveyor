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

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "История статусов заявки")
public final class ApplicationStatusHistoryDTO {

    private ApplicationStatus status;

    private LocalDateTime time;

    private ChangeType changeType;
}
