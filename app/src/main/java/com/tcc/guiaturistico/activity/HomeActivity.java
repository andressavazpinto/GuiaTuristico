package com.tcc.guiaturistico.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.tcc.guiaturistico.R;

/**
 * Created by Andressa on 27/05/2018.
 */

public class HomeActivity extends AppCompatActivity {

    public Button buttonRamdom, buttonByRegion;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        buttonByRegion = findViewById(R.id.buttonByRegion);
        buttonRamdom = findViewById(R.id.buttonRamdom);

        buttonRamdom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchRamdomly();
            }
        });
        
        buttonByRegion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchByRegion();
            }
        });
    }

    private void searchByRegion() {
    }

    private void searchRamdomly() {
    }
}
