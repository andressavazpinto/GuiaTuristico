package com.tcc.guiaturistico.activity;

/**
 * Created by Andressa on 30/03/2018.
 */

import android.arch.lifecycle.ViewModelProvider;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.tcc.guiaturistico.R;

import model.User;
import util.DBController;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        setupComponents();

    }

    public void setupComponents() {
        Button buttonSignIn = findViewById(R.id.buttonSignIn);
        Button buttonSignUp = findViewById(R.id.buttonSignUp);

        buttonSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openLogin();
            }
        });
        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openRegister();
            }
        });
    }

    public void openLogin(){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    public void openRegister(){
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }
}
