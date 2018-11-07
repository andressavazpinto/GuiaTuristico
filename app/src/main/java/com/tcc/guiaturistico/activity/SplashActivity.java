package com.tcc.guiaturistico.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tcc.guiaturistico.R;

import model.Localization;
import model.Search;
import model.SearchDeserializer;
import model.User;
import model.UserDeserializer;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import service.LocalizationService;
import service.SearchService;
import service.UserService;
import util.DBController;
import util.StatusSearch;

/**
 * Created by Andressa on 27/05/2018.
 */

public class SplashActivity extends Activity implements Runnable {
    private static final String TAG = "SplashActivity";
    private DBController crud;
    private User u;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        crud = new DBController(this);
        u = crud.getUser();

        Handler splashScreen = new Handler();
        splashScreen.postDelayed(SplashActivity.this, 2800);
    }

    public void run() {
        try {
            if (u.getIdUser() != 0) {
                login(u.getEmail(), u.getPassword());
            } else {
                startActivity(new Intent(this, MainActivity.class));
                finish();
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void login(String email, final String password) {
        Gson g = new GsonBuilder().registerTypeAdapter(User.class, new UserDeserializer())
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(UserService.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(g))
                .build();

        UserService service = retrofit.create(UserService.class);

        u.setEmail(email);
        u.setPassword(password);

        Call<User> requestUser = service.login(u);

        requestUser.enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                if(response.isSuccessful()) {
                    try {
                        u = response.body();

                        try {
                            verifyStatusSearch(u.getIdUser());
                        } catch(Exception e) {
                            e.printStackTrace();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                else {
                    String aux = "Erro: " + response.code();
                    Log.e(TAG, aux);
                    Toast.makeText(getApplicationContext(), aux, Toast.LENGTH_LONG).show();
                }

            }
            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                String aux = "Erro: " + t.getMessage();
                Log.e(TAG, aux);
                Toast.makeText(getApplicationContext(), aux, Toast.LENGTH_LONG).show();
                login(u.getEmail(), u.getPassword());
            }
        });
    }

    public void verifyStatusSearch(int id) {
        Gson g = new GsonBuilder().registerTypeAdapter(Search.class, new SearchDeserializer())
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(UserService.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(g))
                .build();

        SearchService service = retrofit.create(SearchService.class);

        Call<Search> requestSearch = service.read(id);

        requestSearch.enqueue(new Callback<Search>() {
            @Override
            public void onResponse(@NonNull Call<Search> call, @NonNull Response<Search> response) {
                String aux;
                if(!response.isSuccessful()) {
                    aux = "Erro: " + (response.code());
                    Log.i(TAG, aux);
                }
                else if(response.isSuccessful()) {
                    try {
                        Search s = response.body();

                        if(crud.getStatusSearch() != null)
                            try{crud.updateStatusSearch(s.getStatus().toString());} catch(Exception e){Log.i(TAG, e.getMessage());}
                        else
                            try{crud.insertStatusSearch(s.getStatus().toString());} catch(Exception e){Log.i(TAG, e.getMessage());}

                        getLocalization(u.getIdLocalization(), s);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<Search> call, @NonNull Throwable t) {
                String aux = "Erro: " + t.getMessage();
                Log.e(TAG, aux);
                Toast.makeText(getApplicationContext(), aux, Toast.LENGTH_LONG).show();
            }
        });
    }

    public void openHome(Search s, Localization loc){
        Intent intent;
        StatusSearch status = (Enum.valueOf(StatusSearch.class, s.getStatus().toString()));
        switch (status) {
            case Accepted:
                intent = new Intent(this, ChatActivity.class);
                break;
            default:
                intent = new Intent(this, HomeActivity.class);
        }

        intent.putExtra("name", u.getName());
        intent.putExtra("localization", loc.getCity() + ", " + loc.getUf());
        intent.putExtra("score", u.getScoreS());
        startActivity(intent);
        finish();
    }

    public void getLocalization(int idLocalization, Search s) {
        final int idAux = idLocalization;
        final Search search = s;
        Gson g = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(LocalizationService.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(g))
                .build();

        LocalizationService service = retrofit.create(LocalizationService.class);

        Call<Localization> requestLocalization = service.read(idLocalization);

        requestLocalization.enqueue(new Callback<Localization>() {
            @Override
            public void onResponse(@NonNull Call<Localization> call, @NonNull Response<Localization> response) {
                if (response.isSuccessful()) {
                    openHome(search, response.body());
                } else {
                    Log.i(TAG, "Erro: " + (response.code()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<Localization> call, @NonNull Throwable t) {
                Log.e(TAG, "Erro: " + t.getMessage());
                getLocalization(idAux, search);
            }
        });
    }
}
