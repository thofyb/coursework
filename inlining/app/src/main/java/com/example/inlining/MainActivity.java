package com.example.inlining;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.os.Process;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {
    TextView mResultTextView;
    Button mStartTestButton;
    static String TAG = "INLINING";

    static int MAX_CYCLES = 1000000;
    int TEST_NUMBER = 15;
    public volatile static long TOTAL_ETIME = 0;
    public static int CORE = 0;
    public static int MASK = (int) Math.pow(2, CORE);
    public static int CURR_ATTEMPT = 0;
    static int PID = 0;

    public int MIN_FREQ = 400000;
    public int MAX_FREQ = 1500000;


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
        mResultTextView.append("(nested class, opt off, mask = " + MASK + ")\n");

        mStartTestButton = (Button)  findViewById(R.id.b_startTest);
        mStartTestButton.setText("Start " + TEST_NUMBER + " tests");
        mStartTestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long avge = 0;

                //Getting min and max freq for future changing

                //MIN_FREQ = Integer.parseInt(execCMD("cat /sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_min_freq"));
                //MAX_FREQ = Integer.parseInt(execCMD("cat /sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq"));

                executeCMDWrite("echo " + MAX_FREQ + " > /sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq");

                Thread warmup;
                for (int i = 0; i < 3; i++) {
                    warmup = new Thread((new CalcTask()));
                    warmup.start();
                    try {
                        warmup.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
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
                    avge += TOTAL_ETIME;
                }
                mResultTextView.append("Average time: " + (avge / TEST_NUMBER));

                executeCMDWrite("echo " + MIN_FREQ + " > /sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq");
            }
        });
    }

    private static class CalcTask implements Runnable {
        public int cycles = MAX_CYCLES;

        @Override
        public void run() {
            setAffinity(MASK);
            long startETime, endETime;

            Log.d(TAG, "run: ====================================================================");
            Log.d(TAG, "run: CURRENT TEST: " + CURR_ATTEMPT);

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

            endETime = Process.getElapsedCpuTime();

            MainActivity.TOTAL_ETIME = endETime - startETime;

            Log.d(TAG, "run: elapsed time:   " + TOTAL_ETIME);
        }

        private void calcFunc() { System.currentTimeMillis(); }

        public native void setAffinity(int arg);
    }

    private static String execCMD(String cmd) {
        try {
            java.lang.Process process = Runtime.getRuntime().exec(cmd);

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            int read;
            char[] buffer = new char[4096];
            StringBuilder output = new StringBuilder();
            while ((read = reader.read(buffer)) > 0) {
                output.append(buffer, 0, read);
            }
            reader.close();

            process.waitFor();

            return output.toString();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void executeCMDWrite(String cmd){
        try {
            java.lang.Process p = Runtime.getRuntime().exec("su");
            DataOutputStream dos = new DataOutputStream(p.getOutputStream());
            DataInputStream is = new DataInputStream(p.getInputStream());
            dos.writeBytes(cmd + "\nexit\n");
            dos.flush();
            dos.close();
            p.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }


    private static class ExecutionHandler {

    }
}
