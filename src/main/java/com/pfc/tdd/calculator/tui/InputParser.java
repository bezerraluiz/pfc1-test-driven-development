package com.pfc.tdd.calculator.tui;

import java.math.BigDecimal;

import com.pfc.tdd.calculator.domain.GrossSalary;

public class InputParser {

    public GrossSalary parse(String text) {
        try {
            return new GrossSalary(new BigDecimal(text.replace(",", ".")));
        } catch (NumberFormatException e) {
            throw new InvalidInputException("Entrada inválida. Digite um número válido, como 3000 ou 3000,50.");
        } catch (IllegalArgumentException e) {
            throw new InvalidInputException("O salário deve ser maior que zero.");
        }
    }
}
