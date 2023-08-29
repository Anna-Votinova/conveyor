package com.neoflex.deal.dto.response;

import com.neoflex.deal.dto.enums.EmailTheme;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Schema(description = "ДТО для отправки писем на почту клиентам")
public class EmailMessage {

    @Schema(description = "Почтовый ящик", example = "kotova@yandex.ru")
    private String address;

    @Schema(description = "Тема письма", example = "FINISH_REGISTRATION")
    private EmailTheme theme;

    @Schema(description = "Идентификатор заявки", example = "1")
    private Long applicationId;
}
