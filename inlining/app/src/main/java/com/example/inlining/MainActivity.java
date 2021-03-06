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
import java.util.ArrayList;
import java.util.List;

import com.example.navitaslite.CpuCoreCluster;
import com.example.navitaslite.MeasurementTool;
import com.example.navitaslite.PowerProfile;

public class MainActivity extends AppCompatActivity {
    TextView mResultTextView;
    Button mStartTestButton;
    static String TAG = "INLINING";

    static int MAX_CYCLES = 1000000;
    int TEST_NUMBER = 15;
    public volatile static float TOTAL_ENERGY = 0;
    public static int CORE = 0;
    public static int MASK = (int) Math.pow(2, CORE);
    public static int CURR_ATTEMPT = 0;
    static int PID = 0;

    public int MIN_FREQ = 400000;
    public int MAX_FREQ = 1500000;

    private static PowerProfile PROFILE = getStandPowerProfile();


    static {
        System.loadLibrary("affinity-lib");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PID = Process.myPid();

        mResultTextView = (TextView) findViewById(R.id.tv_result);
        mResultTextView.setText("PID: " + Process.myPid());
        mResultTextView.append(", opt off\nmask = " + MASK + ", cycles = " + MAX_CYCLES +  "\n");

        mStartTestButton = (Button)  findViewById(R.id.b_startTest);
        mStartTestButton.setText("Start " + TEST_NUMBER + " tests");
        mStartTestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float avge = 0;

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
                mResultTextView.append("W.  ec: " + TOTAL_ENERGY + "\n");


                for (int i = 1; i <= TEST_NUMBER; i++) {
                    CURR_ATTEMPT = i;
                    Thread calc = new Thread(new CalcTask());
                    calc.start();
                    try {
                        calc.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    mResultTextView.append(i + ".  ec: " + TOTAL_ENERGY + "\n");
                    avge += TOTAL_ENERGY;
                }

                mResultTextView.append("Average ec: " + (avge / TEST_NUMBER));

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
            MeasurementTool.Reading r1, r2;

            Log.d(TAG, "run: ====================================================================");
            Log.d(TAG, "run: CURRENT TEST: " + CURR_ATTEMPT);

//            startETime = Process.getElapsedCpuTime();

            r1 = MeasurementTool.makeMeasurement(0x11111111);

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

//            endETime = Process.getElapsedCpuTime();

            r2 = MeasurementTool.makeMeasurement(0x11111111);

            MeasurementTool.Reading diff = MeasurementTool.findDiff(r1, r2);

            MainActivity.TOTAL_ENERGY = MeasurementTool.analyzeMeasurement(diff, PROFILE);

//            Log.d(TAG, "run: elapsed ec:   " + (endETime - startETime));
            Log.d(TAG, "run: elapsed ec:   " + TOTAL_ENERGY);
            Log.d(TAG, "run: elapsed ec:   " + diff.toString());

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

    private static PowerProfile getStandPowerProfile() {
        CpuCoreCluster cluster = new CpuCoreCluster(4);

        cluster.speeds.add(1500000L);
        cluster.speeds.add(1400000L);
        cluster.speeds.add(1300000L);
        cluster.speeds.add(1200000L);
        cluster.speeds.add(1100000L);
        cluster.speeds.add(1000000L);
        cluster.speeds.add( 900000L);
        cluster.speeds.add( 800000L);
        cluster.speeds.add( 700000L);
        cluster.speeds.add( 600000L);
        cluster.speeds.add( 500000L);
        cluster.speeds.add( 400000L);

        cluster.powers.add(141F);
        cluster.powers.add(126F);
        cluster.powers.add(111F);
        cluster.powers.add( 98F);
        cluster.powers.add( 89F);
        cluster.powers.add( 80F);
        cluster.powers.add( 70F);
        cluster.powers.add( 65F);
        cluster.powers.add( 54F);
        cluster.powers.add( 50F);
        cluster.powers.add( 46F);
        cluster.powers.add( 42F);

        List<CpuCoreCluster> tmp = new ArrayList<>();
        tmp.add(cluster);

        return new PowerProfile(tmp);
    }
}
