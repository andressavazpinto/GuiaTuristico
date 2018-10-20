package fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
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
import com.tcc.guiaturistico.activity.ByRegionActivity;

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
import util.StatusSearch;

public class HomeFragment extends Fragment {
    private static final String TAG = "HomeFragment";
    public Button buttonRamdom, buttonByRegion;
    public ProgressDialog progress;
    private Search search;
    private ConnectGuides connectGuides;
    private DBController crud;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup v, Bundle b) {
        View view = inflater.inflate(R.layout.middle_home, v, false);
        crud = new DBController(getContext());
        search = new Search(0, null, crud.getUser().getIdUser());
        connectGuides = new ConnectGuides();
        setupComponents(view);
        setRetainInstance(true); //preservar a instância do fragment

        progress = new ProgressDialog(fragment.HomeFragment.this.getContext());
        progress.setMessage(getString(R.string.searchingGuide));
        progress.setIndeterminate(true);

        return view;
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


    }

    private void searchByRegion() {
        Intent intent = new Intent(getActivity(), ByRegionActivity.class);
        getActivity().startActivity(intent);
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
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                String aux;
                if(!response.isSuccessful()) {
                    aux = "Erro: " + (response.code());
                    Log.i(TAG, aux);
                    Toast.makeText(getContext(), aux, Toast.LENGTH_LONG).show();
                }
                else if(response.isSuccessful()) {
                    Log.d(TAG, "Status alterado para searching");
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                String aux = "Erro: " + t.getMessage();
                Log.e(TAG, aux);
            }
        });
    }

    public void searchRam(final Search search) {
        Log.d(TAG, "Resultado: Entrou no searchRam + " + search.toString());

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
            public void onResponse(@NonNull Call<ConnectGuides> call, @NonNull Response<ConnectGuides> response) {
                if(response.isSuccessful()) {
                    connectGuides = response.body();

                    if(connectGuides != null) {
                        search.setStatus(Enum.valueOf(StatusSearch.class, "Found"));
                        setStatus(new Search(0, (Enum.valueOf(StatusSearch.class, "Found")), connectGuides.getIdUser1()));
                        setStatus(new Search(0, (Enum.valueOf(StatusSearch.class, "Found")), connectGuides.getIdUser2()));

                        if (crud.getStatusSearch() != null) {
                            try {
                                crud.updateStatusSearch(search.getStatus().toString());
                            } catch (Exception e) {
                                Log.i(TAG, e.getMessage());
                            }
                        }

                        System.out.println("Resultado da busca: " + response.body());
                    }
                    else {
                        progress.cancel();
                        Toast.makeText(getContext(), getString(R.string.noneGuide), Toast.LENGTH_LONG).show();
                    }
                }
                else {
                    progress.cancel();
                    Toast.makeText(getContext(), getString(R.string.noneGuide), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ConnectGuides> call, @NonNull Throwable t) {
                String aux = "Erro: " + t.getMessage();
                Log.e(TAG, aux);
                Toast.makeText(getContext(), aux, Toast.LENGTH_LONG).show();
            }
        });
        progress.cancel();
    }
}
