package com.tcc.guiaturistico.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tcc.guiaturistico.R;

import java.util.ArrayList;
import java.util.List;

import model.Interest;
import model.InterestDeserializer;
import model.UserInterest;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import service.InterestService;
import service.UserInterestService;
import util.DBController;

/**
 * Created by Andressa on 27/05/2018.
 */

public class InterestsActivity extends AppCompatActivity {

    private CheckBox[] checkBoxInterest;
    private Button buttonSave;
    private DBController crud;
    private static final String TAG = "InterestsActivity";
    private ArrayList<Interest> interests, selectedInterests;
    private LinearLayout linearLayout;
    private ProgressBar spinner;
    private TextView textViewCheckInterests;
    private Boolean hasInterests;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interests);

        crud = new DBController(this);

        getSupportActionBar().setTitle(getString(R.string.interests));

        linearLayout = findViewById(R.id.linearCheck);

        spinner = findViewById(R.id.progressBar);
        textViewCheckInterests = findViewById(R.id.textViewCheckInterests);

        selectedInterests = new ArrayList<Interest>();
        hasInterests = false;
        //listar os interesses conforme retorno do bd
        listInterests();

        buttonSave = findViewById(R.id.buttonSave);
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                spinner.setVisibility(View.VISIBLE);
                if(hasInterests)
                    updateInterests();
                else
                    registerInterests();
            }
        });
    }

    //infla a tela conforme retorno da API
    private void listInterests(List<Interest> list) {
        this.interests = (ArrayList<Interest>) list;
        checkBoxInterest = new CheckBox[interests.size()];

        if (interests.size() > 0) {
            int i=0;
            do {
                final int j = i;
                CheckBox checkBox = new CheckBox(this);
                checkBox.setTextColor(getResources().getColor(R.color.textItem));
                checkBox.setTextSize(16);
                checkBox.setText(interests.get(j).getName());
                checkBox.setId(interests.get(j).getIdInterest());
                checkBox.setPadding(16,16,16,16);

                linearLayout.addView(checkBox);
                checkBoxInterest[i] = checkBox;
                i++;
            } while(i<interests.size());
        }

        readUserInterests();

        textViewCheckInterests.setVisibility(View.VISIBLE);
        linearLayout.setVisibility(View.VISIBLE);
        buttonSave.setVisibility(View.VISIBLE);
        spinner.setVisibility(View.GONE);
    }

    private void listInterests() {
        spinner.setVisibility(View.VISIBLE);

        Gson g = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(InterestService.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(g))
                .build();

        InterestService service = retrofit.create(InterestService.class);

        Call<List<Interest>> requestInterest = service.list();

        requestInterest.enqueue(new Callback<List<Interest>>() {
            @Override
            public void onResponse(@NonNull Call<List<Interest>> call, @NonNull Response<List<Interest>> response) {
                String aux;
                if(!response.isSuccessful()) {
                    aux = "Erro: " + (response.code());
                    Log.i(TAG, aux);
                    Toast.makeText(getApplicationContext(), aux, Toast.LENGTH_LONG).show();
                    System.out.println("sem sucesso");
                }
                else {
                    List<Interest> output = response.body();
                    Log.d(TAG, "interests: " + response.body().toString());
                    //chama método para inflar a tela
                    listInterests(output);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Interest>> call, @NonNull Throwable t) {
                String aux = " Erro: " + t.getMessage();
                Log.e(TAG, aux);
                Toast.makeText(getApplicationContext(), aux, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void registerInterests() {
        selectInterests();

        Gson g = new GsonBuilder().registerTypeAdapter(UserInterest.class, new InterestDeserializer())
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(UserInterestService.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(g))
                .build();

        UserInterestService service = retrofit.create(UserInterestService.class);
        List<UserInterest> userInterests = new ArrayList<UserInterest>();

        for(int i = 0; i<selectedInterests.size(); i++) {
            final UserInterest ui = new UserInterest();
            ui.setIdUser(crud.getUser().getIdUser());
            ui.setIdInterest(selectedInterests.get(i).getIdInterest());
            userInterests.add(ui);
        }

        System.out.println(userInterests.toString());
        Call<Integer> requestUser = service.insert(userInterests);

        requestUser.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(@NonNull Call<Integer> call, @NonNull Response<Integer> response) {
                String aux;
                if(!response.isSuccessful()) {
                    aux = "Erro: " + (response.code());
                    Log.i(TAG, aux);
                    Toast.makeText(getApplicationContext(), aux, Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(getApplicationContext(), getString(R.string.successfulRegister), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Integer> call, @NonNull Throwable t) {
                String aux = " Erro: " + t.getMessage();
                Log.e(TAG, aux);
                Toast.makeText(getApplicationContext(), aux, Toast.LENGTH_LONG).show();
            }
        });
        openHome();
    }

    public void openHome(){
        Intent intent = new Intent(this, HomeActivity.class);
        intent.putExtra("name", crud.getUser().getName());
        //intent.putExtra("localization", crud.getUser().getLocalization());
        startActivity(intent);
        finishAffinity();
    }

    public void selectInterests() {
        for (int i = 0; i < interests.size(); i++) {
            int idInterest = interests.get(i).getIdInterest();
            if (checkBoxInterest[i].isChecked()) {
            //insert
                if (!selectedInterests.contains(idInterest)) {
                    Interest in = new Interest(idInterest, null);
                    selectedInterests.add(in);
                }
            } else {
            //delete
                if (selectedInterests.contains(idInterest)) {
                    Interest in = new Interest(idInterest, null);
                    selectedInterests.remove(in);
                }
            }
            if (selectedInterests.contains(idInterest)) {
                checkBoxInterest[i].setChecked(true);
            } else {
                checkBoxInterest[i].setChecked(false);
            }
        }
        System.out.println(selectedInterests.toString());
    }

    public void readUserInterests() {
        Gson g = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(InterestService.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(g))
                .build();

        UserInterestService service = retrofit.create(UserInterestService.class);

        Call<List<UserInterest>> requestInterest = service.listByUser(crud.getUser().getIdUser());

        requestInterest.enqueue(new Callback<List<UserInterest>>() {
            @Override
            public void onResponse(@NonNull Call<List<UserInterest>> call, @NonNull Response<List<UserInterest>> response) {
                String aux;
                if(!response.isSuccessful()) {
                    aux = "Erro: " + (response.code());
                    Log.i(TAG, aux);
                    hasInterests = false;
                }
                else {
                    List<UserInterest> output = response.body();
                    if(output.size() > 0) {
                        hasInterests = true;
                        checkInterests(output);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<UserInterest>> call, @NonNull Throwable t) {
                String aux = " Erro: " + t.getMessage();
                Log.e(TAG, aux);
                Toast.makeText(getApplicationContext(), aux, Toast.LENGTH_LONG).show();
            }
        });
    }

    public void checkInterests(List<UserInterest> listUserInterests) {
        for(int i=0; i<listUserInterests.size(); i++) {
            //checar os negócios
            int aux = listUserInterests.get(i).getIdInterest();
            checkBoxInterest[aux-1].setChecked(true);
        }
    }

    //PAREI AQUI
    public void updateInterests() {
        selectInterests();

        Gson g = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(InterestService.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(g))
                .build();

        UserInterestService service = retrofit.create(UserInterestService.class);

        List<UserInterest> userInterests = new ArrayList<UserInterest>();

        for(int i = 0; i<selectedInterests.size(); i++) {
            final UserInterest ui = new UserInterest();
            ui.setIdUser(crud.getUser().getIdUser());
            ui.setIdInterest(selectedInterests.get(i).getIdInterest());
            userInterests.add(ui);
        }
        System.out.println("interesses selecionados " + userInterests.toString());
        Call<Void> requestInterest = service.update(crud.getUser().getIdUser(), userInterests);

        requestInterest.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                String aux;
                if(!response.isSuccessful()) {
                    aux = "Erro: " + (response.code());
                    Log.i(TAG, aux);
                    Toast.makeText(getApplicationContext(), aux, Toast.LENGTH_LONG).show();
                    System.out.println("sem sucesso");
                }
                else {
                    Toast.makeText(getApplicationContext(), getString(R.string.saveProfile), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                String aux = " Erro: " + t.getMessage();
                Log.e(TAG, aux);
                Toast.makeText(getApplicationContext(), aux, Toast.LENGTH_LONG).show();
            }
        });
        openHome();
    }
}


