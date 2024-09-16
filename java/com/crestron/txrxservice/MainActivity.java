package com.crestron.txrxservice;

import android.content.Intent;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.View;

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
        startServiceView();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkDrawOverlayPermission();
        }
        else {
            startServiceView();
        }
    }

    public void startServiceView()
    {
        if (serviceStarted) {
            Log.e(TAG, "service is already running");
            return;
        }
        Log.e(TAG, "starting service");
        Intent serviceIntent = new Intent(getApplicationContext(), MainService.class);
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
        Intent serviceIntent = new Intent(getApplicationContext(), MainService.class);
        stopService(serviceIntent);
        serviceStarted = false;
    }
}
