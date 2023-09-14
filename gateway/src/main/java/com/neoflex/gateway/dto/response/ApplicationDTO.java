package com.neoflex.gateway.dto.response;

import com.neoflex.gateway.dto.enums.ApplicationStatus;
import com.neoflex.gateway.dto.response.element.ApplicationStatusHistoryDTO;
import com.neoflex.gateway.dto.response.element.AppliedOfferInfo;
import com.neoflex.gateway.dto.response.element.ClientInfo;
import com.neoflex.gateway.dto.response.element.CreditInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Schema(description = "Заявка на кредит c информацией о клиенте и кредите")
public record ApplicationDTO(
        @Schema(description = "Идентификатор заявки", example = "1")
        Long id,

        ClientInfo clientInfo,

        CreditInfo creditInfo,

        @Schema(description = "Статус заявки", example = "PREAPPROVAL")
        ApplicationStatus status,

        @Schema(description = "Дата создания заявки", example = "2023-09-07 23:44:32.970197")
        LocalDateTime creationDate,

        AppliedOfferInfo appliedOfferInfo,

        @Schema(description = "Дата подписания документов на кредит", example = "2023-09-07 24:44:32.970197")
        LocalDateTime signDate,

        @Schema(description = "История статусов заявки")
        List<ApplicationStatusHistoryDTO> statusHistory
) {}
