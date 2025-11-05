package com.example.mobile_app;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;

import com.example.auth.ui.LoginActivity;

public class MainActivity extends AppCompatActivity {
    private boolean ready = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SplashScreen.installSplashScreen(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            ready = true;
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }, 500);
    }
}