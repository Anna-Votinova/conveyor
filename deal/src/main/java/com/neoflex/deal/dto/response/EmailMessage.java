package com.neoflex.deal.dto.response;

import com.neoflex.deal.dto.enums.EmailTheme;
import com.neoflex.deal.dto.SesCodeDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
@Builder
@Schema(description = "ДТО для отправки писем на почту клиентам")
public class EmailMessage {

    @Schema(description = "Почтовый ящик", example = "anyvotinova@yandex.ru")
    private String address;

    @Schema(description = "Тема письма", example = "FINISH_REGISTRATION")
    private EmailTheme theme;

    @Schema(description = "Идентификатор заявки", example = "1")
    private Long applicationId;

    @Schema(description = "Код для подписания документов", example = "808e1cc4630440858f5199e4c0a3e706")
    private SesCodeDTO sesCode;

    @Schema(description = "Информация о кредите и клиенте для подготовки документов")
    private DocumentDTO document;
}
