package com.pfc.tdd.calculator.tui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TuiAnimatorTest {

    private ByteArrayOutputStream baos;
    private TuiAnimator animator;

    @BeforeEach
    void setUp() {
        baos = new ByteArrayOutputStream();
        animator = new TuiAnimator("Calculando...", new PrintStream(baos));
    }

    @Test
    void shouldDisplayMessageWhileRunning() throws InterruptedException {
        animator.start();
        Thread.sleep(200);
        animator.stop();

        assertTrue(baos.toString().contains("Calculando..."));
    }

    @Test
    void shouldClearLineOnStop() throws InterruptedException {
        animator.start();
        Thread.sleep(200);
        animator.stop();

        assertTrue(baos.toString().contains("\033[2K"));
    }

    @Test
    void shouldNotGenerateOutputAfterStop() throws InterruptedException {
        animator.start();
        Thread.sleep(200);
        animator.stop();

        int lengthAfterStop = baos.toString().length();
        Thread.sleep(200);

        assertEquals(lengthAfterStop, baos.toString().length());
    }
}
