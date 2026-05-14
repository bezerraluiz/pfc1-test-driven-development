package com.pfc.tdd.calculator.domain;

public class SalaryNetCalculator {

    private final InssTable inssTable;
    private final IrrfTable irrfTable;

    public SalaryNetCalculator(InssTable inssTable, IrrfTable irrfTable) {
        this.inssTable = inssTable;
        this.irrfTable = irrfTable;
    }

    public CalculationResult calculate(GrossSalary grossSalary) {
        var inss = inssTable.calculate(grossSalary.value());
        var taxableBase = grossSalary.value().subtract(inss);
        var irrf = irrfTable.calculate(taxableBase);
        var net = grossSalary.value().subtract(inss).subtract(irrf);
        return new CalculationResult(grossSalary.value(), inss, irrf, net);
    }
}
