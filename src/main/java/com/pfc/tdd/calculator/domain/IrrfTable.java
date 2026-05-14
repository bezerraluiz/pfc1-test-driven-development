package com.pfc.tdd.calculator.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class IrrfTable {

    private static final BigDecimal LIMIT_BRACKET_1 = new BigDecimal("2428.80");
    private static final BigDecimal LIMIT_BRACKET_2 = new BigDecimal("2826.65");
    private static final BigDecimal LIMIT_BRACKET_3 = new BigDecimal("3751.05");
    private static final BigDecimal LIMIT_BRACKET_4 = new BigDecimal("4664.68");

    private static final BigDecimal RATE_BRACKET_2 = new BigDecimal("0.075");
    private static final BigDecimal DEDUCTIBLE_BRACKET_2 = new BigDecimal("182.16");
    private static final BigDecimal RATE_BRACKET_3 = new BigDecimal("0.15");
    private static final BigDecimal DEDUCTIBLE_BRACKET_3 = new BigDecimal("394.16");
    private static final BigDecimal RATE_BRACKET_4 = new BigDecimal("0.225");
    private static final BigDecimal DEDUCTIBLE_BRACKET_4 = new BigDecimal("675.49");
    private static final BigDecimal RATE_BRACKET_5 = new BigDecimal("0.275");
    private static final BigDecimal DEDUCTIBLE_BRACKET_5 = new BigDecimal("908.73");

    public BigDecimal calculate(BigDecimal taxableBase) {
        if (taxableBase.compareTo(LIMIT_BRACKET_1) <= 0) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
        BigDecimal rate = rateFor(taxableBase);
        BigDecimal deductible = deductibleFor(taxableBase);
        BigDecimal irrf = taxableBase.multiply(rate).subtract(deductible);
        return irrf.max(BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal rateFor(BigDecimal taxableBase) {
        if (taxableBase.compareTo(LIMIT_BRACKET_2) <= 0) return RATE_BRACKET_2;
        if (taxableBase.compareTo(LIMIT_BRACKET_3) <= 0) return RATE_BRACKET_3;
        if (taxableBase.compareTo(LIMIT_BRACKET_4) <= 0) return RATE_BRACKET_4;
        return RATE_BRACKET_5;
    }

    private BigDecimal deductibleFor(BigDecimal taxableBase) {
        if (taxableBase.compareTo(LIMIT_BRACKET_2) <= 0) return DEDUCTIBLE_BRACKET_2;
        if (taxableBase.compareTo(LIMIT_BRACKET_3) <= 0) return DEDUCTIBLE_BRACKET_3;
        if (taxableBase.compareTo(LIMIT_BRACKET_4) <= 0) return DEDUCTIBLE_BRACKET_4;
        return DEDUCTIBLE_BRACKET_5;
    }
}
