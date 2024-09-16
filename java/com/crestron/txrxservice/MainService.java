package com.crestron.txrxservice;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.RelativeLayout;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.File;
import com.droideic.app.DisplaySettingManager;

public class MainService extends Service implements SurfaceHolder.Callback, MediaPlayer.OnPreparedListener {
    public final static int REQUEST_CODE = 5463 & 0xffffff00;
    static final String TAG = "txrxservice";
    public final static int TYPE_APPLICATION_CRESTRON_OVERLAY = 2039;
    private RelativeLayout layout;
    private SurfaceView[] videoSurface = new SurfaceView[2];
    private MediaPlayer[] mp = new MediaPlayer[2];
    private SurfaceHolder[] videoSurfaceHolder = new SurfaceHolder[2];
    private static String[] VIDEO_PATH = new String[2];

    @SuppressWarnings("deprecation")
    private void createViews() {
        WindowManager wm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(displayMetrics);
        int windowHeight = displayMetrics.heightPixels;
        int windowWidth = displayMetrics.widthPixels;
        int windowType = 0;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            windowType = TYPE_APPLICATION_CRESTRON_OVERLAY;
        }
        else {
            windowType = WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY;// TYPE_PRIORITY_PHONE;
        }

        WindowManager.LayoutParams wmLayoutParams = new WindowManager.LayoutParams(
                windowWidth,
                windowHeight,
                windowType,
                (0 | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED),
                PixelFormat.TRANSLUCENT);
        wmLayoutParams.gravity = Gravity.TOP | Gravity.LEFT;
        wmLayoutParams.x = 0;
        wmLayoutParams.y = 0;

        wm.addView(layout, wmLayoutParams);
    }

    private void removeViews() {
        WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
            try {
                wm.removeView(layout);
            } catch (Exception e) {}
    }


    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "Service started!");
        layout = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.service_main, null);
        createViews();

        startVideos();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "Service destroyed!");
        stopVideos();
        this.stopSelf();
    }


    public void stopVideos()
    {
        Log.e(TAG, "Inside stopVideos()");
        for (int i = 0; i < mp.length; ++i) {
            if (mp[i] != null) {
                mp[i].release();
            }
            mp[i] = null;
        }
    }

    public void startVideos()
    {
        videoSurface[0] = (SurfaceView) layout.findViewById(R.id.surfaceView1);
        videoSurfaceHolder[0] = videoSurface[0].getHolder();
        videoSurfaceHolder[0].addCallback(this);
        VIDEO_PATH[0] = "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4";

        videoSurface[1] = (SurfaceView) layout.findViewById(R.id.surfaceView2);
        videoSurfaceHolder[1] = videoSurface[1].getHolder();
        videoSurfaceHolder[1].addCallback(this);
        VIDEO_PATH[1] = "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4";

        BufferedReader bufferReader = null;
        BufferedWriter bufferWriter = null;
        File streamUrlFile = null;
        try {
                streamUrlFile = new File(this.getFilesDir(), "streamUrlFile.txt");

                if (!streamUrlFile.exists()) {
                        streamUrlFile.createNewFile();
                        bufferWriter = new BufferedWriter(new FileWriter(streamUrlFile));
                        bufferWriter.write(VIDEO_PATH[0].toString());
                        bufferWriter.newLine();
                        bufferWriter.write(VIDEO_PATH[1].toString());
                        bufferWriter.close();
                }

                bufferReader = new BufferedReader(new FileReader(streamUrlFile));

                String videoStreamURL1 = bufferReader.readLine();

                if (videoStreamURL1 != null && !videoStreamURL1.isEmpty()) {
                        VIDEO_PATH[0] = videoStreamURL1;
                }

                String videoStreamURL2 = bufferReader.readLine();

                if (videoStreamURL2 != null && !videoStreamURL2.isEmpty()) {
                        VIDEO_PATH[1] = videoStreamURL2;
                }
        } catch (IOException e) {
                e.printStackTrace();
        } finally {
                if (bufferWriter != null) {
                        try {
                                bufferWriter.close();
                        } catch (IOException e) {
                                e.printStackTrace();
                        }
                }

                if (bufferReader != null) {
                        try {
                                bufferReader.close();
                        } catch (IOException e) {
                                e.printStackTrace();
                        }
                }
          }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        for (int i = 0; i < mp.length; ++i) {
            if (holder == videoSurfaceHolder[i]) {
                mp[i] = new MediaPlayer();
                mp[i].setDisplay(videoSurfaceHolder[i]);
                try {
                    mp[i].setDataSource(VIDEO_PATH[i]);
                    mp[i].prepare();
                    mp[i].setOnPreparedListener(this);
                    mp[i].setAudioStreamType(AudioManager.STREAM_MUSIC);
                    ((AudioManager) getSystemService(Context.AUDIO_SERVICE)).setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
                    mp[i].setLooping(true);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    @Override
    public void onPrepared(MediaPlayer _mp) {
        for (int i = 0; i < mp.length; ++i) {
            if (_mp == mp[i])
            {
                mp[i].start();
                break;
            }
        }
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
