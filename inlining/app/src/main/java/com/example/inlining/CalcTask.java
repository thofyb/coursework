package com.example.inlining;

public class CalcTask implements Runnable {
    public int cycles = MainActivity.MAX_CYCLES;

    @Override
    public void run() {
        for (int i = 0; i < cycles; i++) {
            calcFunc();
            calcFunc();
            calcFunc();
            calcFunc();
            calcFunc();
            calcFunc();
            calcFunc();
            calcFunc();
            calcFunc();
            calcFunc();
        }
    }

    private void calcFunc() { System.currentTimeMillis(); }
}