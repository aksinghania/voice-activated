package com.example.android.globalactionbarservice;



import android.Manifest;
import android.accessibilityservice.AccessibilityService;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.widget.Button;
import android.widget.FrameLayout;

import androidx.core.app.ActivityCompat;

import ai.picovoice.porcupine.Porcupine;
import ai.picovoice.porcupine.PorcupineException;
import ai.picovoice.porcupine.PorcupineManager;

import ai.picovoice.porcupine.PorcupineManagerCallback;

public class GlobalActionBarService extends AccessibilityService {
    FrameLayout mLayout;
    private String accessKey = "/DFR6s3XsMSEfVgw+cIBt5BS19yIW+QgdAAQncG16L0IJWePNjLQUg==";
    private PorcupineManager engineManager;


    @Override
    protected void onServiceConnected() {
        // Create an overlay and display the action bar
        WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        mLayout = new FrameLayout(this);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.type = WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY;
        lp.format = PixelFormat.TRANSLUCENT;
        lp.flags |= WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.TOP;
        LayoutInflater inflater = LayoutInflater.from(this);
        inflater.inflate(R.layout.action_bar, mLayout);
        wm.addView(mLayout, lp);

        Log.i("onserviceconnects",String.valueOf(hasRecordPermission()));


        if (!hasRecordPermission()) {
            Intent intent = new Intent(this, PermissionRequestActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
        Log.i("onServiceConnected", String.valueOf(hasRecordPermission()));



        try {
            engineManager = new PorcupineManager.Builder()
                    .setAccessKey(accessKey)
                    .setKeywordPaths(new String[]{"scan-front_en_android_v3_0_0.ppn"})
                    .build(this, wakeWordCallback);
        } catch (PorcupineException e) {
            throw new RuntimeException(e);
        }
        try {
            Log.i("engine","Starting now");
            configureMainButton();
            engineManager.start();
        } catch (PorcupineException e) {
            throw new RuntimeException(e);
        }

    }

    private void configureMainButton() {
        Button swipeButton = (Button) mLayout.findViewById(R.id.main_button);
        swipeButton.setText("Actively Listening");
    }
    private final PorcupineManagerCallback wakeWordCallback = new PorcupineManagerCallback() {
        @Override
        public void invoke(int keywordIndex) {
            if (keywordIndex == 0) {

                Log.i("WakeWord", "Scan Front Detected");
            } else if (keywordIndex == 1) {

                Log.i("WakeWord", "Bumblebee detected");
            }
        }
    };



    private boolean hasRecordPermission() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                == PackageManager.PERMISSION_GRANTED;
    }
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {

    }

    @Override
    public void onInterrupt() {

    }
    @Override
    public void onDestroy() {
        try {
            engineManager.stop();
        } catch (PorcupineException e) {
            throw new RuntimeException(e);
        }
        if (engineManager != null) {
            engineManager.delete(); // Release Porcupine resources
        }
        super.onDestroy();
    }
}


