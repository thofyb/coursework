package com.example.outlining;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.os.Process;

import com.example.navitaslite.CpuCoreCluster;
import com.example.navitaslite.PowerProfile;
import com.example.navitaslite.MeasurementTool;
import com.example.navitaslite.MeasurementTool.Reading;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    TextView mResultTextView;
    Button mStartTestButton;
    static String TAG = "OUTLINING";

    static int MAX_CYCLES = 10000;
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
        mResultTextView.append(", opt on\nmask = " + MASK + ", cycles = " + MAX_CYCLES +  "\n");

        mStartTestButton = (Button)  findViewById(R.id.b_startTest);
        mStartTestButton.setText("Start " + TEST_NUMBER + " tests");
        mStartTestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float avge = 0;

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

            Log.d(TAG, "run: ====================================================================");
            long startETime, endETime;
            Reading r1, r2;

            String[] arr = new String[20];
            Log.d(TAG, "run: arr.length = " + arr.length);
            Arrays.fill(arr, "empty");
            Log.d(TAG, "run: CURRENT TEST: " + CURR_ATTEMPT);

//            startETime = Process.getElapsedCpuTime();

            r1 = MeasurementTool.makeMeasurement(0x11111111);

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

            r2 = MeasurementTool.makeMeasurement(0x11111111);

            MeasurementTool.Reading diff = MeasurementTool.findDiff(r1, r2);

            MainActivity.TOTAL_ENERGY = MeasurementTool.analyzeMeasurement(diff, PROFILE);

//            endETime = Process.getElapsedCpuTime();
//            MainActivity.TOTAL_ENERGY = endETime - startETime;

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
