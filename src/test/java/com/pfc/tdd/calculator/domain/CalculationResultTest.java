package com.pfc.tdd.calculator.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

public class CalculationResultTest {

    @Test
    void shouldCreateCalculationResultWithValidValues() {
        final BigDecimal gross = new BigDecimal("5000.00");
        final BigDecimal inss = new BigDecimal("509.60");
        final BigDecimal irrf = new BigDecimal("479.00");
        final BigDecimal net = new BigDecimal("4011.40");

        final CalculationResult result = new CalculationResult(gross, inss, irrf, net);

        assertEquals(0, gross.compareTo(result.gross()));
        assertEquals(0, inss.compareTo(result.inss()));
        assertEquals(0, irrf.compareTo(result.irrf()));
        assertEquals(0, net.compareTo(result.net()));
    }
}
