package com.pfc.tdd.calculator.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class InssTable {

    private record Bracket(BigDecimal lowerLimit, BigDecimal upperLimit, BigDecimal aliquot) {}

    private static final List<Bracket> TABLE = List.of(
        new Bracket(BigDecimal.ZERO,             new BigDecimal("1621.00"), new BigDecimal("0.075")),
        new Bracket(new BigDecimal("1621.00"),   new BigDecimal("2902.84"), new BigDecimal("0.09")),
        new Bracket(new BigDecimal("2902.84"),   new BigDecimal("4354.27"), new BigDecimal("0.12")),
        new Bracket(new BigDecimal("4354.27"),   new BigDecimal("8475.55"), new BigDecimal("0.14"))
    );

    public BigDecimal calculate(BigDecimal grossSalary) {
        BigDecimal total = BigDecimal.ZERO;
        for (Bracket bracket : TABLE) {
            total = total.add(calculateInstallment(grossSalary, bracket.lowerLimit(), bracket.upperLimit(), bracket.aliquot()));
        }
        return total.setScale(2, RoundingMode.HALF_EVEN);
    }

    private BigDecimal calculateInstallment(BigDecimal grossSalary, BigDecimal lowerLimit, BigDecimal upperLimit, BigDecimal aliquot) {
        if (grossSalary.compareTo(lowerLimit) <= 0) {
            return BigDecimal.ZERO;
        }
        return grossSalary.min(upperLimit).subtract(lowerLimit).multiply(aliquot);
    }
}
