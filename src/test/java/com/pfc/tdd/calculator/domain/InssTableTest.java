package com.pfc.tdd.calculator.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InssTableTest {

    private InssTable inssTable;

    @BeforeEach
    void setUp() {
        inssTable = new InssTable();
    }

    @Test
    void salarioDentroFaixa1_deveAplicarSomente7Porcento() {
        BigDecimal result = inssTable.calculate(new BigDecimal("1500.00"));
        assertEquals(new BigDecimal("112.50"), result);
    }

    @Test
    void salarioNoTetoFaixa1_deveCalcularCorretamente() {
        BigDecimal result = inssTable.calculate(new BigDecimal("1621.00"));
        assertEquals(new BigDecimal("121.58"), result);
    }

    @Test
    void salarioNaFaixa3_deveAplicarFaixasProgressivas() {
        BigDecimal result = inssTable.calculate(new BigDecimal("3000.00"));
        assertEquals(new BigDecimal("248.60"), result);
    }

    @Test
    void salarioNaFaixa4_deveAplicarTodasAsFaixas() {
        BigDecimal result = inssTable.calculate(new BigDecimal("5000.00"));
        assertEquals(new BigDecimal("501.51"), result);
    }

    @Test
    void salarioNoTeto_deveCalcularValorMaximoDeInss() {
        BigDecimal result = inssTable.calculate(new BigDecimal("8475.55"));
        assertEquals(new BigDecimal("988.09"), result);
    }
}
