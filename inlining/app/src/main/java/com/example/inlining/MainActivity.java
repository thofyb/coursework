package com.example.inlining;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    TextView mResultTextView;
    Button mStartTestButton;
    static int MAX_CYCLES = 1000000;
    int TEST_NUMBER = 15;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mResultTextView = (TextView) findViewById(R.id.tv_result);
        mResultTextView.append("(outer class, opt on)\n");

        mStartTestButton = (Button)  findViewById(R.id.b_startTest);
        mStartTestButton.setText("Start " + TEST_NUMBER + " tests");
        mStartTestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long avg = 0;
                for (int i = 0; i < TEST_NUMBER; i++) {
                    Thread calc = new Thread(new CalcTask());
                    long startTime, totalTime;
                    startTime = System.currentTimeMillis();
                    calc.start();
                    try {
                        calc.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    totalTime = System.currentTimeMillis();
                    totalTime -= startTime;
                    mResultTextView.append(i + ".  Execution time: " + totalTime + "ms\n");
                    avg += totalTime;
                }
                mResultTextView.append("Average time: " + (avg / TEST_NUMBER));
            }
        });
    }


}
