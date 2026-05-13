package com.pfc.tdd.calculator.tui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import com.pfc.tdd.calculator.domain.GrossSalary;

public class InputParserTest {

    private final InputParser parser = new InputParser();

    @Test
    void shouldParseIntegerInput() {
        final GrossSalary result = parser.parse("3000");

        assertEquals(0, new BigDecimal("3000").compareTo(result.value()));
    }

    @Test
    void shouldParseInputWithDotDecimalSeparator() {
        final GrossSalary result = parser.parse("3000.50");

        assertEquals(0, new BigDecimal("3000.50").compareTo(result.value()));
    }

    @Test
    void shouldParseInputWithCommaDecimalSeparator() {
        final GrossSalary result = parser.parse("3000,50");

        assertEquals(0, new BigDecimal("3000.50").compareTo(result.value()));
    }

    @Test
    void shouldThrowInvalidInputExceptionForNonNumericInput() {
        final InvalidInputException exception = assertThrows(InvalidInputException.class,
                () -> parser.parse("abc"));

        assertEquals("Entrada inválida. Digite um número válido, como 3000 ou 3000,50.", exception.getMessage());
    }

    @Test
    void shouldThrowInvalidInputExceptionForZeroInput() {
        final InvalidInputException exception = assertThrows(InvalidInputException.class,
                () -> parser.parse("0"));

        assertEquals("O salário deve ser maior que zero.", exception.getMessage());
    }

    @Test
    void shouldThrowInvalidInputExceptionForEmptyInput() {
        final InvalidInputException exception = assertThrows(InvalidInputException.class,
                () -> parser.parse(""));

        assertEquals("Entrada inválida. Digite um número válido, como 3000 ou 3000,50.", exception.getMessage());
    }
}
