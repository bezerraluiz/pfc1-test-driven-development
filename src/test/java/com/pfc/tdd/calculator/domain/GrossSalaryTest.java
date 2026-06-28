package com.pfc.tdd.calculator.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class GrossSalaryTest {

    @Test
    void valorPositivo_deveCriarObjeto() {
        GrossSalary salary = new GrossSalary(new BigDecimal("3000.00"));
        assertEquals(new BigDecimal("3000.00"), salary.value());
    }

    @Test
    void valorZero_deveLancarExcecao() {
        assertThrows(IllegalArgumentException.class, () ->
            new GrossSalary(BigDecimal.ZERO)
        );
    }

    @Test
    void valorNegativo_deveLancarExcecao() {
        assertThrows(IllegalArgumentException.class, () ->
            new GrossSalary(new BigDecimal("-100.00"))
        );
    }
}
