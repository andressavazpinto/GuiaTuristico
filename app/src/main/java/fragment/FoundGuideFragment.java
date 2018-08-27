package fragment;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tcc.guiaturistico.R;

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
import service.ConnectGuidesService;
import service.SearchService;
import service.UserService;
import util.DBController;
import util.StatusSearch;

public class FoundGuideFragment extends Fragment {

    public Button buttonAccept, buttonReject;
    public TextView textViewName;
    private DBController crud;
    private Search search1, search2;
    private ConnectGuides connectGuides;
    private User guide;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup v, Bundle b) {
        View view = inflater.inflate(R.layout.middle_found_guide, v, false);
        crud = new DBController(getContext());
        search1 = new Search(1, Enum.valueOf(StatusSearch.class,"Initial"), crud.getUser().getIdUser());

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
            public void onResponse(Call<ConnectGuides> call, Response<ConnectGuides> response) {
                if(!response.isSuccessful())
                    Log.i("erro", "Deu erro: " + response.code());
                else {
                    connectGuides = response.body();
                    //System.out.println("Resultado readConnectGuides" + connectGuides.toString());

                    if(connectGuides == null) {
                        System.out.print("Resultado null do connectguides na parte de condições");
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
            public void onFailure(Call<ConnectGuides> call, Throwable t) {
                Log.e("erro", "Deu ruim: " + t.getMessage());
            }
        });
    }

    public void getSearch(int id) {
        System.out.println("Resultado do que é o id enviado no getSearch: " + id);
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
            public void onResponse(Call<Search> call, Response<Search> response) {
                String aux;
                if(!response.isSuccessful()) {
                    aux = "Deu falha no sucesso: " + (response.code());
                    Log.i("TAG", aux);
                }
                else {
                    search2 = response.body();
                    //System.out.println("Resultado do status em FoundGuideFragment: " + search2.toString());
                    readUser(search2.getIdUser());
                }
            }

            @Override
            public void onFailure(Call<Search> call, Throwable t) {
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
            public void onResponse(Call<Void> call, Response<Void> response) {
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
            public void onFailure(Call<Void> call, Throwable t) {
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
            public void onResponse(Call<User> call, Response<User> response) {
                if(!response.isSuccessful())
                    Log.i("erro", "Deu erro: " + response.code());
                else {
                    guide = response.body();
                    textViewName.setText(guide.getName());
                    //System.out.println("Resultado do Guide.toString: " + response.body().toString());
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e("erro", "Deu ruim: " + t.getMessage());
            }
        });
    }
}
