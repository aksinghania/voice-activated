package com.example.android.globalactionbarservice;



import android.Manifest;
import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.widget.FrameLayout;

import androidx.core.app.ActivityCompat;

import ai.picovoice.porcupine.Porcupine;
import ai.picovoice.porcupine.PorcupineException;
import ai.picovoice.porcupine.PorcupineManager;

import ai.picovoice.porcupine.PorcupineManagerCallback;

public class GlobalActionBarService extends AccessibilityService {
    FrameLayout mLayout;
    private String accessKey = "G1WLZ+U9c7mAOjAtXUJq8wEMa5SsZ1L+Ti7C4y/MKz1cIHbZIYoM2A==";
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

        PorcupineManagerCallback wakeWordcallback = new PorcupineManagerCallback() {
            @Override
            public void invoke(int keywordIndex) {
                if (keywordIndex == 0) {
                    // porcupine detected
                } else if (keywordIndex == 1) {
                    // bumblebee detected
                }
            }
        };

        try {
            engineManager = new PorcupineManager.Builder()
                    .setAccessKey(accessKey) // Replace with your access key
                    .setKeywords(new Porcupine.BuiltInKeyword[]{Porcupine.BuiltInKeyword.PORCUPINE, Porcupine.BuiltInKeyword.BUMBLEBEE})
                    .build(this, wakeWordCallback);
        } catch (PorcupineException e) {
            throw new RuntimeException(e);
        }
        try {
            Log.i("engine","Starting now");
            engineManager.start();
        } catch (PorcupineException e) {
            throw new RuntimeException(e);
        }

    }
    private final PorcupineManagerCallback wakeWordCallback = new PorcupineManagerCallback() {
        @Override
        public void invoke(int keywordIndex) {
            if (keywordIndex == 0) {
                // Porcupine detected
                Log.i("WakeWord", "Porcupine detected");
            } else if (keywordIndex == 1) {
                // Bumblebee detected
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
}


