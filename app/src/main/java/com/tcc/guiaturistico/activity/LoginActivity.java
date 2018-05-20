package com.tcc.guiaturistico.activity;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.tcc.guiaturistico.R;

/**
 * Created by Andressa on 31/03/2018.
 */

public class LoginActivity extends AppCompatActivity {

    EditText editTextUserEmail, editTextPassword;
    TextView textForgetPass;
    Button buttonLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        buttonLogin = findViewById(R.id.buttonLogin);
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                efetuarLogin();
            }
        });

        textForgetPass =  findViewById(R.id.textForgotPass);
        textForgetPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment d = new DialogForgotPassActivity();
                d.show(getFragmentManager(), "esqueceu");
            }
        });
    }

    public void efetuarLogin() {
        editTextUserEmail = (EditText) findViewById(R.id.editTextUserEmail);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);


    }
}
