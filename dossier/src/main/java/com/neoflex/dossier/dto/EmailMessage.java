package com.neoflex.dossier.dto;


import com.neoflex.dossier.dto.enums.EmailTheme;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "ДТО для отправки писем на почту клиентам")
public record EmailMessage(
    @Schema(description = "Почтовый ящик", example = "kotova@yandex.ru")
    String address,

    @Schema(description = "Тема письма", example = "FINISH_REGISTRATION")
    EmailTheme theme,

    @Schema(description = "Идентификатор заявки", example = "1")
    Long applicationId,

    @Schema(description = "Код для подписания документов", example = "808e1cc4630440858f5199e4c0a3e706")
    SesCodeDTO sesCode,

    @Schema(description = "Информация о кредите и клиенте для подготовки документов")
    DocumentDTO document
) {}
