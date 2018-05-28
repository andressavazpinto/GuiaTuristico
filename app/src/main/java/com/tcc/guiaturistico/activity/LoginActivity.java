package com.tcc.guiaturistico.activity;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tcc.guiaturistico.R;

import org.json.JSONException;
import org.json.JSONObject;

import model.User;
import model.UserDes;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import service.UserService;
import util.DBHelper;
import util.Status;

/**
 * Created by Andressa on 31/03/2018.
 */

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "Error";
    EditText editTextUserEmail, editTextPassword;
    TextView textForgetPass;
    Button buttonLogin;
    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        dbHelper = new DBHelper(this);

        buttonLogin = findViewById(R.id.buttonLogin);
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
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

    public void login() {
        editTextUserEmail = (EditText) findViewById(R.id.editTextUserEmail);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);

        Gson g = new GsonBuilder().registerTypeAdapter(User.class, new UserDes())
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(UserService.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(g))
                .build();

        UserService service = retrofit.create(UserService.class);

        final User u = new User();

        u.setEmail(editTextUserEmail.getText().toString());
        u.setPassword(editTextPassword.getText().toString());

        Call<User> requestUser = service.login(u);

        requestUser.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                String aux;
                if(!response.isSuccessful()) {
                    aux = "Erro: " + (response.code());
                    Log.i(TAG, aux);
                    Toast.makeText(getApplicationContext(), aux, Toast.LENGTH_LONG).show();
                }
                else {
                    try {
                        JSONObject jsonUser = new JSONObject(new Gson().toJson(response.body()));
                        u.setIdUser(jsonUser.getInt("idUser"));
                        u.setName(jsonUser.getString("name"));
                        u.setDateOfBirth(jsonUser.getString("dateOfBirth"));
                        u.setEmail(jsonUser.getString("email"));
                        u.setPassword(jsonUser.getString("password"));
                        u.setOccupation(jsonUser.getString("occupation"));
                        u.setLanguage(jsonUser.getString("language"));
                        u.setLocalization(jsonUser.getString("localization"));
                        u.setStatusAccount(Enum.valueOf(Status.class, jsonUser.getString("statusAccount")));

                        //FAZER ESSE MESMO PROCESSO APÓS CADASTRO, ALIÁS NO CADASTRO DEVE-SE RETORNAR OS DADOS DO USUÁRIO
                        //registrar os dados do user no bd interno do app
                        dbHelper.insertUser(u);

                        User user = dbHelper.getUser();
                        Log.i("DadosDoCara", user.toString());
                        Toast.makeText(getApplicationContext(), "Login realizado", Toast.LENGTH_LONG).show();
                        openHome();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    System.out.print("Id: " + u.getIdUser());
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                String aux = " Erro: " + t.getMessage();
                Log.e(TAG, aux);
                Toast.makeText(getApplicationContext(), aux, Toast.LENGTH_LONG).show();
            }
        });
    }

    public void openHome(){
        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
        startActivity(intent);
    }
}
