package com.pfc.tdd.calculator.domain;

import java.math.BigDecimal;

public record CalculationResult(
        BigDecimal gross,
        BigDecimal inss,
        BigDecimal irrf,
        BigDecimal net) {
}
