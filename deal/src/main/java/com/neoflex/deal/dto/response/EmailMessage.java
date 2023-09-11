package com.neoflex.deal.dto.response;

import com.neoflex.deal.dto.enums.EmailTheme;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
@Builder
public class EmailMessage {

    private String address;
    private EmailTheme theme;
    private Long applicationId;
    private Integer sesCode;
    private DocumentDTO document;
}
