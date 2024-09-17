package com.example.myactiveserviceapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import android.provider.Settings;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    public final static int REQUEST_CODE = 5463 & 0xffffff00;
    private final static String TAG = "txrxservice";
    private boolean serviceStarted = false;

    public void checkDrawOverlayPermission() {
        /** check if we already  have permission to draw over other apps */
        if (!Settings.canDrawOverlays(this)) {
            /** if not construct intent to request permission */
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            /** request permission via start activity for result */
            startActivityForResult(intent, REQUEST_CODE);
        }
        else {
            startServiceView();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,  Intent data) {
        /** check if received result code
         is equal our requested code for draw permission  */
        if (requestCode == REQUEST_CODE) {
            /* if so check once again if we have permission */
            if (Settings.canDrawOverlays(this)) {
                Log.e(TAG, "Can draw overlays");
            }
        }
//        startServiceView();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkDrawOverlayPermission();
        }
//        else {
//            startServiceView();
//        }

        Button startSrvButton = (Button) findViewById(R.id.button5);
        startSrvButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                Log.e(TAG, "Service start button clicked");
                startServiceView();
                Log.e(TAG, "startServiceView called");
            }
        });

        Button stopSrvButton = (Button) findViewById(R.id.button6);
        stopSrvButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                Log.e(TAG, "Service stop button clicked");
                stopServiceView();
                Log.e(TAG, "stopServiceView called");
            }
        });

    }

    public void startServiceView()
    {
        if (serviceStarted) {
            Log.e(TAG, "service is already running");
            return;
        }
        Log.e(TAG, "starting service");
        Intent serviceIntent = new Intent(getApplicationContext(), MyMainService.class);
        startService(serviceIntent);
        serviceStarted = true;
    }

    public void stopServiceView()
    {
        if (!serviceStarted) {
            Log.e(TAG, "service is not running");
            return;
        }
        Log.e(TAG, "stopping service");
        Intent serviceIntent = new Intent(getApplicationContext(), MyMainService.class);
        stopService(serviceIntent);
        serviceStarted = false;
    }
}