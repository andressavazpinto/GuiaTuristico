package com.tcc.guiaturistico.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tcc.guiaturistico.R;

import model.DeserializedUser;
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

    Button buttonSave;
    public Spinner spinnerLanguage;
    public EditText editTextName, editTextDateOfBirth, editTextEmail, editTextPassword, editTextOccupation, editTextLocalization;
    DBController crud;
    User user;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        crud = new DBController(this);
        user = crud.getUser();
        read(user.getIdUser()); //carrega na tela os dados do usuário

        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //Mostrar o botão
        getSupportActionBar().setHomeButtonEnabled(true);      //Ativar o botão
        getSupportActionBar().setTitle(Message.profile);

        spinnerLanguage = findViewById(R.id.spinnerLanguage);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.list_languages, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLanguage.setAdapter(adapter);

        buttonSave = findViewById(R.id.buttonSave);
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                update();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) { //Botão adicional na ToolBar
        switch (item.getItemId()) {
            case android.R.id.home:  //ID do seu botão (gerado automaticamente pelo android, usando como está, deve funcionar
                startActivity(new Intent(this, HomeActivity.class));  //O efeito ao ser pressionado do botão (no caso abre a activity)
                finishAffinity();  //Método para matar a activity e não deixa-lá indexada na pilhagem
                break;
            default:break;
        }
        return true;
    }

    public void read(int id) {

        Gson g = new GsonBuilder().registerTypeAdapter(User.class, new DeserializedUser())
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

                    editTextName = findViewById(R.id.editTextName);
                    editTextDateOfBirth = findViewById(R.id.editTextDateOfBirth);
                    editTextEmail = findViewById(R.id.editTextUserEmail);
                    editTextPassword = findViewById(R.id.editTextPassword);
                    editTextOccupation = findViewById(R.id.editTextOccupation);
                    editTextLocalization = findViewById(R.id.editTextLocalization);

                    editTextName.setText(u.getName());

                    String array[];
                    array = u.getDateOfBirth().split("-");
                    String aux = array[2]+"/"+array[1]+"/"+array[0];
                    editTextDateOfBirth.setText(aux);

                    editTextEmail.setText(u.getEmail());
                    //editTextPassword
                    editTextOccupation.setText(u.getOccupation());
                    editTextLocalization.setText(u.getLocalization());
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
        Gson g = new GsonBuilder().registerTypeAdapter(User.class, new DeserializedUser())
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

        u.setEmail(editTextEmail.getText().toString());
        u.setPassword(editTextPassword.getText().toString());
        u.setOccupation(editTextOccupation.getText().toString());
        u.setLanguage(selectLanguage());
        u.setLocalization(editTextLocalization.getText().toString());

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
                    Toast.makeText(getApplicationContext(), Message.saveProfile, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                String aux = " Erro: " + t.getMessage();
                Log.e("erro", aux);
                Toast.makeText(getApplicationContext(), aux, Toast.LENGTH_LONG).show();
            }
        });
    }
}
