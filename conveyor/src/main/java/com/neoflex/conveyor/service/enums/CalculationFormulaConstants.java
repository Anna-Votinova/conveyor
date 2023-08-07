package com.neoflex.conveyor.service.enums;

import java.math.BigDecimal;

public enum CalculationFormulaConstants {

    MONTHS_IN_YEAR("12"),
    NUMBER_PERCENTAGE("0.01"),
    ONE_RATIO("1"),
    RATIO_HUNDRED("100");

    private final String value;

    CalculationFormulaConstants(String value) {
        this.value = value;
    }

    public BigDecimal getValue() {
        return new BigDecimal(value);
    }
}
