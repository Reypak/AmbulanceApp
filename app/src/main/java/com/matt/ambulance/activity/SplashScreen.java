package com.matt.ambulance.activity;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import com.matt.ambulance.R;
import com.matt.ambulance.util.Util;

public class SplashScreen extends AppCompatActivity {
    private final int SPLASH_DISPLAY_LENGTH = 1000;

    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        mHandler.postDelayed(() -> {
            Util.Loader(Navigation.class, this);
            finish();
        }, SPLASH_DISPLAY_LENGTH);

    }

    @Override
    public void onDestroy() {
        // 7. Remove any delayed Runnable(s) and prevent them from executing.
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
        // 8. Eagerly clear mHandler allocated memory
        mHandler = null;
    }
}