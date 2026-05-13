package com.pfc.tdd;

import java.util.Scanner;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.pfc.tdd.calculator.domain.InssTable;
import com.pfc.tdd.calculator.domain.IrrfTable;
import com.pfc.tdd.calculator.domain.SalaryNetCalculator;
import com.pfc.tdd.calculator.tui.InputParser;
import com.pfc.tdd.calculator.tui.TuiApp;

@SpringBootApplication
public class TddApplication {

	public static void main(String[] args) {
		SpringApplication.run(TddApplication.class, args);
		var calculator = new SalaryNetCalculator(new InssTable(), new IrrfTable());
		new TuiApp(calculator, new InputParser()).execute(new Scanner(System.in), System.out);
	}
}
