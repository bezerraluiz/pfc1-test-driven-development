package com.pfc.tdd.calculator.tui;

import java.io.PrintStream;
import java.util.Scanner;

import com.pfc.tdd.calculator.domain.CalculationResult;
import com.pfc.tdd.calculator.domain.GrossSalary;
import com.pfc.tdd.calculator.domain.SalaryNetCalculator;

public class TuiApp {

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
            String input = scanner.nextLine().trim();
            GrossSalary grossSalary;
            try {
                grossSalary = parser.parse(input);
            } catch (InvalidInputException e) {
                output.println(" " + e.getMessage());
                continue;
            }
            CalculationResult result = calculateWithAnimation(grossSalary, output);
            output.println(new TuiFormatter().format(result));
            break;
        }
    }

    private CalculationResult calculateWithAnimation(GrossSalary grossSalary, PrintStream output) {
        if (output != System.out) {
            return calculator.calculate(grossSalary);
        }
        TuiAnimator animator = new TuiAnimator("Calculando...", output);
        animator.start();
        CalculationResult result = calculator.calculate(grossSalary);
        animator.stop();
        return result;
    }

    private static void displayBanner(PrintStream output) {
        output.println("╭──────────────────────────────────────╮");
        output.println("│   Calculadora de Salário Líquido     │");
        output.println("│             2026                     │");
        output.println("╰──────────────────────────────────────╯");
    }
}
