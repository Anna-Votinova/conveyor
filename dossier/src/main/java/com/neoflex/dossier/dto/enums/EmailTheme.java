package com.neoflex.dossier.dto.enums;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum EmailTheme {

    FINISH_REGISTRATION("Finish registration"),
    CREATE_DOCUMENTS("Create documents"),
    SEND_DOCUMENTS("Your loan documents"),
    SEND_SES("Sign documents with SES code"),
    CREDIT_ISSUED("Credit issued"),
    APPLICATION_DENIED("Application denied");

    private final String value;
}
