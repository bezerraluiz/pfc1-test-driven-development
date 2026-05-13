package com.pfc.tdd.calculator.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

public class SalaryNetCalculatorTest {

    private final SalaryNetCalculator calculator = new SalaryNetCalculator(new InssTable(), new IrrfTable());

    // Salário na 3ª faixa INSS (12%) e 2ª faixa IRRF (7.5%)
    @Test
    void shouldCalculateNetSalaryForThirdInssBracket() {
        final CalculationResult result = calculator.calculate(new GrossSalary(new BigDecimal("3000.00")));

        assertEquals(0, new BigDecimal("3000.00").compareTo(result.gross()));
        assertEquals(0, new BigDecimal("248.60").compareTo(result.inss()));
        assertEquals(0, new BigDecimal("24.20").compareTo(result.irrf()));
        assertEquals(0, new BigDecimal("2727.20").compareTo(result.net()));
    }

    // Salário na 4ª faixa INSS (14%) e 4ª faixa IRRF (22.5%)
    @Test
    void shouldCalculateNetSalaryForFourthInssBracket() {
        final CalculationResult result = calculator.calculate(new GrossSalary(new BigDecimal("5000.00")));

        assertEquals(0, new BigDecimal("5000.00").compareTo(result.gross()));
        assertEquals(0, new BigDecimal("501.51").compareTo(result.inss()));
        assertEquals(0, new BigDecimal("336.67").compareTo(result.irrf()));
        assertEquals(0, new BigDecimal("4161.82").compareTo(result.net()));
    }
}
