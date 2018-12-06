package com.tcc.guiaturistico.activity;

import android.app.AlertDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tcc.guiaturistico.R;

import model.User;
import model.UserDeserializer;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import service.MailService;
import service.UserService;

/**
 * Created by Andressa on 31/03/2018.
 */

public class DialogForgotPass {
    private static final String TAG = "DialogForgotPass";

    private AlertDialog dialogForgot;
    private AppCompatActivity context;
    private ProgressBar spinner;
    private EditText editTextEmail;

    public DialogForgotPass(AppCompatActivity context){
        this.context = context;
    }

    public void showLayoutDialog() {
        View passView = context.getLayoutInflater().inflate(R.layout.dialog_forgotpass, null, false);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(passView);
        dialogForgot = builder.create();
        dialogForgot.setTitle(R.string.forgetPass);

        LinearLayout linearLayout = passView.findViewById(R.id.linear_texts);
        LinearLayout linearLayout2 = passView.findViewById(R.id.linear_buttons);

        editTextEmail = linearLayout.findViewById(R.id.editTextUserEmail);

        TextView buttonCancel = linearLayout2.findViewById(R.id.button_cancel);
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogForgot.dismiss();
            }
        });

        TextView buttonSend = linearLayout2.findViewById(R.id.button_send);
        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendPass();
            }
        });

        spinner = passView.findViewById(R.id.progressBar);

        dialogForgot.show();
    }

    public void checkEmail(final String email) {

        Gson g = new GsonBuilder().registerTypeAdapter(User.class, new UserDeserializer())
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
                if (response.isSuccessful()) {
                    if(response.body())
                        generatePass(email);
                    else {
                        spinner.setVisibility(View.GONE);
                        Toast.makeText(context, R.string.noneEmail, Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<Boolean> call, @NonNull Throwable t) {
                String aux = "Erro: " + t.getMessage();
                Log.e(TAG, aux);
                spinner.setVisibility(View.GONE);
                Toast.makeText(context, aux, Toast.LENGTH_LONG).show();
            }
        });
    }

    public void generatePass(String email) {
        Gson g = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(UserService.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(g))
                .build();

        MailService service = retrofit.create(MailService.class);

        Call<Void> requestMail = service.generatePass(email);

        requestMail.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(context, R.string.checkEmail, Toast.LENGTH_LONG).show();
                    spinner.setVisibility(View.GONE);
                    dialogForgot.dismiss();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                String aux = "Erro: " + t.getMessage();
                Log.e(TAG, aux);
                spinner.setVisibility(View.GONE);
                Toast.makeText(context, aux, Toast.LENGTH_LONG).show();
            }
        });
    }

    public void sendPass() {
        String email = editTextEmail.getText().toString();

        if(email.length() == 0)
            Toast.makeText(context, R.string.enterEmail, Toast.LENGTH_LONG).show();
        else {
            spinner.setVisibility(View.VISIBLE);
            checkEmail(email);
        }
    }
}
