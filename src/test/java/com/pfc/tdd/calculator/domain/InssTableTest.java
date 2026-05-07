package com.pfc.tdd.calculator.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

public class InssTableTest {

    private final InssTable inssTable = new InssTable();

    // Salário dentro da 1ª faixa (7,5%)
    @Test
    void shouldCalculateInssForFirstBracketSalary() {
        final BigDecimal salary = new BigDecimal("1500.00");
        final BigDecimal expected = new BigDecimal("112.50");

        final BigDecimal result = inssTable.calculate(salary);

        assertEquals(0, expected.compareTo(result));
    }

    // Salário no limite da 1ª faixa (7,5%)
    @Test
    void shouldCalculateInssForSalaryAtTheEdgeOfTheFirstBracket() {
        final BigDecimal salary = new BigDecimal("1621.00");
        final BigDecimal expected = new BigDecimal("121.58");

        final BigDecimal result = inssTable.calculate(salary);

        assertEquals(0, expected.compareTo(result));
    }

    // Salário atravessa até a 3ª faixa (12%)
    @Test
    void shouldCalculateInssForSalaryCrossingTheThirdBracket() {
        final BigDecimal salary = new BigDecimal("3000.00");
        final BigDecimal expected = new BigDecimal("248.60");

        final BigDecimal result = inssTable.calculate(salary);

        assertEquals(0, expected.compareTo(result));
    }

    // Salário atravessa até a 4ª faixa (14%)
    @Test
    void shouldCalculateInssForSalaryCrossingTheFourthBracket() {
        final BigDecimal salary = new BigDecimal("5000.00");
        final BigDecimal expected = new BigDecimal("501.51");

        final BigDecimal result = inssTable.calculate(salary);

        assertEquals(0, expected.compareTo(result));
    }

    // Salário teto do INSS (14%)
    @Test
    void shouldCalculateInssForSalaryAtTheCeilingOfTheInss() {
        final BigDecimal salary = new BigDecimal("8475.55");
        final BigDecimal expected = new BigDecimal("988.09");

        final BigDecimal result = inssTable.calculate(salary);

        assertEquals(0, expected.compareTo(result));
    }
}
