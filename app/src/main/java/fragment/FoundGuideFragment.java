package fragment;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tcc.guiaturistico.R;

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

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup v, Bundle b) {
        View view = inflater.inflate(R.layout.middle_found_guide, v, false);
        crud = new DBController(getContext());
        search1 = new Search(0, null, crud.getUser().getIdUser());

        setupComponents(view);
        readConnectGuides(crud.getUser().getIdUser());

        setRetainInstance(true); //preservar a instância do fragment
        return view;
    }

    public void setupComponents(View view) {
        textViewName = view.findViewById(R.id.textViewName);
        buttonAccept = view.findViewById(R.id.buttonAccept);
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
                rejectGuide();
            }
        });
    }

    public void acceptGuide() {
        if(search2.getStatus() == Enum.valueOf(StatusSearch.class,"Found")) {
            search1.setStatus(Enum.valueOf(StatusSearch.class, "WaitingAnswer"));
            setStatusSearch(search1);

            getActivity().recreate();
        }
        else if(search2.getStatus() == Enum.valueOf(StatusSearch.class, "WaitingAnswer")) {
            search1.setStatus(Enum.valueOf(StatusSearch.class, "Accepted"));
            search2.setStatus(Enum.valueOf(StatusSearch.class, "Accepted"));
            setStatusSearch(search1);
            setStatusSearch(search2);

            registerChat();

            getActivity().recreate();
        }
    }

    public void rejectGuide() {
        search1.setStatus(Enum.valueOf(StatusSearch.class, "Initial"));
        search2.setStatus(Enum.valueOf(StatusSearch.class, "Rejected"));
        setStatusSearch(search1);
        setStatusSearch(search2);

        getActivity().recreate();
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

                    if(connectGuides == null) {
                        Log.i(TAG , "Resultado null do connectguides na parte de condições");
                    }
                    else if(connectGuides.getIdUser1() != crud.getUser().getIdUser()) {
                        getSearch(connectGuides.getIdUser1());
                    }
                    else {
                        getSearch(connectGuides.getIdUser2());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ConnectGuides> call, @NonNull Throwable t) {
                Log.e("erro", "Deu ruim: " + t.getMessage());
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
                String aux = " Deu falha no login: " + t.getMessage();
                Log.e("TAG", aux);
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
                    aux = "Deu falha no sucesso: " + (response.code());
                    Log.i("TAG", aux);
                }
                else if(response.isSuccessful()) {
                    System.out.print("Entrou no sucesso");
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                String aux = " Deu falha: " + t.getMessage();
                Log.e("TAG", aux);
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
                    try {textViewName.setText(guide.getName());} catch (Exception e) {Log.i(TAG, e.getMessage());}
                }
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                Log.e(TAG, "Erro: " + t.getMessage());
            }
        });
    }

    public void registerChat() {
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
        c.setIdChat(connectGuides.getIdUser2());
        c.setStatus(StatusChat.Active);

        Call<Integer> requestChat = service.register(c);

        requestChat.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(@NonNull Call<Integer> call, @NonNull Response<Integer> response) {
                if(response.isSuccessful()) {
                    if(response.body() != null)
                        Log.d(TAG, "idChat: " + response.body());
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
}
