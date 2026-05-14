package com.pfc.tdd.calculator.domain;

import java.math.BigDecimal;

public record GrossSalary(BigDecimal value) {
    public GrossSalary {
        if (value.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Gross salary must be greater than zero");
        }
    }
}
