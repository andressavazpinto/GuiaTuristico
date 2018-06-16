package com.tcc.guiaturistico.activity;

import android.content.Intent;
import android.os.Bundle;
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
import util.Message;

/**
 * Created by Andressa on 27/05/2018.
 */

public class InterestsActivity extends AppCompatActivity {

    private CheckBox[] checkBoxInterest;
    private Button buttonContinue;
    private DBController crud;
    private static final String TAG = "Error";
    private ArrayList<Interest> interests;
    private ArrayList<Interest> selectedInterests;
    private LinearLayout linearLayout;
    private ProgressBar spinner;
    private TextView textViewCheckInterests;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interests);

        crud = new DBController(this);

        getSupportActionBar().setTitle(Message.interests);

        linearLayout = findViewById(R.id.linearCheck);

        spinner = findViewById(R.id.progressBar);
        textViewCheckInterests = findViewById(R.id.textViewCheckInterests);

        selectedInterests = new ArrayList<Interest>();
        listInterests();

        buttonContinue = findViewById(R.id.buttonContinue);
        buttonContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                spinner.setVisibility(View.VISIBLE);
                registerInterests();
            }
        });
    }

    private void listInterests(List<Interest> list) {
        this.interests = (ArrayList<Interest>) list;
        checkBoxInterest = new CheckBox[list.size()];

        if (list.size() > 0) {
            int i=0;
            do {
                final int j = i;
                CheckBox checkBox = new CheckBox(this);
                checkBox.setTextColor(getResources().getColor(R.color.textItem));
                checkBox.setTextSize(16);
                checkBox.setText(list.get(j).getName());
                checkBox.setId(list.get(j).getIdInterest());
                checkBox.setPadding(16,16,16,16);

                linearLayout.addView(checkBox);
                checkBoxInterest[i] = checkBox;
                i++;
            } while(i<list.size());
        }
        textViewCheckInterests.setVisibility(View.VISIBLE);
        buttonContinue.setVisibility(View.VISIBLE);
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
            public void onResponse(Call<List<Interest>> call, Response<List<Interest>> response) {
                String aux;
                if(!response.isSuccessful()) {
                    aux = "Erro: " + (response.code());
                    Log.i(TAG, aux);
                    Toast.makeText(getApplicationContext(), aux, Toast.LENGTH_LONG).show();
                    System.out.println("sem sucesso");
                }
                else {
                    List<Interest> output = response.body();
                    listInterests(output);
                }
            }

            @Override
            public void onFailure(Call<List<Interest>> call, Throwable t) {
                String aux = " Erro: " + t.getMessage();
                Log.e(TAG, aux);
                Toast.makeText(getApplicationContext(), aux, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void registerInterests() {
        select();

        Gson g = new GsonBuilder().registerTypeAdapter(UserInterest.class, new InterestDeserializer())
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(UserInterestService.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(g))
                .build();

        UserInterestService service = retrofit.create(UserInterestService.class);

        for(int i = 0; i<selectedInterests.size(); i++) {

            final UserInterest userInterest = new UserInterest();
            userInterest.setIdUser(crud.getUser().getIdUser());
            userInterest.setIdInterest(selectedInterests.get(i).getIdInterest());
            System.out.println(userInterest.toString());

            Call<Integer> requestUser = service.insert(userInterest);

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
                        Toast.makeText(getApplicationContext(), "Cadastrado com sucesso", Toast.LENGTH_LONG).show();

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
        openHome();
    }

    public void openHome(){
        Intent intent = new Intent(this, HomeActivity.class);
        intent.putExtra("name", crud.getUser().getName());
        intent.putExtra("localization", crud.getUser().getLocalization());
        startActivity(intent);
        finishAffinity();
    }

    public void select() {
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
}

