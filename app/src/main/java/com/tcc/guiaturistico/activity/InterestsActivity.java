package com.tcc.guiaturistico.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tcc.guiaturistico.R;

import java.util.ArrayList;
import java.util.List;

import model.Interest;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import service.InterestService;
import util.DBHelper;
import util.Message;

/**
 * Created by Andressa on 27/05/2018.
 */

public class InterestsActivity extends AppCompatActivity {

    CheckBox[] checkBoxInterest;
    Button buttonContinue;
    DBHelper dbHelper;
    private static final String TAG = "Error";
    int array[];
    ArrayList<Interest> interests;
    Interest interest;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interests);

        dbHelper = new DBHelper(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //Mostrar o botão
        getSupportActionBar().setHomeButtonEnabled(true);      //Ativar o botão
        getSupportActionBar().setTitle(Message.interests);     //Titulo para ser exibido na sua Action Bar em frente à seta

        listInterests();

        buttonContinue = findViewById(R.id.buttonContinue);
        /*buttonContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //registerInterests();
            }
        });*/
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) { //Botão adicional na ToolBar
        switch (item.getItemId()) {
            case android.R.id.home:  //ID do seu botão (gerado automaticamente pelo android, usando como está, deve funcionar
                startActivity(new Intent(this, LoginActivity.class));  //O efeito ao ser pressionado do botão (no caso abre a activity)
                finishAffinity();  //Método para matar a activity e não deixa-lá indexada na pilhagem
                break;
            default:break;
        }
        return true;
    }

    private void listInterests(List<Interest> list) {
        this.interests = (ArrayList<Interest>) list;
        System.out.println("dentro do lisInterests" + this.interests.toString());

        checkBoxInterest = new CheckBox[1];
        checkBoxInterest[0] = findViewById(R.id.checkBoxInterest);

        ListView layout = (ListView) findViewById(R.id.linear_view);
        checkBoxInterest[0].setText(interests.get(0).getName());
        layout.addView((View)checkBoxInterest[0]);
    }

    private void listInterests() {
        System.out.println("entrou em listIntesrests()");
        Gson g = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(InterestService.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(g))
                .build();

        InterestService service = retrofit.create(InterestService.class);
        //final Interest i = new Interest();

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
                    System.out.println("entrou no else de listInterests()");

                    List<Interest> output = response.body();
                    //for(Interest i : output)
                        //output.add(i);

                    listInterests(output);
                    System.out.println("antes da chamada do retorno list " + interests.toString());
                }
            }

            @Override
            public void onFailure(Call<List<Interest>> call, Throwable t) {
                System.out.println("de falha no listIntesrests()");
                String aux = " Erro: " + t.getMessage();
                Log.e(TAG, aux);
                Toast.makeText(getApplicationContext(), aux, Toast.LENGTH_LONG).show();
            }
        });
    }

    /*private void registerInterests() {

        Gson g = new GsonBuilder().registerTypeAdapter(UserInterest.class, new InterestDeserializer())
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(UserInterestService.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(g))
                .build();

        UserInterestService service = retrofit.create(UserInterestService.class);


        for(int i = 0; i<interests.size(); i++) {
            final UserInterest userInterest = new UserInterest();
            //userInterest.setIdUser(<>);
            //userInterest.setIdInterest(<>);

            Call<Integer> requestUser = service.insert(userInterest);

        requestUser.enqueue(new Callback<Interest() {
            @Override
            public void onResponse(Call<Interest> call, Response<Interest> response) {
                String aux;
                if(!response.isSuccessful()) {
                    aux = "Erro: " + (response.code());
                    Log.i(TAG, aux);
                    Toast.makeText(getApplicationContext(), aux, Toast.LENGTH_LONG).show();
                }
                else {

                    //ESSA MENSAGEM TEM QUE SER PEGA PELO @STRINGS
                    Toast.makeText(getApplicationContext(), "Cadastrado com sucesso", Toast.LENGTH_LONG).show();
                    openHome();
                }
            }

            @Override
            public void onFailure(Call<Interest> call, Throwable t) {
                String aux = " Erro: " + t.getMessage();
                Log.e(TAG, aux);
                Toast.makeText(getApplicationContext(), aux, Toast.LENGTH_LONG).show();
            }
        });
        }
    }

    public void openHome(){
        Intent intent = new Intent(InterestsActivity.this, HomeActivity.class);
        startActivity(intent);
        finishAffinity();
    }*/
}
