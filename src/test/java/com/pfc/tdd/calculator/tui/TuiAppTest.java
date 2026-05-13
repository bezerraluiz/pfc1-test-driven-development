package com.pfc.tdd.calculator.tui;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Scanner;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.pfc.tdd.calculator.domain.InssTable;
import com.pfc.tdd.calculator.domain.IrrfTable;
import com.pfc.tdd.calculator.domain.SalaryNetCalculator;

public class TuiAppTest {

    private TuiApp app;

    @BeforeEach
    void setUp() {
        app = new TuiApp(new SalaryNetCalculator(new InssTable(), new IrrfTable()), new InputParser());
    }

    private String execute(String inputLines) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        app.execute(new Scanner(inputLines), new PrintStream(baos));
        return baos.toString();
    }

    @Test
    void shouldDisplayResultForValidSalary() {
        assertTrue(execute("3000\n").contains("Salário Líquido"));
    }

    @Test
    void shouldRetryAndDisplayResultAfterInvalidNumericInput() {
        String output = execute("abc\n3000\n");
        assertTrue(output.contains("Entrada inválida"));
        assertTrue(output.contains("Salário Líquido"));
    }

    @Test
    void shouldRetryAndDisplayResultAfterNegativeInput() {
        String output = execute("-100\n3000\n");
        assertTrue(output.contains("maior que zero"));
        assertTrue(output.contains("Salário Líquido"));
    }

    @Test
    void shouldRetryAndDisplayResultAfterZeroInput() {
        String output = execute("0\n3000\n");
        assertTrue(output.contains("maior que zero"));
        assertTrue(output.contains("Salário Líquido"));
    }

    @Test
    void shouldNotExposeStackTraceOnInvalidInput() {
        String output = execute("abc\n3000\n");
        assertFalse(output.contains("Exception"));
        assertFalse(output.contains("at com."));
    }
}
