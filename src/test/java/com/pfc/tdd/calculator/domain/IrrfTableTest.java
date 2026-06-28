package com.pfc.tdd.calculator.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class IrrfTableTest {

    private IrrfTable irrfTable;

    @BeforeEach
    void setUp() {
        irrfTable = new IrrfTable();
    }

    @Test
    void baseAbaixoDaIsencao_deveRetornarZero() {
        BigDecimal result = irrfTable.calculate(new BigDecimal("1500.00"));
        assertEquals(new BigDecimal("0.00"), result);
    }

    @Test
    void baseLimiteExatoDaIsencao_deveRetornarZero() {
        BigDecimal result = irrfTable.calculate(new BigDecimal("2428.80"));
        assertEquals(new BigDecimal("0.00"), result);
    }

    @Test
    void baseNaFaixa3_deveCalcularIrrf() {
        BigDecimal result = irrfTable.calculate(new BigDecimal("3500.00"));
        assertEquals(new BigDecimal("130.84"), result);
    }

    @Test
    void baseNaFaixa4_deveCalcularIrrf() {
        BigDecimal result = irrfTable.calculate(new BigDecimal("4490.40"));
        assertEquals(new BigDecimal("334.85"), result);
    }

    @Test
    void baseMuitoAlta_naoDeveRetornarNegativo() {
        BigDecimal result = irrfTable.calculate(new BigDecimal("10000.00"));
        assertEquals(1, result.compareTo(BigDecimal.ZERO));
    }
}
