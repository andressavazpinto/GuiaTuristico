package fragment;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tcc.guiaturistico.R;
import com.tcc.guiaturistico.activity.ChatActivity;

import model.Chat;
import model.ChatDeserializer;
import model.ConnectGuides;
import model.ConnectGuidesDeserializer;
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
import service.SearchService;
import service.UserService;
import util.DBController;
import util.StatusChat;
import util.StatusSearch;

public class FoundGuideFragment extends Fragment {
    private static final String TAG = "FoundGuideFragment";
    public Button buttonAccept, buttonReject;
    public TextView textViewName;
    private DBController crud;
    private Search search1, search2;
    private ConnectGuides connectGuides;
    private User guide;
    private ProgressBar spinner;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup v, Bundle b) {
        View view = inflater.inflate(R.layout.middle_found_guide, v, false);
        crud = new DBController(getContext());
        search1 = new Search(0, null, crud.getUser().getIdUser());

        Log.i(TAG, "antes de chamar o readConnectGuides");
        readConnectGuides(crud.getUser().getIdUser());

        setRetainInstance(true); //preservar a instância do fragment
        setupComponents(view);
        return view;
    }

    public void setupComponents(View view) {
        spinner = view.findViewById(R.id.progressBar);
        textViewName = view.findViewById(R.id.textViewName);
        buttonAccept =view.findViewById(R.id.buttonAccept);
        buttonReject = view.findViewById(R.id.buttonReject);
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
            if (connectGuides.getIdUser1() != crud.getUser().getIdUser())
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

            Log.d(TAG, "chamou register chat: " + connectGuides.toString());
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
                        Log.d(TAG, "connectGuides: " + response.body());
                        Log.i(TAG, "Antes de chamar o readUser: ");

                        if (connectGuides.getIdUser1() != crud.getUser().getIdUser())
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
                    Log.i(TAG, "chamar de novo o acceptGuide");
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
        Log.i(TAG, "Dentro do readUser: ");
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
                    textViewName.setText(guide.getName());
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
                        Log.d(TAG, "idChat: " + response.body());
                        Intent intent = new Intent(getActivity(), ChatActivity.class);
                        startActivity(intent);
                        getActivity().finishAffinity();
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
                    if (crud.getStatusSearch() != null) {
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
}
