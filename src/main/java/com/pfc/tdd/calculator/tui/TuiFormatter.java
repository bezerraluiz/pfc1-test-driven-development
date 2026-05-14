package com.pfc.tdd.calculator.tui;

import com.pfc.tdd.calculator.domain.CalculationResult;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

public class TuiFormatter {

    private static final String RESET = "\033[0m";
    private static final String CYAN = "\033[36m";
    private static final String RED = "\033[31m";
    private static final String GREEN_BOLD = "\033[1;32m";
    private static final int INNER_WIDTH = 38;

    public String format(CalculationResult result) {
        String line = "─".repeat(INNER_WIDTH);
        String doubleLine = "═".repeat(INNER_WIDTH);

        return String.join("\n",
            "╭" + line + "╮",
            titleLine(CYAN, "Resumo do Salário"),
            "├" + line + "┤",
            valueLine("Salário Bruto", formatCurrency(result.gross())),
            "├" + line + "┤",
            coloredValueLine(RED, "(-) INSS", formatCurrency(result.inss())),
            coloredValueLine(RED, "(-) IRRF", formatCurrency(result.irrf())),
            "├" + doubleLine + "╡",
            coloredValueLine(GREEN_BOLD, "Salário Líquido", formatCurrency(result.net())),
            "╰" + line + "╯"
        );
    }

    private String titleLine(String color, String label) {
        int trailing = INNER_WIDTH - 2 - label.length();
        return "│  " + color + label + RESET + " ".repeat(trailing) + "│";
    }

    private String valueLine(String label, String value) {
        int spaces = INNER_WIDTH - 4 - label.length() - value.length();
        return "│  " + label + " ".repeat(spaces) + value + "  │";
    }

    private String coloredValueLine(String color, String label, String value) {
        int spaces = INNER_WIDTH - 4 - label.length() - value.length();
        return "│  " + color + label + " ".repeat(spaces) + value + RESET + "  │";
    }

    private String formatCurrency(BigDecimal value) {
        NumberFormat format = NumberFormat.getNumberInstance(Locale.of("pt", "BR"));
        format.setMinimumFractionDigits(2);
        format.setMaximumFractionDigits(2);
        return "R$ " + format.format(value);
    }
}
