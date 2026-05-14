package com.pfc.tdd;

import com.pfc.tdd.calculator.domain.InssTable;
import com.pfc.tdd.calculator.domain.IrrfTable;
import com.pfc.tdd.calculator.domain.SalaryNetCalculator;
import com.pfc.tdd.calculator.tui.InputParser;
import com.pfc.tdd.calculator.tui.TuiApp;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.util.Scanner;

@SpringBootApplication
public class TddApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(TddApplication.class, args);
    }

    @Override
    public void run(String... args) {
        var calculator = new SalaryNetCalculator(new InssTable(), new IrrfTable());
        new TuiApp(calculator, new InputParser()).execute(new Scanner(System.in), System.out);
    }
}
