package com.neoflex.deal.dto.response;

import com.neoflex.deal.dto.response.element.ApplicationStatusHistoryDTO;
import com.neoflex.deal.dto.response.element.AppliedOfferInfo;
import com.neoflex.deal.dto.response.element.ClientInfo;
import com.neoflex.deal.dto.response.element.CreditInfo;
import com.neoflex.deal.entity.enums.ApplicationStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Schema(description = "Заявка на кредит c информацией о клиенте и кредите")
public class ApplicationDTO {

    @Schema(description = "Идентификатор заявки", example = "1")
    private Long id;

    private ClientInfo clientInfo;

    private CreditInfo creditInfo;

    @Schema(description = "Статус заявки", example = "PREAPPROVAL")
    private ApplicationStatus status;

    @Schema(description = "Дата создания заявки", example = "2023-09-07 23:44:32.970197")
    private LocalDateTime creationDate;

    private AppliedOfferInfo appliedOfferInfo;

    @Schema(description = "Дата подписания документов на кредит", example = "2023-09-07 24:44:32.970197")
    private LocalDateTime signDate;

    @Schema(description = "История статусов заявки")
    private List<ApplicationStatusHistoryDTO> statusHistory;
}
