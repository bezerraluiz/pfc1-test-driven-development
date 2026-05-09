package com.pfc.tdd.calculator.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

public class IrrfTableTest {

    private final IrrfTable irrfTable = new IrrfTable();

    // Base abaixo do limite de isenção -> IRRF = 0
    @Test
    void shouldReturnZeroIrrfWhenBaseIsBelowExemptionLimit() {
        final BigDecimal base = new BigDecimal("1500.00");
        final BigDecimal expected = new BigDecimal("0.00");

        final BigDecimal result = irrfTable.calculate(base);

        assertEquals(0, expected.compareTo(result));
    }

    // faixa 2 = 7,5%
    @Test
    void shouldCalculateIrrfForSecondBracket() {
        final BigDecimal base = new BigDecimal("2490.40");
        final BigDecimal expected = new BigDecimal("4.62");

        final BigDecimal result = irrfTable.calculate(base);

        assertEquals(0, expected.compareTo(result));
    }

    // faixa 4 = 22,5%
    @Test
    void shouldCalculateIrrfForFourthBracket() {
        final BigDecimal base = new BigDecimal("4490.40");
        final BigDecimal expected = new BigDecimal("334.85");

        final BigDecimal result = irrfTable.calculate(base);

        assertEquals(0, expected.compareTo(result));
    }
}
