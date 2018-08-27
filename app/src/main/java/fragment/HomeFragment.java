package fragment;

import android.app.ProgressDialog;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tcc.guiaturistico.R;

import model.ConnectGuides;
import model.ConnectGuidesDeserializer;
import model.Search;
import model.SearchDeserializer;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import service.ConnectGuidesService;
import service.SearchService;
import util.DBController;
import util.Message;
import util.StatusSearch;

public class HomeFragment extends Fragment {

    public Button buttonRamdom, buttonByRegion;
    public ProgressDialog progress;
    private DBController crud;
    private Search search;
    private Boolean found;
    private ConnectGuides connectGuides;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup v, Bundle b) {
        View view = inflater.inflate(R.layout.middle_home, v, false);
        crud = new DBController(getContext());
        search = new Search(1, Enum.valueOf(StatusSearch.class,"Initial"), crud.getUser().getIdUser());
        found = false;
        connectGuides = new ConnectGuides();
        setupComponents(view);
        setRetainInstance(true); //preservar a instância do fragment
        return view;
    }

    public void setFound(Boolean found) {
        this.found = found;
    }

    public void setupComponents(View view) {
        buttonByRegion = view.findViewById(R.id.buttonByRegion);
        buttonRamdom = view.findViewById(R.id.buttonRamdom);
        buttonRamdom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchRamdomly();
            }
        });
        buttonByRegion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchByRegion();
            }
        });

        progress = new ProgressDialog(fragment.HomeFragment.this.getContext());
        progress.setMessage(Message.searchingGuide);
        progress.setIndeterminate(true);
    }

    private void searchByRegion() {
    }

    private void searchRamdomly() {
        progress.show();
        search.setStatus(Enum.valueOf(StatusSearch.class,"Searching"));
        setStatus(search);
        searchRam(search);
    }

    public void setStatus(Search search) {

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

    public void searchRam(final Search search) {
        System.out.println("Resultado: Entrou no searchRam + " + search.toString());

        Gson g = new GsonBuilder().registerTypeAdapter(ConnectGuides.class, new ConnectGuidesDeserializer())
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ConnectGuidesService.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(g))
                .build();

        ConnectGuidesService service = retrofit.create(ConnectGuidesService.class);

        Call<ConnectGuides> requestSearchRam = service.searchRandomly(search);

        requestSearchRam.enqueue(new Callback<ConnectGuides>() {
            @Override
            public void onResponse(Call<ConnectGuides> call, Response<ConnectGuides> response) {
                if(!response.isSuccessful()) {
                    Log.i("TAG", "Falhou no searchRam: " + response.code());
                }
                else if(response.isSuccessful()) {
                    connectGuides = response.body();
                    System.out.println("Resultado do connectguides " + connectGuides.toString());
                    setFound(true);
                    if(found) {
                        search.setStatus(Enum.valueOf(StatusSearch.class, "Found"));
                        setStatus(new Search(2, (Enum.valueOf(StatusSearch.class, "Found")), connectGuides.getIdUser1()));
                        setStatus(new Search(2, (Enum.valueOf(StatusSearch.class, "Found")), connectGuides.getIdUser2()));
                        //setConnectGuides(search);
                        getActivity().recreate();
                    }
                    else {
                        progress.cancel();
                        Toast.makeText(getContext(), Message.noneGuide, Toast.LENGTH_LONG).show();
                    }
                    System.out.println("Resultado da busca: " + response.body());
                }
            }

            @Override
            public void onFailure(Call<ConnectGuides> call, Throwable t) {
                String aux = " Deu falha: " + t.getMessage();
                Log.e("TAG", aux);
                Toast.makeText(getContext(), aux, Toast.LENGTH_LONG).show();
            }
        });
        System.out.println("Resultado do searchRam: " + found);
    }
}
