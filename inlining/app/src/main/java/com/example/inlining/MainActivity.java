package com.example.inlining;

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
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    TextView mResultTextView;
    Button mStartTestButton;
    Button check;
    static String TAG = "INLINING";

    static int MAX_CYCLES = 1000000;
    int TEST_NUMBER = 15;
    public volatile static long TOTAL_TIME = 0;
    public volatile static long TOTAL_ETIME = 0;
    public static int MASK = 0b1;
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
                mResultTextView.append("W.  Execution time: " + TOTAL_TIME + "ms, " + TOTAL_ETIME + "\n");

                for (int i = 1; i <= TEST_NUMBER; i++) {
                    CURR_ATTEMPT = i;
                    Thread calc = new Thread(new CalcTask());
                    calc.start();
                    try {
                        calc.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    mResultTextView.append(i + ".  Execution time: " + TOTAL_TIME + "ms, " + TOTAL_ETIME + "\n");
                    avg += TOTAL_TIME;
                    avge += TOTAL_ETIME;
                }
                mResultTextView.append("Average time: " + (avg / TEST_NUMBER) + ", " + (avge / TEST_NUMBER));
            }
        });

        check = findViewById(R.id.check);
        check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mResultTextView.setText("");
                mResultTextView.append("PID: " + PID + "\n");
                String str = "cat /proc/" + PID + "/stat";
                mResultTextView.append(str + "\n");
                mResultTextView.append(execCMD(str));

            }
        });
    }

    private static class CalcTask implements Runnable {
        public int cycles = MAX_CYCLES;
        String cmd = "cat /sys/devices/system/cpu/cpu" + CORE + "/cpufreq/stats/time_in_state";
        static Runtime runtime = Runtime.getRuntime();

        @Override
        public void run() {
            setAffinity(MASK);
            int pid = PID;
            long startTime, endTime, startETime, endETime, startPTime, endPTime;
            Log.d(TAG, "run: ====================================================================");
            Log.d(TAG, "run: CURRENT TEST: " + CURR_ATTEMPT);
            List<Pair<Integer, Integer>> stats1, stats2;
            stats1 = getStats(execCMD(cmd));
            startTime = System.currentTimeMillis();
            startETime = Process.getElapsedCpuTime();
//            startPTime = getTime(execCMD("cat /proc/" + pid + "/stat"));
            Log.d(TAG, "run: " + execCMD("cat /proc" + pid + "/stat"));
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
//            endPTime = getTime(execCMD("cat /proc/" + pid + "/stat"));
            stats2 = getStats(execCMD(cmd));
            List<Pair<Integer, Integer>> delta = getDelta(stats1, stats2);
            Log.d(TAG, "run: Delta: \n" + getDataString(delta));

            MainActivity.TOTAL_TIME = endTime - startTime;
            MainActivity.TOTAL_ETIME = endETime - startETime;

            Log.d(TAG, "run: total time:     " + TOTAL_TIME);
            Log.d(TAG, "run: elapsed time:   " + TOTAL_ETIME);
//            Log.d(TAG, "run: /proc/pid/stat: " + (endPTime - startPTime));
        }

        private void calcFunc() { System.currentTimeMillis(); }

        public native void testJNI();

        public native void checkAffinity();

        public native void setAffinity(int arg);

        private int getTime(String arg) {
            String[] tmp = arg.split(" ");
            return (Integer.parseInt(tmp[13]) + Integer.parseInt(tmp[14]));
        }

        private List<Pair<Integer, Integer>> getStats(String arg) {
            List<Pair<Integer, Integer>> res = new ArrayList<>();
            String[] tmp = arg.split("\n");

            for (String s : tmp) {
                String[] str = s.split(" ");
                res.add(new Pair<>(Integer.parseInt(str[0]), Integer.parseInt(str[1])));
            }

            return res;
        }

        private List<Pair<Integer, Integer>> getDelta(List<Pair<Integer, Integer>> data1, List<Pair<Integer, Integer>> data2) {
            List<Pair<Integer, Integer>> res = new ArrayList<>();

            for (int i = 0; i < data1.size(); i++) {
                res.add(new Pair<>(data1.get(i).first, (data2.get(i).second - data1.get(i).second)));
            }

            return res;
        }

        private String getDataString(List<Pair<Integer, Integer>> data) {
            StringBuilder res = new StringBuilder();
            for (Pair<Integer, Integer> pair : data) {
                res.append(pair.first).append(" ").append(pair.second).append("\n");
            }

            return res.toString();
        }

        private static String execCMD(String cmd) {
            try {
                java.lang.Process process = runtime.exec(cmd);

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
}
