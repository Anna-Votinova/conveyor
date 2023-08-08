package com.neoflex.conveyor.service.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum CalculationFormulaConstants {

    MONTHS_IN_YEAR("12"),
    NUMBER_PERCENTAGE("0.01"),
    ONE_RATIO("1"),
    RATIO_HUNDRED("100");

    private final String value;
}
