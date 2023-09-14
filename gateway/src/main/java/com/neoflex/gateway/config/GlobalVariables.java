package com.neoflex.gateway.config;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class GlobalVariables {

    public static final String LATIN_LANG = "[a-zA-Z]+";
    public static final String PASSPORT_SERIES_FORMAT = "[\\d]{4}";
    public static final String PASSPORT_NUMBER_FORMAT = "[\\d]{6}";
}
