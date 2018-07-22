package com.tcc.guiaturistico.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tcc.guiaturistico.R;

import model.UserDeserializer;
import model.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import service.UserService;
import util.DBController;
import util.Message;

/**
 * Created by Andressa on 07/06/2018.
 */

public class ProfileActivity extends AppCompatActivity {

    private Button buttonSave;
    private Spinner spinnerLanguage;
    private EditText editTextName, editTextDateOfBirth, editTextUserEmail, editTextPassword, editTextOccupation, editTextLocalization;
    private ProgressBar spinner;
    private DBController crud;
    private User user;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        spinner = findViewById(R.id.progressBar);

        crud = new DBController(this);
        user = crud.getUser();
        spinner.setVisibility(View.VISIBLE);
        read(user.getIdUser()); //carrega na tela os dados do usuário

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle(Message.profile);

        spinnerLanguage = findViewById(R.id.spinnerLanguage);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.list_languages, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLanguage.setAdapter(adapter);

        editTextName = findViewById(R.id.editTextName);
        editTextDateOfBirth = findViewById(R.id.editTextDateOfBirth);
        editTextUserEmail = findViewById(R.id.editTextUserEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextOccupation = findViewById(R.id.editTextOccupation);
        editTextLocalization = findViewById(R.id.editTextLocalization);

        buttonSave = findViewById(R.id.buttonSave);
        buttonSave.setVisibility(View.VISIBLE);
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                if (imm.isActive()) {
                    imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                }
                if(validateFields()) {
                    spinner.setVisibility(View.VISIBLE);
                    update();
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) { //Botão adicional na ToolBar
        switch (item.getItemId()) {
            case android.R.id.home:
                startActivity(new Intent(this, HomeActivity.class));
                finishAffinity();
                break;
            default:break;
        }
        return true;
    }

    public void read(int id) {

        Gson g = new GsonBuilder().registerTypeAdapter(User.class, new UserDeserializer())
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(UserService.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(g))
                .build();

        UserService service = retrofit.create(UserService.class);

        Call<User> requestUsuario = service.read(id);
        requestUsuario.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if(!response.isSuccessful())
                    Log.i("erro", "Deu erro: " + response.code());
                else {
                    User u = response.body();
                    System.out.println(response.body().toString());

                    editTextName.setText(u.getName());

                    String array[];
                    array = u.getDateOfBirth().split("-");
                    String aux = array[2]+"/"+array[1]+"/"+array[0];
                    editTextDateOfBirth.setText(aux);

                    editTextUserEmail.setText(u.getEmail());
                    editTextOccupation.setText(u.getOccupation());
                    //editTextLocalization.setText(u.getLocalization());

                    spinner.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e("erro", "Deu ruim: " + t.getMessage());
            }
        });
    }

    public String selectLanguage() {
        return (String) spinnerLanguage.getSelectedItem();
    }

    public void update() {
        Gson g = new GsonBuilder().registerTypeAdapter(User.class, new UserDeserializer())
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(UserService.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(g))
                .build();

        UserService service = retrofit.create(UserService.class);

        final User u = new User();

        u.setIdUser(user.getIdUser());
        u.setName(editTextName.getText().toString());

        String array[];
        array = editTextDateOfBirth.getText().toString().split("/");
        String aux = array[2]+"-"+array[1]+"-"+array[0];
        u.setDateOfBirth(aux);

        u.setEmail(editTextUserEmail.getText().toString());
        u.setPassword(editTextPassword.getText().toString());
        u.setOccupation(editTextOccupation.getText().toString());
        u.setLanguage(selectLanguage());
        //u.setLocalization(editTextLocalization.getText().toString());

        Call<User> requestUser = service.update(u);

        requestUser.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                String aux;
                if(!response.isSuccessful()) {
                    aux = "Erro: " + (response.code());
                    Log.i("erro", aux);
                    Toast.makeText(getApplicationContext(), aux, Toast.LENGTH_LONG).show();
                }
                else if(response.isSuccessful()) {
                    User us = response.body();
                    crud.updateUser(us);
                    System.out.println(us.toString());
                    Toast.makeText(getApplicationContext(), Message.saveProfile, Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                String aux = " Erro: " + t.getMessage();
                Log.e("erro", aux);
                Toast.makeText(getApplicationContext(), aux, Toast.LENGTH_LONG).show();
            }
        });
        spinner.setVisibility(View.GONE);
    }

    public boolean validateFields() {
        Boolean aux = true;
        if(editTextName.getText().length() == 0){
            editTextName.setBackground(ContextCompat.getDrawable(this, R.drawable.shape_line_error));
            aux = false;
        }
        else
            editTextUserEmail.setBackground(ContextCompat.getDrawable(this, R.drawable.shape_line_success));

        if(editTextDateOfBirth.getText().length() == 0){
            editTextDateOfBirth.setBackground(ContextCompat.getDrawable(this, R.drawable.shape_line_error));
            aux = false;
        }
        else
            editTextDateOfBirth.setBackground(ContextCompat.getDrawable(this, R.drawable.shape_line_success));

        if(editTextLocalization.getText().length() == 0){
            editTextLocalization.setBackground(ContextCompat.getDrawable(this, R.drawable.shape_line_error));
            aux = false;
        }
        else
            editTextLocalization.setBackground(ContextCompat.getDrawable(this, R.drawable.shape_line_success));

        if(editTextUserEmail.getText().length() == 0 | !editTextUserEmail.getText().toString().contains("@")){
            editTextUserEmail.setBackground(ContextCompat.getDrawable(this, R.drawable.shape_line_error));
            aux = false;
        }
        else
            editTextUserEmail.setBackground(ContextCompat.getDrawable(this, R.drawable.shape_line_success));

        return aux;
    }
}
