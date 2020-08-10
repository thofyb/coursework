package com.example.inlining;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    TextView mResultTextView;
    Button mStartTestButton;
    int MAX_CYCLES = 1000000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mResultTextView = (TextView) findViewById(R.id.tv_result);

        mStartTestButton = (Button)  findViewById(R.id.b_startTest);
        mStartTestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Thread calc = new Thread(new CalcTask());
                long startTime, endTime;
                startTime = System.currentTimeMillis();
                calc.start();
                try {
                    calc.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                endTime = System.currentTimeMillis();
                mResultTextView.append("Execution time: " + (endTime - startTime) + "ms\n");
            }
        });

//        new CalcTask().execute(100000);

//        mResultTextView.setText("");
//        mResultTextView.append("Execution time: " + totalTime / 1000 + " s");
    }

    private class CalcTask implements Runnable {
        @Override
        public void run() {
            for (int i = 0; i < MAX_CYCLES; i++) {
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
//                            Log.i("com.example.inlining", String.valueOf(i));
//                            publishProgress(i);
            }
        }

        private void calcFunc() {
            System.currentTimeMillis();
        }
    }

//    private void calcFunc() {
//        System.currentTimeMillis();
//    }

//    private class CalcTask extends AsyncTask<Integer, Integer, Long> {
//        int MAX_CYCLES = 1000;
//
//        @Override
//        protected Long doInBackground(Integer... params) {
//            MAX_CYCLES = params[0];
//            long startTime = System.currentTimeMillis();
//
//            for (int i = 0; i < MAX_CYCLES; i++) {
//                calcFunc();
//                calcFunc();
//                calcFunc();
//                calcFunc();
//                calcFunc();
//                calcFunc();
//                calcFunc();
//                calcFunc();
//                calcFunc();
//                calcFunc();
//                //Log.i("com.example.inlining", String.valueOf(i));
//                publishProgress(i);
//            }
//
//            long endTime = System.currentTimeMillis();
//
//            return endTime - startTime;
//        }
//
//        @Override
//        protected void onPostExecute(Long aLong) {
//            mResultTextView.setText("Execution time: " + aLong + "ms");
//        }
//
//        @Override
//        protected void onProgressUpdate(Integer... values) {
//            mResultTextView.setText("Curr counter: " + values[0] + " of " + MAX_CYCLES);
//        }
//
//        private void calcFunc() {
//            System.currentTimeMillis();
//        }
//    }


}
