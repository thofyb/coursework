package com.example.vlclauncher;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    TextView mResultTextView;
    Button mStartTestButton;
    String TAG = "VlcLauncher";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mResultTextView = (TextView) findViewById(R.id.tv_result);

        mStartTestButton = (Button)  findViewById(R.id.b_startTest);
        mStartTestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int vlcRequestCode = -1;
                File file = new File("/storage/emulated/0/video.mp4");
                Uri uri = FileProvider.getUriForFile(MainActivity.this, BuildConfig.APPLICATION_ID + ".provider", file);
                //Uri uri = Uri.parse("file:///storage/emulated/0/video.mp4"); //your file URI
                Intent vlcIntent = new Intent(Intent.ACTION_VIEW);
                vlcIntent.setPackage("org.videolan.vlc");
                vlcIntent.setDataAndTypeAndNormalize(uri, "video/*");
                vlcIntent.putExtra("from_start", true);
                vlcIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                Log.d(TAG, "onClick: start video");
                startActivityForResult(vlcIntent, vlcRequestCode);
                Log.d(TAG, "onClick: end video");
                mResultTextView.setText("done");

            }
        });
    }
}