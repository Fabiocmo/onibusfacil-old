package com.motirolabs.onibusfacil;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class SplashActivity extends Activity implements Runnable {

    private static int SPLASH_TIME_OUT = 1000;

    public void onCreate(Bundle paramBundle) {

        super.onCreate(paramBundle);

        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(this, SPLASH_TIME_OUT);

    }

    public void run() {

        startActivity(new Intent(this, MainActivity.class));

        finish();

    }

}
