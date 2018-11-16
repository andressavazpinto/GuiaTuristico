package com.tcc.guiaturistico.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tcc.guiaturistico.R;

import org.w3c.dom.Text;

import model.Grade;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import service.UserService;
import util.DBController;

public class DialogScore {
    private static final String TAG = "DialogScore";

    private AlertDialog mScoreDialog;
    private AppCompatActivity mContext;
    private View mScoreView;
    private double mGrade = 0.1;
    private Activity activity;

    public DialogScore(AppCompatActivity context, Activity activity){
        this.activity = activity;
        mContext = context;
    }

    public void showLayoutScore(final int idUser, int idChat, boolean disconnected){
        DBController crud = new DBController(mContext);
        try { crud.deleteChat(idChat); } catch (Exception e) { Log.i(TAG, e.getMessage()); }

        //Adicionando o Layout a View que será inserida no AlertDialog
        mScoreView = mContext.getLayoutInflater().inflate(R.layout.dialog_score, null, false);
        //Instanciando o AlertDialog - Inserindo a View nele - Criando o AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setView(mScoreView);

        mScoreDialog = builder.create();

        if(disconnected) {
            TextView textViewDisconnected = mScoreView.findViewById(R.id.disconnected);
            textViewDisconnected.setVisibility(View.VISIBLE);
            mScoreDialog.setTitle(R.string.disconnected);
        }
        else
            mScoreDialog.setTitle(R.string.evaluateGuide);

        LinearLayout linearLayout = mScoreView.findViewById(R.id.linearScore);

        final ImageView starBorder0 = linearLayout.findViewById(R.id.imageViewStarBorder1);
        final ImageView starBorder1 = linearLayout.findViewById(R.id.imageViewStarBorder2);
        final ImageView starBorder2 = linearLayout.findViewById(R.id.imageViewStarBorder3);
        final ImageView starBorder3 = linearLayout.findViewById(R.id.imageViewStarBorder4);
        final ImageView starBorder4 = linearLayout.findViewById(R.id.imageViewStarBorder5);

        final Drawable starBorder = mContext.getDrawable(R.drawable.ic_star_border);
        final Drawable star = mContext.getDrawable(R.drawable.ic_star);

        starBorder0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                starBorder0.setImageDrawable(star);
                starBorder1.setImageDrawable(starBorder);
                starBorder2.setImageDrawable(starBorder);
                starBorder3.setImageDrawable(starBorder);
                starBorder4.setImageDrawable(starBorder);
                mGrade = 1;
            }
        });

        starBorder1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                starBorder0.setImageDrawable(star);
                starBorder1.setImageDrawable(star);
                starBorder2.setImageDrawable(starBorder);
                starBorder3.setImageDrawable(starBorder);
                starBorder4.setImageDrawable(starBorder);
                mGrade = 2;
            }
        });

        starBorder2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                starBorder0.setImageDrawable(star);
                starBorder1.setImageDrawable(star);
                starBorder2.setImageDrawable(star);
                starBorder3.setImageDrawable(starBorder);
                starBorder4.setImageDrawable(starBorder);
                mGrade = 3;
            }
        });

        starBorder3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                starBorder0.setImageDrawable(star);
                starBorder1.setImageDrawable(star);
                starBorder2.setImageDrawable(star);
                starBorder3.setImageDrawable(star);
                starBorder4.setImageDrawable(starBorder);
                mGrade = 4;
            }
        });

        starBorder4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                starBorder0.setImageDrawable(star);
                starBorder1.setImageDrawable(star);
                starBorder2.setImageDrawable(star);
                starBorder3.setImageDrawable(star);
                starBorder4.setImageDrawable(star);
                mGrade = 5;
            }
        });

        LinearLayout linearLayout2 = mScoreView.findViewById(R.id.linearScore_buttons);

        TextView buttonCancel = linearLayout2.findViewById(R.id.button_cancel);
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mScoreDialog.dismiss();

                activity.startActivity(new Intent(activity.getApplicationContext(), HomeActivity.class));
                activity.finish();
            }
        });

        TextView buttonSend = linearLayout2.findViewById(R.id.button_send);
        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Grade gradeG = new Grade(mGrade);
                sendGrade(idUser, gradeG);
                mScoreDialog.dismiss();

                activity.startActivity(new Intent(activity.getApplicationContext(), HomeActivity.class));
                activity.finish();
            }
        });

        mScoreDialog.show();
    }

    private void sendGrade(int idUser, Grade grade) {

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
                Toast.makeText(mContext, aux, Toast.LENGTH_LONG).show();
            }
        });
    }


}
