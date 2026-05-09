package com.pfc.tdd.calculator.domain;

import java.util.List;
import java.math.*;

public class IrrfTable {

    private record IrrfBracket(BigDecimal upperLimit, BigDecimal aliquot, BigDecimal deduction) {
    }

    private static final List<IrrfBracket> BRACKETS = List.of(
            new IrrfBracket(new BigDecimal("2428.80"), new BigDecimal("0.000"), new BigDecimal("0.00")),
            new IrrfBracket(new BigDecimal("2826.65"), new BigDecimal("0.075"), new BigDecimal("182.16")),
            new IrrfBracket(new BigDecimal("3751.05"), new BigDecimal("0.150"), new BigDecimal("394.16")),
            new IrrfBracket(new BigDecimal("4664.68"), new BigDecimal("0.225"), new BigDecimal("675.49")),
            new IrrfBracket(null, new BigDecimal("0.275"), new BigDecimal("908.73")));

    public BigDecimal calculate(BigDecimal taxableBase) {
        for (IrrfBracket bracket : BRACKETS) {
            if (bracket.upperLimit() == null || taxableBase.compareTo(bracket.upperLimit()) <= 0) {
                BigDecimal result = taxableBase
                        .multiply(bracket.aliquot())
                        .subtract(bracket.deduction())
                        .setScale(2, RoundingMode.HALF_EVEN);

                return result.max(BigDecimal.ZERO);
            }
        }

        return BigDecimal.ZERO.setScale(2);
    }
}