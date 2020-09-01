package com.example.outlining;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.os.Process;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    TextView mResultTextView;
    Button mStartTestButton;
    static String TAG = "OUTLINING";

    static int MAX_CYCLES = 1000000;
    int TEST_NUMBER = 15;
    public volatile static long TOTAL_TIME = 0;
    public volatile static long TOTAL_ETIME = 0;
    public static int MASK = 0b10000;
    public static int CORE = 1;
    public static int CURR_ATTEMPT = 0;
    static int PID = 0;


    static {
        System.loadLibrary("affinity-lib");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PID = Process.myPid();

        mResultTextView = (TextView) findViewById(R.id.tv_result);
        mResultTextView.setText("PID: " + Process.myPid() + "\n");
        mResultTextView.append("(nested class, opt off, mask = " + MASK + "), cycles = " + MAX_CYCLES + "\n");

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
                mResultTextView.append("W.  Execution time: " + TOTAL_ETIME + "ms\n");

                for (int i = 1; i <= TEST_NUMBER; i++) {
                    CURR_ATTEMPT = i;
                    Thread calc = new Thread(new CalcTask());
                    calc.start();
                    try {
                        calc.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    mResultTextView.append(i + ".  Execution time: " + TOTAL_ETIME + "ms\n");
                    avg += TOTAL_TIME;
                    avge += TOTAL_ETIME;
                }
                mResultTextView.append("Average time: " + (avge / TEST_NUMBER));
            }
        });
    }

    private static class CalcTask implements Runnable {
        public int cycles = MAX_CYCLES;

        @Override
        public void run() {
            setAffinity(MASK);

            Log.d(TAG, "run: ====================================================================");
            long startETime, endETime;

            String[] arr = new String[20];

            Log.d(TAG, "run: arr.length = " + arr.length);

            Arrays.fill(arr, "empty");

//            for (int i = 0; i < arr.length; i++) {
//                Log.d(TAG, "run: arr[" + i + "]: " + arr[i]);
//            }


            Log.d(TAG, "run: CURRENT TEST: " + CURR_ATTEMPT);

            startETime = Process.getElapsedCpuTime();

            for (int i = 0; i < cycles; i++) {
                arr[0] = "value:" + i;

                arr[1] = "value:" + i;

                arr[2] = "value:" + i;

                arr[3] = "value:" + i;

                arr[4] = "value:" + i;

                arr[5] = "value:" + i;

                arr[6] = "value:" + i;

                arr[7] = "value:" + i;

                arr[8] = "value:" + i;

                arr[9] = "value:" + i;

                arr[10] = "value:" + i;

                arr[11] = "value:" + i;

                arr[12] = "value:" + i;

                arr[13] = "value:" + i;

                arr[14] = "value:" + i;

                arr[15] = "value:" + i;

                arr[16] = "value:" + i;

                arr[17] = "value:" + i;

                arr[18] = "value:" + i;

                arr[19] = "value:" + i;

            }

            endETime = Process.getElapsedCpuTime();

            MainActivity.TOTAL_ETIME = endETime - startETime;

//            for (int i = 0; i < arr.length; i++) {
//                Log.d(TAG, "run: arr[" + i + "]: " + arr[i]);
//            }

            Log.d(TAG, "run: elapsed time:   " + TOTAL_ETIME);
        }

        public native void setAffinity(int arg);
    }
}
