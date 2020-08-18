package com.example.inlining;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.os.Process;

public class MainActivity extends AppCompatActivity {
    TextView mResultTextView;
    Button mStartTestButton;
    static int MAX_CYCLES = 1000000;
    int TEST_NUMBER = 15;
    public volatile static long TOTAL_TIME = 0;
    public volatile static long TOTAL_ETIME = 0;
    public static int MASK = 0b10000;


    static {
        System.loadLibrary("affinity-lib");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mResultTextView = (TextView) findViewById(R.id.tv_result);
        mResultTextView.append("(nested class, opt on, mask = " + MASK + ")\n");

        mStartTestButton = (Button)  findViewById(R.id.b_startTest);
        mStartTestButton.setText("Start " + TEST_NUMBER + " tests");
        mStartTestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long avg = 0;
                long avge = 0;
                Thread warmup = new Thread((new CalcTask()));
                warmup.start();
                try {
                    warmup.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mResultTextView.append("W.  Execution time: " + TOTAL_TIME + "ms, " + TOTAL_ETIME + "\n");

                for (int i = 1; i <= TEST_NUMBER; i++) {
                    Thread calc = new Thread(new CalcTask());
//                    long startTime, totalTime;
//                    startTime = System.currentTimeMillis();
                    calc.start();
                    try {
                        calc.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
//                    totalTime = System.currentTimeMillis();
//                    totalTime -= startTime;
                    mResultTextView.append(i + ".  Execution time: " + TOTAL_TIME + "ms, " + TOTAL_ETIME + "\n");
                    avg += TOTAL_TIME;
                    avge += TOTAL_ETIME;
                }
                mResultTextView.append("Average time: " + (avg / TEST_NUMBER) + ", " + (avge / TEST_NUMBER));
            }
        });
    }

    private static class CalcTask implements Runnable {
        public int cycles = MAX_CYCLES;
        @Override
        public void run() {
//            Log.d("INLINING_JNI", "ready to launch");
//            testJNI();
            checkAffinity();
            setAffinity(MASK);
//            checkAffinity();
//            Log.d("INLINING_JNI", "------------------------------------------------------------------------------------");
            long startTime, endTime, startETime, endETime;
            startTime = System.currentTimeMillis();
            startETime = Process.getElapsedCpuTime();
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
            endTime = System.currentTimeMillis();
            endETime = Process.getElapsedCpuTime();
            MainActivity.TOTAL_TIME = endTime - startTime;
            MainActivity.TOTAL_ETIME = endETime - startETime;
        }

        private void calcFunc() { System.currentTimeMillis(); }

        public native void testJNI();

        public native void checkAffinity();

        public native void setAffinity(int arg);
    }
}
