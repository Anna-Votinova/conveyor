package com.neoflex.conveyor.config;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class GlobalVariables {

    public static final String LATIN_LANG = "[a-zA-Z]+";

    public static final String DATE_FORMAT = "yyyy-MM-dd";

    public static final String EMAIL_FORMAT = "[\\w\\.]{2,50}@[\\w\\.]{2,20}";

    public static final String PASSPORT_SERIES_FORMAT = "[\\d]{4}";

    public static final String PASSPORT_NUMBER_FORMAT = "[\\d]{6}";

    public static final String ACCOUNT_FORMAT = "[\\d]{20}";

    public static final BigDecimal MONTHS_IN_YEAR = new BigDecimal("12");

    public static final BigDecimal NUMBER_PERCENTAGE = BigDecimal.valueOf(0.01);

    public static final BigDecimal ONE_RATIO = BigDecimal.ONE;

}
