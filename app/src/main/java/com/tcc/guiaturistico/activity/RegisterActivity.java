//https://www.youtube.com/watch?v=bP9RYHKJzNs
package com.tcc.guiaturistico.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.support.v7.app.AppCompatActivity;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tcc.guiaturistico.R;

import model.User;
import model.UserDes;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import service.UserService;
import util.Mask;
import util.Status;

/**
 * Created by Andressa on 13/05/2018.
 */

public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = "Error";
    public EditText editTextName, editTextDateOfBirth, editTextUserEmail, editTextPassword, editTextOccupation, editTextLocalization, editTextLanguage;
    public Spinner spinnerLanguage;
    public Button buttonRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        if (android.os.Build.VERSION.SDK_INT > 9)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        editTextName = (EditText) findViewById(R.id.editTextName);

        editTextDateOfBirth = (EditText) findViewById(R.id.editTextDateOfBirth);
        editTextDateOfBirth.addTextChangedListener(Mask.insert(Mask.FORMAT_DATE, editTextDateOfBirth));

        editTextUserEmail = (EditText) findViewById(R.id.editTextUserEmail);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        editTextOccupation = (EditText) findViewById(R.id.editTextOccupation);

        //editTextLanguage = (EditText) findViewById(R.id.editTextLanguage);
        //String[] list_languages = getResources().getStringArray(R.array.list_languages);

        spinnerLanguage = findViewById(R.id.spinnerLanguage);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.list_languages, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLanguage.setAdapter(adapter);


        //pegar o idioma do celular spinnerLanguage.setSelection();

        editTextLocalization = (EditText) findViewById(R.id.editTextLocalization);

        /*editTextLanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectLanguage();
            }
        });*/

        buttonRegister = findViewById(R.id.buttonContinue);

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                register();
            }
        });

    }

    public String selectLanguage() {
        return (String) spinnerLanguage.getSelectedItem();
    }

    /*public void selectLanguage () {
        Intent intent = new Intent(this, LanguageActivity.class);
        startActivity(intent);
    }*/

    public void register() {

        Gson g = new GsonBuilder().registerTypeAdapter(User.class, new UserDes())
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(UserService.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(g))
                .build();

        UserService service = retrofit.create(UserService.class);

        final User u = new User();
        u.setName(editTextName.getText().toString());

        String array[] = new String[3];
        array = editTextDateOfBirth.getText().toString().split("/");
        String aux = array[2]+"-"+array[1]+"-"+array[0];
        u.setDateOfBirth(aux);

        u.setEmail(editTextUserEmail.getText().toString());
        u.setPassword(editTextPassword.getText().toString());
        u.setOccupation(editTextOccupation.getText().toString());

        u.setLanguage(selectLanguage());

        u.setLocalization(editTextLocalization.getText().toString());
        u.setStatusAccount(Status.Active);

        Call<Integer> requestUser = service.register(u);

        requestUser.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                String aux;
                if(!response.isSuccessful()) {
                    aux = "Erro: " + (response.code());
                    Log.i(TAG, aux);
                    Toast.makeText(getApplicationContext(), aux, Toast.LENGTH_LONG).show();
                }
                else {
                    u.setIdUser(Integer.parseInt(response.body().toString()));
                    System.out.print("Id: " + u.getIdUser());

                    //ESSA MENSAGEM TEM QUE SER PEGA PELO @STRINGS
                    Toast.makeText(getApplicationContext(), "Cadastrado com sucesso", Toast.LENGTH_LONG).show();
                    openInterests();
                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                String aux = " Erro: " + t.getMessage();
                Log.e(TAG, aux);
                Toast.makeText(getApplicationContext(), aux, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void openInterests() {
        Intent intent = new Intent(RegisterActivity.this, InterestsActivity.class);
        startActivity(intent);
    }
}




        //consulta de um usuário

        /*Call<User> requestUsuario = service.consultar(3);
        requestUsuario.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if(!response.isSuccessful())
                    Log.i(TAG, "Deu erro: " + response.code());
                else {
                    User u = response.body();

                    editTextName.setText(u.getNome());
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e(TAG, "Deu ruim: " + t.getMessage());
            }
        });*/