package com.jherrera.incidencias;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                SharedPreferences preferences = getSharedPreferences("preferenceSession", Context.MODE_PRIVATE);
                boolean session = preferences.getBoolean("session", false);
                if (session) {
                    Intent intent  = new Intent(SplashScreenActivity.this, HomeActivity.class);
                    startActivity(intent);
                    finish();
                }else {
                    Intent intent  = new Intent(SplashScreenActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        }, 2000);
    }
}