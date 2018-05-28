package com.tcc.guiaturistico.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.tcc.guiaturistico.R;

/**
 * Created by Andressa on 27/05/2018.
 */

public class SplashActivity extends Activity implements Runnable{
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Handler splashScreen = new Handler();
        splashScreen.postDelayed(SplashActivity.this, 2500);
    }

    public void run() {
        startActivity(new Intent(SplashActivity.this, MainActivity.class));
        finish(); //isso aqui mata o splash, e não deixa voltar para essa tela
    }
}
