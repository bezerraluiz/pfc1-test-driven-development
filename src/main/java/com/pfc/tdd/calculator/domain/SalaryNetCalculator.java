package com.pfc.tdd.calculator.domain;

import java.math.BigDecimal;

public class SalaryNetCalculator {

    private final InssTable inssTable;
    private final IrrfTable irrfTable;

    public SalaryNetCalculator(InssTable inssTable, IrrfTable irrfTable) {
        this.inssTable = inssTable;
        this.irrfTable = irrfTable;
    }

    public CalculationResult calculate(GrossSalary grossSalary) {
        BigDecimal inss = inssTable.calculate(grossSalary.value());
        BigDecimal taxableBase = grossSalary.value().subtract(inss);
        BigDecimal irrf = irrfTable.calculate(taxableBase);
        BigDecimal net = grossSalary.value().subtract(inss).subtract(irrf);
        return new CalculationResult(grossSalary.value(), inss, irrf, net);
    }
}
