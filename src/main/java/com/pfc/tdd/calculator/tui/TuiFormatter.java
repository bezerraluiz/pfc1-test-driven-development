package com.pfc.tdd.calculator.tui;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

import com.pfc.tdd.calculator.domain.CalculationResult;

public class TuiFormatter {

    private static final int INTERNAL_WIDTH = 38;
    private static final String RESET = "\033[0m";
    private static final String CYAN = "\033[36m";
    private static final String RED = "\033[31m";
    private static final String BOLD_GREEN = "\033[1;32m";

    public String format(CalculationResult result) {
        String gross = formatCurrency(result.gross());
        String inss = formatCurrency(result.inss());
        String irrf = formatCurrency(result.irrf());
        String net = formatCurrency(result.net());
        return String.join("\n",
                border('╭', '─', '╮'),
                line(CYAN, "Resumo do Salário", ""),
                border('├', '─', '┤'),
                line("", "Salário Bruto", gross),
                border('├', '─', '┤'),
                line(RED, "(-) INSS", inss),
                line(RED, "(-) IRRF", irrf),
                border('├', '═', '╡'),
                line(BOLD_GREEN, "Salário Líquido", net),
                border('╰', '─', '╯'));
    }

    private String line(String color, String label, String currency) {
        int padding = INTERNAL_WIDTH - 2 - label.length() - currency.length() - 2;
        String colored = color.isEmpty() ? label : color + label + RESET;
        return "│  " + colored + " ".repeat(padding) + currency + "  │";
    }

    private String border(char left, char fill, char right) {
        return left + String.valueOf(fill).repeat(INTERNAL_WIDTH) + right;
    }

    private String formatCurrency(BigDecimal value) {
        NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.forLanguageTag("pt-BR"));
        numberFormat.setMinimumFractionDigits(2);
        numberFormat.setMaximumFractionDigits(2);
        return "R$ " + numberFormat.format(value);
    }
}
