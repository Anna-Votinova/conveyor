package com.neoflex.dossier.dto;


import com.neoflex.dossier.dto.enums.EmailTheme;
import lombok.Builder;

@Builder
public record EmailMessage(
    String address,
    EmailTheme theme,
    Long applicationId,
    Integer sesCode,
    DocumentDTO document
) {}
