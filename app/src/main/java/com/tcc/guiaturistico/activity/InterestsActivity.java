package com.tcc.guiaturistico.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

import com.tcc.guiaturistico.R;

import util.DBHelper;

/**
 * Created by Andressa on 27/05/2018.
 */

public class InterestsActivity extends AppCompatActivity {

    CheckBox checkBoxArt, checkBoxCulinaria, checkBoxCultura, checkBoxSport, checkBoxStyle, checkBoxLanguage;
    Button buttonContinue;
    DBHelper dbHelper;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interests);

        dbHelper = new DBHelper(this);

        checkBoxArt = findViewById(R.id.checkBoxArt);
        checkBoxCulinaria = findViewById(R.id.checkBoxCooking);
        checkBoxCultura = findViewById(R.id.checkBoxCulture); 
        checkBoxSport = findViewById(R.id.checkBoxSport);
        checkBoxStyle = findViewById(R.id.checkBoxStyle);
        checkBoxLanguage = findViewById(R.id.checkBoxLanguage);
        
        buttonContinue = findViewById(R.id.buttonContinue);
        buttonContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerInterests();
            }
        });
    }

    private void registerInterests() {
    }
}
