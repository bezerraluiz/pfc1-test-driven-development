package com.pfc.tdd.calculator.tui;

import com.pfc.tdd.calculator.domain.GrossSalary;
import java.math.BigDecimal;

public class InputParser {

    private static final String INVALID_INPUT_MESSAGE = "Entrada inválida. Digite um número válido, como 3000 ou 3000,50.";
    private static final String INVALID_SALARY_MESSAGE = "O salário deve ser maior que zero.";

    public GrossSalary parse(String text) {
        if (text == null || text.isBlank()) {
            throw new InvalidInputException(INVALID_INPUT_MESSAGE);
        }

        BigDecimal value;
        try {
            value = new BigDecimal(text.replace(",", "."));
        } catch (NumberFormatException e) {
            throw new InvalidInputException(INVALID_INPUT_MESSAGE);
        }

        try {
            return new GrossSalary(value);
        } catch (IllegalArgumentException e) {
            throw new InvalidInputException(INVALID_SALARY_MESSAGE);
        }
    }
}
