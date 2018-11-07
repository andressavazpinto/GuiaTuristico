package com.tcc.guiaturistico.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tcc.guiaturistico.R;

import model.Grade;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import service.UserService;

public class DialogScore extends DialogFragment {
    private static final String TAG = "DialogScore";

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(R.layout.dialog_score)
                .setTitle(R.string.evaluateGuide)
                .setPositiveButton(R.string.send, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Grade grade = new Grade(3);
                        sendGrade((Integer) getArguments().get("idUser"), grade);
                        Intent intent = new Intent(getActivity(), HomeActivity.class);
                        startActivity(intent);
                        getActivity().finish();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(getActivity(), HomeActivity.class);
                        startActivity(intent);
                        getActivity().finish();
                    }
                });

        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View view = inflater.inflate(R.layout.dialog_score, null, false);
        final LinearLayout linearLayout = view.findViewById(R.id.linearScore);

        //final Drawable drawable1= getResources().getDrawable(R.drawable.ic_star_border);
        final ImageView star0 = linearLayout.findViewById(R.id.imageViewStarBorder1);
        star0.setVisibility(View.VISIBLE);


        //final ImageView imageView1 = new ImageButton(getActivity());
        //imageView1.setImageDrawable(drawable1);
        //linearLayout.addView(imageView1);
        // linearLayout.findViewById(R.id.imageViewStar1);

        //final Drawable drawable= getResources().getDrawable(R.drawable.ic_star);

        star0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.print("cliquei na estrela");
                star0.setVisibility(View.GONE);



                /*linearLayout.removeView(imageView1);
                imageView1.setImageDrawable(drawable);
                linearLayout.addView(imageView1);*/
            }
        });

        return builder.create();
    }

    public void sendGrade(int idUser, Grade grade) {

        Gson g = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(UserService.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(g))
                .build();

        UserService service = retrofit.create(UserService.class);

        Call<Void> requestUser = service.updateScore(idUser, grade);

        requestUser.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.i(TAG, "Sucesso");
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                String aux = "Erro: " + t.getMessage();
                Log.e(TAG, aux);
                Toast.makeText(getActivity(), aux, Toast.LENGTH_LONG).show();
            }
        });
    }
}
