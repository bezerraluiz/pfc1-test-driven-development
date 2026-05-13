package com.pfc.tdd.calculator.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

public class GrossSalaryTest {

    @Test
    void shouldCreateGrossSalaryWithValidValue() {
        final GrossSalary grossSalary = new GrossSalary(new BigDecimal("3000.00"));

        assertEquals(0, new BigDecimal("3000.00").compareTo(grossSalary.value()));
    }

    @Test
    void shouldCreateGrossSalaryWithMinimumValidValue() {
        final GrossSalary grossSalary = new GrossSalary(new BigDecimal("0.01"));

        assertEquals(0, new BigDecimal("0.01").compareTo(grossSalary.value()));
    }

    @Test
    void shouldThrowIllegalArgumentExceptionForZeroValue() {
        assertThrows(IllegalArgumentException.class,
                () -> new GrossSalary(new BigDecimal("0.00")));
    }

    @Test
    void shouldThrowIllegalArgumentExceptionForNegativeValue() {
        assertThrows(IllegalArgumentException.class,
                () -> new GrossSalary(new BigDecimal("-100.00")));
    }

    @Test
    void shouldThrowNullPointerExceptionForNullValue() {
        assertThrows(NullPointerException.class,
                () -> new GrossSalary(null));
    }
}
