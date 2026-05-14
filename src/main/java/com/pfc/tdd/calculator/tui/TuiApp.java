package com.pfc.tdd.calculator.tui;

import com.pfc.tdd.calculator.domain.CalculationResult;
import com.pfc.tdd.calculator.domain.GrossSalary;
import com.pfc.tdd.calculator.domain.SalaryNetCalculator;
import java.io.PrintStream;
import java.util.Scanner;

public class TuiApp {

    private static final String BANNER = """
            ╭──────────────────────────────────────╮
            │   Calculadora de Salário Líquido     │
            │             2026                     │
            ╰──────────────────────────────────────╯""";

    private final SalaryNetCalculator calculator;
    private final InputParser parser;

    public TuiApp(SalaryNetCalculator calculator, InputParser parser) {
        this.calculator = calculator;
        this.parser = parser;
    }

    public void execute(Scanner scanner, PrintStream output) {
        displayBanner(output);
        while (true) {
            output.print(" Digite o salário bruto (R$): ");
            var input = scanner.nextLine().trim();
            GrossSalary grossSalary;
            try {
                grossSalary = parser.parse(input);
            } catch (InvalidInputException e) {
                output.println(e.getMessage());
                continue;
            }
            var result = output == System.out
                    ? calculateWithAnimation(grossSalary, output)
                    : calculator.calculate(grossSalary);
            output.println(new TuiFormatter().format(result));
            break;
        }
    }

    private void displayBanner(PrintStream output) {
        output.println(BANNER);
    }

    private CalculationResult calculateWithAnimation(GrossSalary grossSalary, PrintStream output) {
        var animator = new TuiAnimator("Calculando...", output);
        animator.start();
        var result = calculator.calculate(grossSalary);
        animator.stop();
        return result;
    }
}
