//https://www.youtube.com/watch?v=bP9RYHKJzNs
package com.tcc.guiaturistico.activity;

import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tcc.guiaturistico.R;

import model.Usuario;
import model.UsuarioDes;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import service.UsuarioService;
import util.Status;

/**
 * Created by Andressa on 13/05/2018.
 */

public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = "blablabla";
    public EditText editTextName, editTextDateOfBirth, editTextUserEmail, editTextPassword, editTextOccupation, editTextLanguage, editTextLocalization;
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
        editTextUserEmail = (EditText) findViewById(R.id.editTextUserEmail);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        editTextOccupation = (EditText) findViewById(R.id.editTextOccupation);
        editTextLanguage = (EditText) findViewById(R.id.editTextLanguage);
        editTextLocalization = (EditText) findViewById(R.id.editTextLocalization);

        buttonRegister = findViewById(R.id.buttonContinue);
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                register();
            }
        });
    }

    public void register() {

        Gson g = new GsonBuilder().registerTypeAdapter(Usuario.class, new UsuarioDes())
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(UsuarioService.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(g))
                .build();

        UsuarioService service = retrofit.create(UsuarioService.class);

        final Usuario u = new Usuario();
        u.setNome(editTextName.getText().toString());

        String array[] = new String[3];
        array = editTextDateOfBirth.getText().toString().split("/");
        String aux = array[2]+"-"+array[1]+"-"+array[0];
        u.setDataNascimento(aux);

        u.setEmail(editTextUserEmail.getText().toString());
        u.setSenha(editTextPassword.getText().toString());
        u.setOcupacao(editTextOccupation.getText().toString());
        u.setIdioma(editTextLanguage.getText().toString());
        u.setLocalizacao(editTextLocalization.getText().toString());
        u.setStatusConta(Status.Ativo);

        Call<Integer> requestUsuario = service.cadastrar(u);

        requestUsuario.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                String aux;
                if(!response.isSuccessful()) {
                    aux = "Erro: " + (response.code());
                    Log.i(TAG, aux);
                    Toast.makeText(getApplicationContext(), aux, Toast.LENGTH_LONG).show();
                }
                else {

                    u.setIdUsuario(Integer.parseInt(response.body().toString()));
                    System.out.print("Id: " + u.getIdUsuario());
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
}




        //consulta de um usuário

        /*Call<Usuario> requestUsuario = service.consultar(3);
        requestUsuario.enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                if(!response.isSuccessful())
                    Log.i(TAG, "Deu erro: " + response.code());
                else {
                    Usuario u = response.body();

                    editTextName.setText(u.getNome());
                }
            }

            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {
                Log.e(TAG, "Deu ruim: " + t.getMessage());
            }
        });*/