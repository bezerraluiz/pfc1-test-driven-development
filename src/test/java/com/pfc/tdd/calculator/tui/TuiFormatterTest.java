package com.pfc.tdd.calculator.tui;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.pfc.tdd.calculator.domain.CalculationResult;

public class TuiFormatterTest {

    private final TuiFormatter formatter = new TuiFormatter();
    private String panel;

    @BeforeEach
    void setUp() {
        panel = TestHelper.stripAnsi(formatter.format(new CalculationResult(
                new BigDecimal("5000.00"),
                new BigDecimal("509.60"),
                new BigDecimal("479.00"),
                new BigDecimal("4011.40"))));
    }

    @Test
    void shouldContainHeader() {
        assertTrue(panel.contains("Resumo do Salário"));
    }

    @Test
    void shouldContainGrossLabel() {
        assertTrue(panel.contains("Salário Bruto"));
    }

    @Test
    void shouldContainFormattedGrossValue() {
        assertTrue(panel.contains("R$ 5.000,00"));
    }

    @Test
    void shouldContainInssLabel() {
        assertTrue(panel.contains("(-) INSS"));
    }

    @Test
    void shouldContainFormattedInssValue() {
        assertTrue(panel.contains("R$ 509,60"));
    }

    @Test
    void shouldContainIrrfLabel() {
        assertTrue(panel.contains("(-) IRRF"));
    }

    @Test
    void shouldContainFormattedIrrfValue() {
        assertTrue(panel.contains("R$ 479,00"));
    }

    @Test
    void shouldContainNetLabel() {
        assertTrue(panel.contains("Salário Líquido"));
    }

    @Test
    void shouldContainFormattedNetValue() {
        assertTrue(panel.contains("R$ 4.011,40"));
    }
}
