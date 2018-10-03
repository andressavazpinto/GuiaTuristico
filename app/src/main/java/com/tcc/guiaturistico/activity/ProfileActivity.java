package com.tcc.guiaturistico.activity;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.tcc.guiaturistico.R;

import java.util.ArrayList;
import java.util.List;

import model.Language;
import model.LanguageDeserializer;
import model.Translate;
import model.UserDeserializer;
import model.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import service.TranslationService;
import service.UserService;
import util.DBController;
import util.Message;

/**
 * Created by Andressa on 07/06/2018.
 */

public class ProfileActivity extends AppCompatActivity {
    private static final String TAG = "ProfileActivity";
    private static final int ERROR_DIALOG_REQUEST = 9001;
    private Button buttonSave;
    private Spinner spinnerLanguage;
    private EditText editTextName, editTextDateOfBirth, editTextUserEmail, editTextPassword;
    private ProgressBar spinner;
    private DBController crud;
    private User user;
    User u;
    List<String> list_languages;
    List<Language> languages;
    String language;

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
        getSupportActionBar().setTitle(Message.profile);

        spinnerLanguage = findViewById(R.id.spinnerLanguage);
        spinnerLanguage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
                //pega nome pela posição
                Log.d(TAG, "Nome Selecionado: " + selectLanguage(position));
            }
            public void onNothingSelected(AdapterView<?> adapterView) {
                //return;
            }
        });

        if(isServicesOK())
            listLanguages();

        editTextName = findViewById(R.id.editTextName);
        editTextDateOfBirth = findViewById(R.id.editTextDateOfBirth);
        editTextUserEmail = findViewById(R.id.editTextUserEmail);
        editTextPassword = findViewById(R.id.editTextPassword);

        buttonSave = findViewById(R.id.buttonSave);
        buttonSave.setVisibility(View.VISIBLE);
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                if (imm.isActive()) {
                    hideSoftKeyboard();
                }
                if(validateFields()) {
                    spinner.setVisibility(View.VISIBLE);
                    update();
                }
            }
        });
    }

    public void hideSoftKeyboard() {
        if(getCurrentFocus()!=null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) { //botão voltar na ToolBar
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
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

        Call<User> requestUser = service.read(id);
        requestUser.enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                if(!response.isSuccessful())
                    Log.i("erro", "Deu erro: " + response.code());
                else {
                    u = response.body();
                    System.out.println(response.body().toString());

                    editTextName.setText(u.getName());

                    String array[];
                    array = u.getDateOfBirth().split("-");
                    String aux = array[2]+"/"+array[1]+"/"+array[0];
                    editTextDateOfBirth.setText(aux);
                    editTextUserEmail.setText(u.getEmail());
                }
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                Log.e("erro", "Deu ruim: " + t.getMessage());
            }
        });
    }

    public String selectLanguage(int position) {
        language = languages.get(position).getLanguage();
        return language;
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
        u.setLanguage(language);
        Log.d(TAG, "pass: " + u.getPassword() + "lang: " + language);

        Call<User> requestUser = service.update(u);

        requestUser.enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                String aux;
                if(!response.isSuccessful()) {
                    aux = "Erro: " + (response.code());
                    Log.i("erro", aux);
                    Toast.makeText(getApplicationContext(), aux, Toast.LENGTH_LONG).show();
                }
                else if(response.isSuccessful()) {
                    User us = response.body();
                    crud.updateUser(us);
                    Toast.makeText(getApplicationContext(), Message.saveProfile, Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
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

        if(editTextUserEmail.getText().length() == 0 | !editTextUserEmail.getText().toString().contains("@")){
            editTextUserEmail.setBackground(ContextCompat.getDrawable(this, R.drawable.shape_line_error));
            aux = false;
        }
        else
            editTextUserEmail.setBackground(ContextCompat.getDrawable(this, R.drawable.shape_line_success));

        return aux;
    }

    public boolean isServicesOK() {
        Log.d(TAG, "isServicesOK: checking google services version");
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);
        if(available == ConnectionResult.SUCCESS) {
            Log.d(TAG, "isServicesOK: Google Play services is working");
            return true;
        } else if(GoogleApiAvailability.getInstance()
                .isUserResolvableError(available)) {
            Log.d(TAG, "isSErvicesOK: an error occured but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        } else {
            Toast.makeText(this, "You can't connect", Toast.LENGTH_LONG).show();
        }
        return false;
    }

    private void listLang(List<Language> output) {
        this.languages = output;
        int systemLanguage = 0;
        list_languages = new ArrayList<String>();

        if(languages.size() > 0) {
            int i=0;
            do {
                if(languages.get(i).getLanguage().equals(u.getLanguage()))
                    systemLanguage = i;

                list_languages.add(languages.get(i).getName());
                i++;
            } while(i<languages.size());
        }

        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item,
                list_languages);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spinnerLanguage.setAdapter(spinnerArrayAdapter);
        spinnerLanguage.setSelection(systemLanguage);
        spinner.setVisibility(View.GONE);
    }

    public void listLanguages() {

        Gson g = new GsonBuilder().registerTypeAdapter(Language.class, new LanguageDeserializer())
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(TranslationService.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(g))
                .build();

        TranslationService service = retrofit.create((TranslationService.class));

        final Translate t = new Translate("", "", "pt", "text");
        String API_KEY = "AIzaSyByLqEvttULJFQRbNxpPqa4dxETVOgP_e8";

        Call<JsonObject> request = service.listLanguages(t, API_KEY);

        request.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JsonObject body = response.body();
                    JsonArray langItens = body.get("data").getAsJsonObject().get("languages").getAsJsonArray();
                    List<Language> output = new ArrayList<Language>();

                    for (int i = 0; i < langItens.size(); i++) {
                        Language lang = new Language();
                        lang.setName(langItens.get(i).getAsJsonObject().get("name").getAsString());
                        lang.setLanguage(langItens.get(i).getAsJsonObject().get("language").getAsString());
                        output.add(lang);
                    }

                    listLang(output);
                }
            }
            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                String aux = " Erro: " + t.getMessage();
                Log.e("listLanguages", aux);
            }
        });
    }
}
