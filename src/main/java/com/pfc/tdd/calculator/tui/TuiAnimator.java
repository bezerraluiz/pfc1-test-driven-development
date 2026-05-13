package com.pfc.tdd.calculator.tui;

import java.io.PrintStream;

public class TuiAnimator {

    private static final String[] FRAMES = {"⠋","⠙","⠹","⠸","⠼","⠴","⠦","⠧","⠇","⠏"};

    private final String mensagem;
    private final PrintStream saida;
    private Thread thread;

    public TuiAnimator(String mensagem, PrintStream saida) {
        this.mensagem = mensagem;
        this.saida = saida;
    }

    public void start() {
        thread = new Thread(() -> {
            int index = 0;
            while (!Thread.currentThread().isInterrupted()) {
                saida.print("\r" + FRAMES[index++ % FRAMES.length] + " " + mensagem);
                saida.flush();
                try { Thread.sleep(80); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    public void stop() {
        thread.interrupt();
        try { thread.join(200); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        saida.print("\033[2K\r");
        saida.flush();
    }
}
