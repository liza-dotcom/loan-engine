package com.finengine.loan_engine.util;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

public final class FinancialMath {

    public static final int INTERMEDIATE_SCALE = 10; // keep 10 decimal places for each value use din calculation
    public static final int MONETARY_SCALE = 2; // after calculation reduce to 2 decimal places
    public static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_EVEN; // tackle the half up bias in the long run

    // private constructor to avoid instanciating from a utility class 
    private FinancialMath() {
        // utility class — never instantiated
    }

    public static BigDecimal toMonetary(BigDecimal value) {
        return value.setScale(MONETARY_SCALE, ROUNDING_MODE);
    }

    public static BigDecimal divide(BigDecimal numerator, BigDecimal denominator) {
        return numerator.divide(denominator, INTERMEDIATE_SCALE, ROUNDING_MODE);
    }

    public static BigDecimal power(BigDecimal base, int exponent) {
        return base.pow(exponent, new MathContext(INTERMEDIATE_SCALE + 5, ROUNDING_MODE));
    }
}