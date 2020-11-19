package com.example.sco;


import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Process;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.navitaslite.CpuCoreCluster;
import com.example.navitaslite.MeasurementTool;
import com.example.navitaslite.PowerProfile;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    TextView mResultTextView;
    Button mStartTestButton;
    static String TAG = "EOANSCO";

    static int MAX_CYCLES = 1000000;
    int TEST_NUMBER = 15;
    public volatile static float TOTAL_ENERGY = 0;
    public static int CORE = 0;
    public static int MASK = (int) Math.pow(2, CORE);
    public static int CURR_ATTEMPT = 0;
    static int PID = 0;

    static int isOptimized = 1 ;

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

        mResultTextView.setText("");
        //mResultTextView.setText("PID: " + Process.myPid() + "\n");
        mResultTextView.append("opt " + (isOptimized == 0 ? "off" : "on") +"\n");
        mResultTextView.append("mask = " + MASK + "\n");
        mResultTextView.append("cycles = " + MAX_CYCLES +  "\n");


        mStartTestButton = (Button)  findViewById(R.id.b_startTest);
        mStartTestButton.setText("Start " + TEST_NUMBER + " tests");mStartTestButton.setOnClickListener(new View.OnClickListener() {
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
            MeasurementTool.Reading r1, r2;

            Log.d(TAG, "run: ====================================================================");
            Log.d(TAG, "run: CURRENT TEST: " + CURR_ATTEMPT);

            r1 = MeasurementTool.makeMeasurement(0x11111111);

            for (int i = 0; i < cycles; i++) {

            }

            r2 = MeasurementTool.makeMeasurement(0x11111111);

            MeasurementTool.Reading diff = MeasurementTool.findDiff(r1, r2);

            MainActivity.TOTAL_ENERGY = MeasurementTool.analyzeMeasurement(diff, PROFILE);

            Log.d(TAG, "run: elapsed ec:   " + TOTAL_ENERGY);
            Log.d(TAG, "run: elapsed ec:   " + diff.toString());

        }

        public native void setAffinity(int arg);
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