package com.pfc.tdd.calculator.tui;

import com.pfc.tdd.calculator.domain.GrossSalary;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class InputParserTest {

    private InputParser parser;

    @BeforeEach
    void setUp() {
        parser = new InputParser();
    }

    @Test
    void entradaValida_deveRetornarGrossSalary() {
        GrossSalary result = parser.parse("3000");
        assertEquals(new BigDecimal("3000"), result.value());
    }

    @Test
    void entradaComVirgula_deveAceitarComoDecimal() {
        GrossSalary result = parser.parse("3000,50");
        assertEquals(new BigDecimal("3000.50"), result.value());
    }

    @Test
    void entradaComPonto_deveAceitarComoDecimal() {
        GrossSalary result = parser.parse("3000.50");
        assertEquals(new BigDecimal("3000.50"), result.value());
    }

    @Test
    void entradaNula_deveLancarInvalidInputException() {
        assertThrows(InvalidInputException.class, () -> parser.parse(null));
    }

    @Test
    void entradaVazia_deveLancarInvalidInputException() {
        assertThrows(InvalidInputException.class, () -> parser.parse(""));
    }

    @Test
    void entradaEmBranco_deveLancarInvalidInputException() {
        assertThrows(InvalidInputException.class, () -> parser.parse("   "));
    }

    @Test
    void entradaComTexto_deveLancarInvalidInputException() {
        assertThrows(InvalidInputException.class, () -> parser.parse("abc"));
    }

    @Test
    void entradaComSimbolo_deveLancarInvalidInputException() {
        assertThrows(InvalidInputException.class, () -> parser.parse("R$3000"));
    }

    @Test
    void entradaZero_deveLancarInvalidInputException() {
        assertThrows(InvalidInputException.class, () -> parser.parse("0"));
    }

    @Test
    void entradaNegativa_deveLancarInvalidInputException() {
        assertThrows(InvalidInputException.class, () -> parser.parse("-500"));
    }

    @Test
    void mensagemDeErroParaEntradaInvalida_deveEstarEmPortugues() {
        InvalidInputException ex = assertThrows(InvalidInputException.class, () -> parser.parse("abc"));
        assertTrue(ex.getMessage().contains("inválida") || ex.getMessage().contains("válido"));
    }

    @Test
    void mensagemDeErroParaSalarioZero_deveEstarEmPortugues() {
        InvalidInputException ex = assertThrows(InvalidInputException.class, () -> parser.parse("0"));
        assertTrue(ex.getMessage().contains("maior") || ex.getMessage().contains("zero"));
    }
}
