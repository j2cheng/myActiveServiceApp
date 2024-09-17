package com.example.myactiveserviceapp;

import android.app.Service;
import android.content.Context;

import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import com.droideic.app.DisplaySettingManager;

public class MyMainService extends Service {
    private final static String TAG = "mymainservice";

    public MyMainService() {

        Log.i(TAG, "MyMainService");

        DisplaySettingManager m_dsm = new DisplaySettingManager(getApplicationContext());
        m_dsm.setDisplayDiscardColorEnable(1);

        Log.i(TAG, "MyMainService: m_dsm: " + m_dsm);
    }

    @Override
    public IBinder onBind(Intent intent) {

        Log.i(TAG, "onBind");
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}