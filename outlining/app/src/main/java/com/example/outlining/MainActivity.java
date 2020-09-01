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

    static int MAX_CYCLES = 100000;
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
                arr[0] = new StringBuilder("\n1st mill: ")
                        .append(System.currentTimeMillis())
                        .append("\n2nd mill: ")
                        .append(System.currentTimeMillis())
                        .append("\n3nd mill: ")
                        .append(System.currentTimeMillis())
                        .append("\nidk mill: ")
                        .append(System.currentTimeMillis())
                        .append("\nidk mill: ")
                        .append(System.currentTimeMillis())
                        .append("\nidk mill: ")
                        .append(System.currentTimeMillis())
                        .append("\nidk mill: ")
                        .append(System.currentTimeMillis())
                        .append("\nidk mill: ")
                        .append(System.currentTimeMillis())
                        .append("\nidk mill: ")
                        .append(System.currentTimeMillis())
                        .toString();

                arr[1] = new StringBuilder("\n1st mill: ")
                        .append(System.currentTimeMillis())
                        .append("\n2nd mill: ")
                        .append(System.currentTimeMillis())
                        .append("\n3nd mill: ")
                        .append(System.currentTimeMillis())
                        .append("\nidk mill: ")
                        .append(System.currentTimeMillis())
                        .append("\nidk mill: ")
                        .append(System.currentTimeMillis())
                        .append("\nidk mill: ")
                        .append(System.currentTimeMillis())
                        .append("\nidk mill: ")
                        .append(System.currentTimeMillis())
                        .append("\nidk mill: ")
                        .append(System.currentTimeMillis())
                        .append("\nidk mill: ")
                        .append(System.currentTimeMillis())
                        .toString();

                arr[2] = new StringBuilder("\n1st mill: ")
                        .append(System.currentTimeMillis())
                        .append("\n2nd mill: ")
                        .append(System.currentTimeMillis())
                        .append("\n3nd mill: ")
                        .append(System.currentTimeMillis())
                        .append("\nidk mill: ")
                        .append(System.currentTimeMillis())
                        .append("\nidk mill: ")
                        .append(System.currentTimeMillis())
                        .append("\nidk mill: ")
                        .append(System.currentTimeMillis())
                        .append("\nidk mill: ")
                        .append(System.currentTimeMillis())
                        .append("\nidk mill: ")
                        .append(System.currentTimeMillis())
                        .append("\nidk mill: ")
                        .append(System.currentTimeMillis())
                        .toString();

                arr[3] = new StringBuilder("\n1st mill: ")
                        .append(System.currentTimeMillis())
                        .append("\n2nd mill: ")
                        .append(System.currentTimeMillis())
                        .append("\n3nd mill: ")
                        .append(System.currentTimeMillis())
                        .append("\nidk mill: ")
                        .append(System.currentTimeMillis())
                        .append("\nidk mill: ")
                        .append(System.currentTimeMillis())
                        .append("\nidk mill: ")
                        .append(System.currentTimeMillis())
                        .append("\nidk mill: ")
                        .append(System.currentTimeMillis())
                        .append("\nidk mill: ")
                        .append(System.currentTimeMillis())
                        .append("\nidk mill: ")
                        .append(System.currentTimeMillis())
                        .toString();

                arr[4] = new StringBuilder("\n1st mill: ")
                        .append(System.currentTimeMillis())
                        .append("\n2nd mill: ")
                        .append(System.currentTimeMillis())
                        .append("\n3nd mill: ")
                        .append(System.currentTimeMillis())
                        .append("\nidk mill: ")
                        .append(System.currentTimeMillis())
                        .append("\nidk mill: ")
                        .append(System.currentTimeMillis())
                        .append("\nidk mill: ")
                        .append(System.currentTimeMillis())
                        .append("\nidk mill: ")
                        .append(System.currentTimeMillis())
                        .append("\nidk mill: ")
                        .append(System.currentTimeMillis())
                        .append("\nidk mill: ")
                        .append(System.currentTimeMillis())
                        .toString();

                arr[5] = new StringBuilder("\n1st mill: ")
                        .append(System.currentTimeMillis())
                        .append("\n2nd mill: ")
                        .append(System.currentTimeMillis())
                        .append("\n3nd mill: ")
                        .append(System.currentTimeMillis())
                        .append("\nidk mill: ")
                        .append(System.currentTimeMillis())
                        .append("\nidk mill: ")
                        .append(System.currentTimeMillis())
                        .append("\nidk mill: ")
                        .append(System.currentTimeMillis())
                        .append("\nidk mill: ")
                        .append(System.currentTimeMillis())
                        .append("\nidk mill: ")
                        .append(System.currentTimeMillis())
                        .append("\nidk mill: ")
                        .append(System.currentTimeMillis())
                        .toString();

                arr[6] = new StringBuilder("\n1st mill: ")
                        .append(System.currentTimeMillis())
                        .append("\n2nd mill: ")
                        .append(System.currentTimeMillis())
                        .append("\n3nd mill: ")
                        .append(System.currentTimeMillis())
                        .append("\nidk mill: ")
                        .append(System.currentTimeMillis())
                        .append("\nidk mill: ")
                        .append(System.currentTimeMillis())
                        .append("\nidk mill: ")
                        .append(System.currentTimeMillis())
                        .append("\nidk mill: ")
                        .append(System.currentTimeMillis())
                        .append("\nidk mill: ")
                        .append(System.currentTimeMillis())
                        .append("\nidk mill: ")
                        .append(System.currentTimeMillis())
                        .toString();

                arr[7] = new StringBuilder("\n1st mill: ")
                        .append(System.currentTimeMillis())
                        .append("\n2nd mill: ")
                        .append(System.currentTimeMillis())
                        .append("\n3nd mill: ")
                        .append(System.currentTimeMillis())
                        .append("\nidk mill: ")
                        .append(System.currentTimeMillis())
                        .append("\nidk mill: ")
                        .append(System.currentTimeMillis())
                        .append("\nidk mill: ")
                        .append(System.currentTimeMillis())
                        .append("\nidk mill: ")
                        .append(System.currentTimeMillis())
                        .append("\nidk mill: ")
                        .append(System.currentTimeMillis())
                        .append("\nidk mill: ")
                        .append(System.currentTimeMillis())
                        .toString();

                arr[8] = new StringBuilder("\n1st mill: ")
                        .append(System.currentTimeMillis())
                        .append("\n2nd mill: ")
                        .append(System.currentTimeMillis())
                        .append("\n3nd mill: ")
                        .append(System.currentTimeMillis())
                        .append("\nidk mill: ")
                        .append(System.currentTimeMillis())
                        .append("\nidk mill: ")
                        .append(System.currentTimeMillis())
                        .append("\nidk mill: ")
                        .append(System.currentTimeMillis())
                        .append("\nidk mill: ")
                        .append(System.currentTimeMillis())
                        .append("\nidk mill: ")
                        .append(System.currentTimeMillis())
                        .append("\nidk mill: ")
                        .append(System.currentTimeMillis())
                        .toString();

                arr[9] = new StringBuilder("\n1st mill: ")
                        .append(System.currentTimeMillis())
                        .append("\n2nd mill: ")
                        .append(System.currentTimeMillis())
                        .append("\n3nd mill: ")
                        .append(System.currentTimeMillis())
                        .append("\nidk mill: ")
                        .append(System.currentTimeMillis())
                        .append("\nidk mill: ")
                        .append(System.currentTimeMillis())
                        .append("\nidk mill: ")
                        .append(System.currentTimeMillis())
                        .append("\nidk mill: ")
                        .append(System.currentTimeMillis())
                        .append("\nidk mill: ")
                        .append(System.currentTimeMillis())
                        .append("\nidk mill: ")
                        .append(System.currentTimeMillis())
                        .toString();

                arr[10] = new StringBuilder("\n1st mill: ")
                        .append(System.currentTimeMillis())
                        .append("\n2nd mill: ")
                        .append(System.currentTimeMillis())
                        .append("\n3nd mill: ")
                        .append(System.currentTimeMillis())
                        .append("\nidk mill: ")
                        .append(System.currentTimeMillis())
                        .append("\nidk mill: ")
                        .append(System.currentTimeMillis())
                        .append("\nidk mill: ")
                        .append(System.currentTimeMillis())
                        .append("\nidk mill: ")
                        .append(System.currentTimeMillis())
                        .append("\nidk mill: ")
                        .append(System.currentTimeMillis())
                        .append("\nidk mill: ")
                        .append(System.currentTimeMillis())
                        .toString();

                arr[11] = new StringBuilder("\n1st mill: ")
                        .append(System.currentTimeMillis())
                        .append("\n2nd mill: ")
                        .append(System.currentTimeMillis())
                        .append("\n3nd mill: ")
                        .append(System.currentTimeMillis())
                        .append("\nidk mill: ")
                        .append(System.currentTimeMillis())
                        .append("\nidk mill: ")
                        .append(System.currentTimeMillis())
                        .append("\nidk mill: ")
                        .append(System.currentTimeMillis())
                        .append("\nidk mill: ")
                        .append(System.currentTimeMillis())
                        .append("\nidk mill: ")
                        .append(System.currentTimeMillis())
                        .append("\nidk mill: ")
                        .append(System.currentTimeMillis())
                        .toString();

                arr[12] = new StringBuilder("\n1st mill: ")
                        .append(System.currentTimeMillis())
                        .append("\n2nd mill: ")
                        .append(System.currentTimeMillis())
                        .append("\n3nd mill: ")
                        .append(System.currentTimeMillis())
                        .append("\nidk mill: ")
                        .append(System.currentTimeMillis())
                        .append("\nidk mill: ")
                        .append(System.currentTimeMillis())
                        .append("\nidk mill: ")
                        .append(System.currentTimeMillis())
                        .append("\nidk mill: ")
                        .append(System.currentTimeMillis())
                        .append("\nidk mill: ")
                        .append(System.currentTimeMillis())
                        .append("\nidk mill: ")
                        .append(System.currentTimeMillis())
                        .toString();

                arr[13] = new StringBuilder("\n1st mill: ")
                        .append(System.currentTimeMillis())
                        .append("\n2nd mill: ")
                        .append(System.currentTimeMillis())
                        .append("\n3nd mill: ")
                        .append(System.currentTimeMillis())
                        .append("\nidk mill: ")
                        .append(System.currentTimeMillis())
                        .append("\nidk mill: ")
                        .append(System.currentTimeMillis())
                        .append("\nidk mill: ")
                        .append(System.currentTimeMillis())
                        .append("\nidk mill: ")
                        .append(System.currentTimeMillis())
                        .append("\nidk mill: ")
                        .append(System.currentTimeMillis())
                        .append("\nidk mill: ")
                        .append(System.currentTimeMillis())
                        .toString();

                arr[14] = new StringBuilder("\n1st mill: ")
                        .append(System.currentTimeMillis())
                        .append("\n2nd mill: ")
                        .append(System.currentTimeMillis())
                        .append("\n3nd mill: ")
                        .append(System.currentTimeMillis())
                        .append("\nidk mill: ")
                        .append(System.currentTimeMillis())
                        .append("\nidk mill: ")
                        .append(System.currentTimeMillis())
                        .append("\nidk mill: ")
                        .append(System.currentTimeMillis())
                        .append("\nidk mill: ")
                        .append(System.currentTimeMillis())
                        .append("\nidk mill: ")
                        .append(System.currentTimeMillis())
                        .append("\nidk mill: ")
                        .append(System.currentTimeMillis())
                        .toString();

                arr[15] = new StringBuilder("\n1st mill: ")
                        .append(System.currentTimeMillis())
                        .append("\n2nd mill: ")
                        .append(System.currentTimeMillis())
                        .append("\n3nd mill: ")
                        .append(System.currentTimeMillis())
                        .append("\nidk mill: ")
                        .append(System.currentTimeMillis())
                        .append("\nidk mill: ")
                        .append(System.currentTimeMillis())
                        .append("\nidk mill: ")
                        .append(System.currentTimeMillis())
                        .append("\nidk mill: ")
                        .append(System.currentTimeMillis())
                        .append("\nidk mill: ")
                        .append(System.currentTimeMillis())
                        .append("\nidk mill: ")
                        .append(System.currentTimeMillis())
                        .toString();

                arr[16] = new StringBuilder("\n1st mill: ")
                        .append(System.currentTimeMillis())
                        .append("\n2nd mill: ")
                        .append(System.currentTimeMillis())
                        .append("\n3nd mill: ")
                        .append(System.currentTimeMillis())
                        .append("\nidk mill: ")
                        .append(System.currentTimeMillis())
                        .append("\nidk mill: ")
                        .append(System.currentTimeMillis())
                        .append("\nidk mill: ")
                        .append(System.currentTimeMillis())
                        .append("\nidk mill: ")
                        .append(System.currentTimeMillis())
                        .append("\nidk mill: ")
                        .append(System.currentTimeMillis())
                        .append("\nidk mill: ")
                        .append(System.currentTimeMillis())
                        .toString();

                arr[17] = new StringBuilder("\n1st mill: ")
                        .append(System.currentTimeMillis())
                        .append("\n2nd mill: ")
                        .append(System.currentTimeMillis())
                        .append("\n3nd mill: ")
                        .append(System.currentTimeMillis())
                        .append("\nidk mill: ")
                        .append(System.currentTimeMillis())
                        .append("\nidk mill: ")
                        .append(System.currentTimeMillis())
                        .append("\nidk mill: ")
                        .append(System.currentTimeMillis())
                        .append("\nidk mill: ")
                        .append(System.currentTimeMillis())
                        .append("\nidk mill: ")
                        .append(System.currentTimeMillis())
                        .append("\nidk mill: ")
                        .append(System.currentTimeMillis())
                        .toString();

                arr[18] = new StringBuilder("\n1st mill: ")
                        .append(System.currentTimeMillis())
                        .append("\n2nd mill: ")
                        .append(System.currentTimeMillis())
                        .append("\n3nd mill: ")
                        .append(System.currentTimeMillis())
                        .append("\nidk mill: ")
                        .append(System.currentTimeMillis())
                        .append("\nidk mill: ")
                        .append(System.currentTimeMillis())
                        .append("\nidk mill: ")
                        .append(System.currentTimeMillis())
                        .append("\nidk mill: ")
                        .append(System.currentTimeMillis())
                        .append("\nidk mill: ")
                        .append(System.currentTimeMillis())
                        .append("\nidk mill: ")
                        .append(System.currentTimeMillis())
                        .toString();

                arr[19] = new StringBuilder("\n1st mill: ")
                        .append(System.currentTimeMillis())
                        .append("\n2nd mill: ")
                        .append(System.currentTimeMillis())
                        .append("\n3nd mill: ")
                        .append(System.currentTimeMillis())
                        .append("\nidk mill: ")
                        .append(System.currentTimeMillis())
                        .append("\nidk mill: ")
                        .append(System.currentTimeMillis())
                        .append("\nidk mill: ")
                        .append(System.currentTimeMillis())
                        .append("\nidk mill: ")
                        .append(System.currentTimeMillis())
                        .append("\nidk mill: ")
                        .append(System.currentTimeMillis())
                        .append("\nidk mill: ")
                        .append(System.currentTimeMillis())
                        .toString();

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
