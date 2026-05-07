package com.pfc.tdd.calculator.domain;

import java.util.List;
import java.math.*;

public class InssTable {

    private record InssRange(BigDecimal lowerLimit, BigDecimal upperLimit, BigDecimal aliquot) {
    }

    private static final List<InssRange> RANGE_INSS = List.of(
            new InssRange(BigDecimal.ZERO, new BigDecimal("1621.00"), new BigDecimal("0.075")),
            new InssRange(new BigDecimal("1621.00"), new BigDecimal("2902.84"), new BigDecimal("0.09")),
            new InssRange(new BigDecimal("2902.84"), new BigDecimal("4354.27"), new BigDecimal("0.12")),
            new InssRange(new BigDecimal("4354.27"), new BigDecimal("8475.55"), new BigDecimal("0.14")));

    public BigDecimal calculate(BigDecimal grossSalary) {
        BigDecimal total = BigDecimal.ZERO;

        for (InssRange range : RANGE_INSS) {
            total = total.add(calculateInstallment(grossSalary, range.lowerLimit(),
                    range.upperLimit(), range.aliquot()));
        }

        return total.setScale(2, RoundingMode.HALF_EVEN);
    }

    private BigDecimal calculateInstallment(BigDecimal grossSalary, BigDecimal lower,
            BigDecimal upper, BigDecimal aliquot) {
        if (grossSalary.compareTo(lower) <= 0)
            return BigDecimal.ZERO;

        BigDecimal base = grossSalary.min(upper).subtract(lower);

        return base.multiply(aliquot);
    }
}