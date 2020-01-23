package com.mirvahidagha.betterbet.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;

import com.mirvahidagha.betterbet.R;
import com.mirvahidagha.betterbet.Others.SecretTextView;

public class SplashActivity extends AppCompatActivity {

    SecretTextView splashBet;
    SecretTextView splashBetter;
    SharedPreferences preferences;
    int delayTime;
    boolean isFirstTime;

    @SuppressLint("InlinedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        preferences = getSharedPreferences("splashSettings", MODE_PRIVATE);
        isFirstTime = preferences.getBoolean("first_time", true);
        delayTime = isFirstTime ? 3500 : 0;

        View mContentView = findViewById(R.id.fullscreen_content);
        splashBetter = (SecretTextView) findViewById(R.id.splash_better);
        splashBet = (SecretTextView) findViewById(R.id.splash_bet);
        splashBetter.setDuration(delayTime);
        splashBet.setDuration(delayTime);
        splashBetter.show();
        splashBet.show();

        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        Handler handler = new Handler();
        final Intent intent = new Intent(
                SplashActivity.this,
                isFirstTime ? SliderActivity.class : Main.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

        Runnable run = new Runnable() {
            @Override
            public void run() {
                startActivity(intent);
            }
        };


        handler.removeCallbacks(run);
        handler.postDelayed(run, delayTime + 500);
    }

}
