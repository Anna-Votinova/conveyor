package com.neoflex.conveyor.config;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class GlobalVariables {

    public static final String LATIN_LANG = "[a-zA-Z]+";

    public static final String PASSPORT_SERIES_FORMAT = "[\\d]{4}";

    public static final String PASSPORT_NUMBER_FORMAT = "[\\d]{6}";

    public static final BigDecimal INSURANCE_RATIO = new BigDecimal("0.1");

}
