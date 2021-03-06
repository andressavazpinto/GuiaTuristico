package fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tcc.guiaturistico.R;

import java.util.Timer;
import java.util.TimerTask;

import model.ConnectGuides;
import model.ConnectGuidesDeserializer;
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
import service.ConnectGuidesService;
import service.LocalizationService;
import service.SearchService;
import service.UserService;
import util.DBController;
import util.StatusSearch;

public class WaitingAnswerFragment extends Fragment {
    private static final String TAG = "WaitingAnswerFragment";
    private DBController crud;
    private TextView textViewName, textViewCurrently, textViewScore, textViewWaiting;
    private ImageView star;
    private Search search1, search2;
    private ConnectGuides connectGuides;
    private User guide, u;
    private ProgressBar spinner;
    private static final long TIME = (1000*5);
    private Timer timer;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup v, Bundle b) {
        View view = inflater.inflate(R.layout.middle_waiting, v, false);

        crud = new DBController(getContext());
        u = crud.getUser();
        search1 = new Search(0, null, crud.getUser().getIdUser());

        if(timer == null) {
            timer = new Timer();
            TimerTask tarefa = new TimerTask() {
                @Override
                public void run() {
                    try {
                        readConnectGuides(u.getIdUser());
                    } catch (Exception e) {
                        String aux = e.getMessage();
                        Log.d(TAG, aux);
                        Toast.makeText(getActivity(), aux, Toast.LENGTH_SHORT).show();
                    }
                }
            };
            timer.scheduleAtFixedRate(tarefa, TIME, TIME);
        }

        setupComponents(view);

        setRetainInstance(true); //preservar a instância do fragment
        return view;
    }

    public void setupComponents(View view) {
        spinner = view.findViewById(R.id.progressBar);
        textViewName = view.findViewById(R.id.textViewName);
        textViewCurrently = view.findViewById(R.id.textViewCurrently);
        LinearLayout linearLayout = view.findViewById(R.id.linearScore);
        textViewScore = linearLayout.findViewById(R.id.textViewScore);
        star = linearLayout.findViewById(R.id.imageViewStar);

        textViewWaiting = view.findViewById(R.id.textViewWaiting);
        textViewWaiting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                alert.setTitle(getString(R.string.cancelConnection));
                alert.setMessage(getString(R.string.reallyCancel));
                alert.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //desfazer conexão
                        deleteConnection(connectGuides);
                        cancelConnection();
                        dialog.dismiss();
                        spinner.setVisibility(View.VISIBLE);
                    }
                });

                alert.setNegativeButton(getString(R.string.noWait), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                alert.show();


            }
        });
    }

    public void readConnectGuides(int id) {
        Gson g = new GsonBuilder().registerTypeAdapter(ConnectGuides.class, new ConnectGuidesDeserializer())
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ConnectGuidesService.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(g))
                .build();

        ConnectGuidesService service = retrofit.create(ConnectGuidesService.class);

        Call<ConnectGuides> requestUser = service.read(id);
        requestUser.enqueue(new Callback<ConnectGuides>() {
            @Override
            public void onResponse(@NonNull Call<ConnectGuides> call, @NonNull Response<ConnectGuides> response) {
                if(!response.isSuccessful())
                    Log.i(TAG, "Erro: " + response.code());
                else {
                    connectGuides = response.body();

                    if(connectGuides != null) {
                        if (connectGuides.getIdUser1() != crud.getUser().getIdUser())
                            getSearch(connectGuides.getIdUser1());
                        else {
                            getSearch(connectGuides.getIdUser2());
                        }
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ConnectGuides> call, @NonNull Throwable t) {
                Log.e(TAG, "Erro: " + t.getMessage());
            }
        });
    }

    public void getSearch(int id) {
        Log.i(TAG, "Resultado do que é o id enviado no getSearch: " + id);
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
                else {
                    search2 = response.body();
                    readUser(search2.getIdUser());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Search> call, @NonNull Throwable t) {
                String aux = "Erro: " + t.getMessage();
                Log.e(TAG, aux);
            }
        });
    }

    public void setStatusSearch(Search search) {
        Gson g = new GsonBuilder().registerTypeAdapter(Search.class, new SearchDeserializer())
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(SearchService.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(g))
                .build();

        SearchService service = retrofit.create(SearchService.class);

        Call<Void> requestSearch = service.update(search);

        requestSearch.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                String aux;
                if(!response.isSuccessful()) {
                    aux = "Erro: " + (response.code());
                    Log.i(TAG, aux);
                }
                else if(response.isSuccessful()) {
                    System.out.print("Entrou no sucesso");
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                String aux = "Erro: " + t.getMessage();
                Log.e(TAG, aux);
            }
        });
    }

    public void readUser(int id) {
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
                    guide = response.body();
                    getLocalization(guide);
                }
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                Log.e(TAG, "Erro: " + t.getMessage());
            }
        });
    }

    public void cancelConnection() {
        //setar o usuário daqui como Searching e o outro como Rejected
        search1.setStatus(Enum.valueOf(StatusSearch.class, "Searching"));
        setStatusSearch(search1);

        if(crud.getStatusSearch() != null)
            try{crud.updateStatusSearch(search1.getStatus().toString());} catch(Exception e){Log.i(TAG, e.getMessage());}
        else
            try{crud.insertStatusSearch(search1.getStatus().toString());} catch(Exception e){Log.i(TAG, e.getMessage());}

        search2.setStatus(Enum.valueOf(StatusSearch.class, "Rejected"));
        setStatusSearch(search2);
    }

    public void deleteConnection(ConnectGuides connectGuides) {
        Gson g = new GsonBuilder().registerTypeAdapter(ConnectGuides.class, new ConnectGuidesDeserializer())
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ConnectGuidesService.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(g))
                .build();

        ConnectGuidesService service = retrofit.create(ConnectGuidesService.class);

        Call<Void> requestUser = service.delete(connectGuides.getIdConnectGuides());
        requestUser.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if(!response.isSuccessful())
                    Log.i(TAG, "Erro: " + response.code());
                else {
                    Log.i(TAG, "Erro: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                Log.e(TAG, "Erro: " + t.getMessage());
            }
        });
    }

    public void getLocalization(User guide) {
        final User userAux = guide;
        Gson g = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(LocalizationService.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(g))
                .build();

        LocalizationService service = retrofit.create(LocalizationService.class);

        Call<Localization> requestLocalization = service.read(guide.getIdLocalization());

        requestLocalization.enqueue(new Callback<Localization>() {
            @Override
            public void onResponse(@NonNull Call<Localization> call, @NonNull Response<Localization> response) {
                if (response.isSuccessful()) {
                    Localization loc = response.body();
                    if(getActivity() != null) {
                        textViewName.setText(userAux.getName());
                        textViewCurrently.setText(getString(R.string.currently) + " " + loc.getCity() + ", " + loc.getCountry());
                    }

                    String aux = userAux.getScoreS();
                    double score = userAux.getScore();
                    if(score != 0.00) {
                        textViewScore.setText(aux);
                        star.setVisibility(View.VISIBLE);
                    }
                } else {
                    Log.i(TAG, "Erro: " + (response.code()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<Localization> call, @NonNull Throwable t) {
                Log.e(TAG, "Erro: " + t.getMessage());
                getLocalization(userAux);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        readConnectGuides(u.getIdUser());
    }
}
