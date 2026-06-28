package com.pfc.tdd.calculator.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class SalaryNetCalculatorTest {

    private SalaryNetCalculator calculator;

    @BeforeEach
    void setUp() {
        calculator = new SalaryNetCalculator(new InssTable(), new IrrfTable());
    }

    @Test
    void salarioIsento_deveCalcularApenasInss() {
        var result = calculator.calculate(new GrossSalary(new BigDecimal("1500.00")));

        assertEquals(new BigDecimal("1500.00"), result.gross());
        assertEquals(new BigDecimal("112.50"), result.inss());
        assertEquals(new BigDecimal("0.00"), result.irrf());
        assertEquals(new BigDecimal("1387.50"), result.net());
    }

    @Test
    void salarioComInssEIrrf_deveCalcularTodosOsDescontos() {
        var result = calculator.calculate(new GrossSalary(new BigDecimal("5000.00")));

        assertEquals(new BigDecimal("5000.00"), result.gross());
        assertEquals(new BigDecimal("501.51"), result.inss());

        BigDecimal expectedTaxableBase = new BigDecimal("5000.00").subtract(new BigDecimal("501.51"));
        BigDecimal expectedNet = new BigDecimal("5000.00").subtract(result.inss()).subtract(result.irrf());

        assertTrue(result.irrf().compareTo(BigDecimal.ZERO) > 0);
        assertEquals(expectedNet, result.net());
    }

    @Test
    void resultadoNaoDevolveValorLiquidoNegativo() {
        var result = calculator.calculate(new GrossSalary(new BigDecimal("1000.00")));
        assertTrue(result.net().compareTo(BigDecimal.ZERO) > 0);
    }

    @Test
    void salarioComVirgula_devePreservarValores() {
        var result = calculator.calculate(new GrossSalary(new BigDecimal("3000.50")));
        BigDecimal expectedNet = result.gross().subtract(result.inss()).subtract(result.irrf());
        assertEquals(expectedNet, result.net());
    }
}
