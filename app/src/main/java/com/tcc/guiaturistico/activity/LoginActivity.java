package com.tcc.guiaturistico.activity;

import com.google.api.gax.paging.Page;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.BucketInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
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

import model.UserDeserializer;
import model.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import service.UserService;
import util.DBController;
import util.Status;

/**
 * Created by Andressa on 31/03/2018.
 */

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "Error";
    private EditText editTextUserEmail, editTextPassword;
    private TextView textForgetPass;
    private ProgressBar spinner;
    private Button buttonLogin;
    private DBController crud;
    private User u;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);



        crud = new DBController(this);
        u = new User();

        spinner = findViewById(R.id.progressBar);

        editTextUserEmail = findViewById(R.id.editTextUserEmail);
        editTextPassword = findViewById(R.id.editTextPassword);

        buttonLogin = findViewById(R.id.buttonLogin);
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                if (imm.isActive()) {
                    imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                }
                if(validateFields()) {
                    spinner.setVisibility(View.VISIBLE);

                    int SDK_INT = android.os.Build.VERSION.SDK_INT;
                    if (SDK_INT > 8)
                    {
                        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                                .permitAll().build();
                        StrictMode.setThreadPolicy(policy);
                        authenticate();
                    }

                    login();
                }
            }
        });


        textForgetPass =  findViewById(R.id.textForgotPass);
        textForgetPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment d = new DialogForgotPass();
                d.show(getFragmentManager(), "esqueceu");
            }
        });
    }

    public void login() {
        Gson g = new GsonBuilder().registerTypeAdapter(User.class, new UserDeserializer())
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
                        //u.setLocalization(jsonUser.getString("localization"));
                        u.setStatusAccount(Enum.valueOf(Status.class, jsonUser.getString("statusAccount")));

                        try {
                            crud.insertUser(u);
                            openHome();
                        } catch(Exception e) {
                            e.printStackTrace();
                        }


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
        spinner.setVisibility(View.GONE);
    }

    public void openHome(){
        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);

        intent.putExtra("name", u.getName());
        //intent.putExtra("localization", u.getLocalization());

        startActivity(intent);
        finishAffinity();
    }

    public boolean validateFields() {
        Boolean aux = true;
        if(editTextUserEmail.getText().length() == 0 | !editTextUserEmail.getText().toString().contains("@")){
            editTextUserEmail.setBackground(ContextCompat.getDrawable(LoginActivity.this, R.drawable.shape_line_error));
            aux = false;
        }
        else
            editTextUserEmail.setBackground(ContextCompat.getDrawable(LoginActivity.this, R.drawable.shape_line_success));
        if(editTextPassword.getText().length() == 0){
            editTextPassword.setBackground(ContextCompat.getDrawable(LoginActivity.this, R.drawable.shape_line_error));
            aux = false;
        }
        else
            editTextPassword.setBackground(ContextCompat.getDrawable(LoginActivity.this, R.drawable.shape_line_success));
        return aux;
    }

    public void authenticate() {
        /*Storage storage = StorageOptions.getDefaultInstance().getService();
        Page<Bucket> buckets = storage.list();
        for (Bucket bucket : buckets.iterateAll()) {
            // do something with the info
            System.out.println(buckets.toString());
        }*/
        Storage storage = StorageOptions.getDefaultInstance().getService();

        // The name for the new bucket
        String bucketName = "my-new-bucket";//args[0];  //

        // Creates the new bucket
        Bucket bucket = storage.create(BucketInfo.of(bucketName));

        System.out.printf("Bucket %s created.%n", bucket.getName());
    }
}