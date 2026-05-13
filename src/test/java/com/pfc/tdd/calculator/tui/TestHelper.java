package com.pfc.tdd.calculator.tui;

public class TestHelper {
    public static String stripAnsi(String text) {
        return text.replaceAll("\\033\\[[^m]*m", "");
    }
}
