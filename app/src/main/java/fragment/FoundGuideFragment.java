package fragment;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

import model.Chat;
import model.ChatDeserializer;
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
import service.ChatService;
import service.ConnectGuidesService;
import service.LocalizationService;
import service.SearchService;
import service.UserService;
import util.DBController;
import util.StatusChat;
import util.StatusSearch;

public class FoundGuideFragment extends Fragment {
    private static final String TAG = "FoundGuideFragment";
    private Button buttonAccept, buttonReject;
    private TextView textViewName, textViewCurrently, textViewScore;
    private ImageView star;
    private DBController crud;
    private Search search1, search2;
    private ConnectGuides connectGuides;
    private User guide, u;
    private Localization loc;
    private ProgressBar spinner;
    private static final long TIME = (1000*5);
    private Timer timer;
    private String statusSearch;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup v, Bundle b) {
        final View view = inflater.inflate(R.layout.middle_found_guide, v, false);
        crud = new DBController(getContext());
        u = crud.getUser();
        statusSearch = crud.getStatusSearch();

        search1 = new Search(0, null, u.getIdUser());


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

        setRetainInstance(true); //preservar a instância do fragment
        setupComponents(view);
        return view;
    }

    public void setupComponents(View view) {
        spinner = view.findViewById(R.id.progressBar);
        textViewName = view.findViewById(R.id.textViewName);
        textViewCurrently = view.findViewById(R.id.textViewCurrently);
        LinearLayout linearLayout = view.findViewById(R.id.linearScore);
        textViewScore = linearLayout.findViewById(R.id.textViewScore);
        star = linearLayout.findViewById(R.id.imageViewStar);

        buttonAccept = view.findViewById(R.id.buttonAccept);
        buttonReject = view.findViewById(R.id.buttonReject);
        buttonAccept.setVisibility(View.VISIBLE);
        buttonReject.setVisibility(View.VISIBLE);
        buttonAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                acceptGuide();
            }
        });
        buttonReject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reject(new ConnectGuides (connectGuides.getIdConnectGuides(), search1.getIdUser(), guide.getIdUser(), null));
            }
        });
    }

    public void acceptGuide() {
        spinner.setVisibility(View.VISIBLE);

        if(search2 == null) {
            if (connectGuides.getIdUser1() != u.getIdUser())
                getSearch(connectGuides.getIdUser1());
            else {
                getSearch(connectGuides.getIdUser2());
            }
        }
        else if(search2.getStatus() == Enum.valueOf(StatusSearch.class,"Found")) {
            search1.setStatus(Enum.valueOf(StatusSearch.class, "WaitingAnswer"));
            setStatusSearch(search1);

            Log.i(TAG, "search2 dentro: " + search2.toString());

            if (crud.getStatusSearch() != null) {
                try { crud.updateStatusSearch(search1.getStatus().toString()); } catch (Exception e) { Log.i(TAG, e.getMessage()); }
            }
            try { this.finalize(); } catch (Throwable throwable) { throwable.printStackTrace(); }
        }
        else if(search2.getStatus() == Enum.valueOf(StatusSearch.class, "WaitingAnswer")) {
            search1.setStatus(Enum.valueOf(StatusSearch.class, "Accepted"));
            search2.setStatus(Enum.valueOf(StatusSearch.class, "Accepted"));
            setStatusSearch(search1);
            setStatusSearch(search2);

            if (crud.getStatusSearch() != null)
                try{crud.updateStatusSearch(search1.getStatus().toString());} catch(Exception e){Log.i(TAG, e.getMessage());}
            else
                try{crud.insertStatusSearch(search1.getStatus().toString());} catch(Exception e){Log.i(TAG, e.getMessage());}

            registerChat();
        }
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
                        if (connectGuides.getIdUser1() != u.getIdUser())
                            readUser(connectGuides.getIdUser1());
                        else
                            readUser(connectGuides.getIdUser2());
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
                    getLocalization(u.getIdLocalization(), search2);
                    acceptGuide();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Search> call, @NonNull Throwable t) {
                String aux = "Erro: " + t.getMessage();
                Log.e(TAG, aux);
            }
        });
    }

    public void setStatusSearch(final Search search) {
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
                    Log.d(TAG, "Este foi o search alterado: " + search.toString());
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

    public void registerChat() {
        Log.d(TAG, "dentro do register chat: " + connectGuides.toString());
        Gson g = new GsonBuilder().registerTypeAdapter(Chat.class, new ChatDeserializer())
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ChatService.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(g))
                .build();

        ChatService service = retrofit.create(ChatService.class);

        final Chat c = new Chat();

        c.setIdUser1(connectGuides.getIdUser1());
        c.setIdUser2(connectGuides.getIdUser2());
        c.setStatus(StatusChat.Active);

        Log.d(TAG, "Chat: " + c.toString());

        Call<Integer> requestChat = service.register(c);

        requestChat.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(@NonNull Call<Integer> call, @NonNull Response<Integer> response) {
                if(response.isSuccessful()) {
                    if(response.body() != null) {
                        //não faz nada, pq a condição está na home
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<Integer> call, @NonNull Throwable t) {
                String aux = " Erro: " + t.getMessage();
                Log.e(TAG, aux);
                Toast.makeText(getContext(), aux, Toast.LENGTH_LONG).show();
            }
        });
    }

    public void reject(ConnectGuides cg) {
        spinner.setVisibility(View.VISIBLE);
        final ConnectGuides cg2 = cg;

        Gson g = new GsonBuilder().registerTypeAdapter(ConnectGuides.class, new ConnectGuidesDeserializer())
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ConnectGuidesService.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(g))
                .build();

        ConnectGuidesService service = retrofit.create(ConnectGuidesService.class);

        Call<Void> requestConnect = service.reject(cg);
        requestConnect.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if(!response.isSuccessful()) {
                    Log.i(TAG, "Erro: " + response.code());
                    Log.i(TAG, "Erro: " + response.body());
                    spinner.setVisibility(View.GONE);
                }
                else {
                    if (statusSearch != null) {
                        try {
                            crud.updateStatusSearch("Searching");
                        } catch (Exception e) {
                            Log.i(TAG, e.getMessage());
                        }
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                Log.e(TAG, "Erro: " + t.getMessage());
                reject(cg2);
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

                    Activity activity = getActivity();

                    if(activity != null) {
                        textViewName.setText(userAux.getName());
                        textViewName.setVisibility(View.VISIBLE);
                        textViewCurrently.setText(getString(R.string.currently) + " " + loc.getCity() + ", " + loc.getUf());
                        textViewCurrently.setVisibility(View.VISIBLE);
                    }

                    String aux = userAux.getScoreS();
                    double score = userAux.getScore();
                    if(score != 0.00) {
                        textViewScore.setText(aux);
                        star.setVisibility(View.VISIBLE);
                    }
                    spinner.setVisibility(View.GONE);
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
                    loc = response.body();
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
