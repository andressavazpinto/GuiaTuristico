package com.tcc.guiaturistico.activity;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
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
import util.Age;
import util.DBController;
import util.MaskDate;
import util.StatusUser;

/**
 * Created by Andressa on 07/06/2018.
 */

public class ProfileActivity extends AppCompatActivity {
    private static final String TAG = "ProfileActivity";
    private static final int ERROR_DIALOG_REQUEST = 9001;
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
        getSupportActionBar().setTitle(getString(R.string.profile));

        spinnerLanguage = findViewById(R.id.spinnerLanguage);
        spinnerLanguage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
                selectLanguage(position);
            }
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        if(isServicesOK())
            listLanguages();

        editTextName = findViewById(R.id.editTextName);
        editTextDateOfBirth = findViewById(R.id.editTextDateOfBirth);
        editTextDateOfBirth.addTextChangedListener(MaskDate.insert(MaskDate.FORMAT_DATE, editTextDateOfBirth));
        editTextUserEmail = findViewById(R.id.editTextUserEmail);
        editTextPassword = findViewById(R.id.editTextPassword);

        editTextPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    validateFields();
                }
                return false;
            }
        });

        Button buttonSave = findViewById(R.id.buttonSave);
        buttonSave.setVisibility(View.VISIBLE);
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                if (imm.isActive()) {
                    hideSoftKeyboard();
                }

                validateFields();
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
                    Log.i(TAG, "Erro: " + response.code());
                else {
                    u = response.body();

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
                Log.e(TAG, "Erro: " + t.getMessage());
            }
        });
    }

    public void selectLanguage(int position) {
        language = languages.get(position).getLanguage();
    }

    public void update() {
        spinner.setVisibility(View.VISIBLE);
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

        if(editTextPassword.getText().length() == 0)
            u.setPassword(user.getPassword());
        else
            u.setPassword(editTextPassword.getText().toString());

        u.setLanguage(language);

        Call<User> requestUser = service.update(u);

        requestUser.enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                String aux;
                if(!response.isSuccessful()) {
                    aux = "Erro: " + (response.code());
                    Log.i(TAG, aux);
                    Toast.makeText(getApplicationContext(), aux, Toast.LENGTH_LONG).show();
                }
                else if(response.isSuccessful()) {
                    u.setStatusAccount(Enum.valueOf(StatusUser.class, "Active"));
                    crud.updateUser(u);
                    Toast.makeText(getApplicationContext(), getString(R.string.saveProfile), Toast.LENGTH_SHORT).show();
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

    public void validateFields() {
        spinner.setVisibility(View.VISIBLE);
        int aux = 1;
        String email = editTextUserEmail.getText().toString();
        Age age = new Age();
        String dateOfBirth = editTextDateOfBirth.getText().toString();

        if(editTextName.getText().length() == 0){
            editTextName.setBackground(ContextCompat.getDrawable(this, R.drawable.shape_line_error));
            aux = -1;
        }
        else
            editTextName.setBackground(ContextCompat.getDrawable(this, R.drawable.shape_line_success));

        if(dateOfBirth.length() == 0) {
            editTextDateOfBirth.setBackground(ContextCompat.getDrawable(this, R.drawable.shape_line_error));
            aux = -1;
        }
        else if(!age.validateDate(dateOfBirth)){
            editTextDateOfBirth.setBackground(ContextCompat.getDrawable(this, R.drawable.shape_line_error));
            aux = -1;
        }
        else if(age.calculaIdade(dateOfBirth,"dd-MM-yyyy") < 18) {
            editTextDateOfBirth.setBackground(ContextCompat.getDrawable(this, R.drawable.shape_line_error));
            aux = 0;
            Toast.makeText(this, getText(R.string.age18), Toast.LENGTH_SHORT).show();
        }
        else
            editTextDateOfBirth.setBackground(ContextCompat.getDrawable(this, R.drawable.shape_line_success));


        if(email.length() == 0 | !email.contains("@")){
            editTextUserEmail.setBackground(ContextCompat.getDrawable(this, R.drawable.shape_line_error));
            aux = -1;
        }
        else
            editTextUserEmail.setBackground(ContextCompat.getDrawable(this, R.drawable.shape_line_success));

        checkEmail(email, aux);
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
                Log.e(TAG, aux);
            }
        });
    }

    public void checkEmail(final String email, final int validate) {
        final boolean[] aux = {false};

        Gson g = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(UserService.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(g))
                .build();

        UserService service = retrofit.create(UserService.class);

        Call<Boolean> requestUser = service.checkEmail(email);

        requestUser.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(@NonNull Call<Boolean> call, @NonNull Response<Boolean> response) {
                if (response.isSuccessful())
                    aux[0] = response.body();

                if(aux[0]) {
                    if(email.equals(user.getEmail()))
                        System.out.print("email dele mesmo, então não faz nada");
                    else {
                        editTextUserEmail.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.shape_line_error));
                        Toast.makeText(getApplicationContext(), getText(R.string.emailExist), Toast.LENGTH_SHORT).show();
                    }

                    if(validate == -1)
                        Toast.makeText(getApplicationContext(), getString(R.string.checkFields), Toast.LENGTH_SHORT).show();
                    else if (email.equals(user.getEmail()) && validate == 1)
                        update();
                }
                else {
                    if(validate == -1)
                        Toast.makeText(getApplicationContext(), getString(R.string.checkFields), Toast.LENGTH_SHORT).show();
                    else if (validate == 1)
                        update();
                }
                spinner.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(@NonNull Call<Boolean> call, @NonNull Throwable t) {
                String aux = "Erro: " + t.getMessage();
                Log.e(TAG, aux);
                Toast.makeText(getApplicationContext(), aux, Toast.LENGTH_LONG).show();
            }
        });
    }
}
