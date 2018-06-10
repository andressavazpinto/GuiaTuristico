package com.tcc.guiaturistico.activity;

import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tcc.guiaturistico.R;

import org.json.JSONException;
import org.json.JSONObject;

import model.DeserializedUser;
import model.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import service.UserService;
import util.DBController;
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
    DBController crud;
    User u;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //crud = new DBController(getBaseContext());
        try {
            crud = new DBController(this);
        } catch (Exception e) {
            System.out.println("não criou o banco");
        }
        u = new User();

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
        editTextUserEmail = findViewById(R.id.editTextUserEmail);
        editTextPassword = findViewById(R.id.editTextPassword);

        Gson g = new GsonBuilder().registerTypeAdapter(User.class, new DeserializedUser())
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(UserService.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(g))
                .build();

        UserService service = retrofit.create(UserService.class);

        u.setEmail(editTextUserEmail.getText().toString());
        u.setPassword(editTextPassword.getText().toString());

        Call<User> requestUser = service.login(u);

        requestUser.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                String aux;
                if(!response.isSuccessful()) {
                    aux = "Deu falha no sucesso: " + (response.code());
                    Log.i(TAG, aux);
                    Toast.makeText(getApplicationContext(), aux, Toast.LENGTH_LONG).show();
                }
                else if(response.isSuccessful()) {
                    try {
                        JSONObject jsonUser = new JSONObject(new Gson().toJson(response.body()));

                        System.out.println(response.body());

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
                        try {
                            crud.insertUser(u);
                            //User user = crud.getUser();
                            //System.out.println("Usuário logado:" + user.toString());
                        } catch(Exception e) {
                            e.printStackTrace();
                        }

                        openHome();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    System.out.print("Id: " + u.getIdUser());
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                String aux = " Deu falha no login: " + t.getMessage();
                Log.e(TAG, aux);
                Toast.makeText(getApplicationContext(), aux, Toast.LENGTH_LONG).show();
            }
        });
        openHome();
    }

    public void openHome(){
        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);

        intent.putExtra("name", u.getName());
        intent.putExtra("localization", u.getLocalization());

        startActivity(intent);
        finishAffinity();
    }
}
