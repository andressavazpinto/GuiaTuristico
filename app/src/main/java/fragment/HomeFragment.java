package fragment;

import android.app.Activity;
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

import org.json.JSONException;
import org.json.JSONObject;

import model.ConnectGuides;
import model.Search;
import model.SearchDeserializer;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import service.ConnectGuidesService;
import service.SearchService;
import service.UserService;
import util.DBController;
import util.Message;
import util.StatusSearch;

public class HomeFragment extends Fragment {

    public Button buttonRamdom, buttonByRegion;
    private DBController crud;
    private Search search;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup v, Bundle b) {
        View view = inflater.inflate(R.layout.middle_home, v, false);
        crud = new DBController(getContext());
        search = new Search(1, Enum.valueOf(StatusSearch.class,"Initial"), crud.getUser().getIdUser());
        setupComponents(view);
        setRetainInstance(true); //preservar a instância do fragment
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
    }

    private void searchRamdomly() {
        ProgressDialog progress = new ProgressDialog(fragment.HomeFragment.this.getContext());
        progress.setMessage(Message.searchingGuide);
        progress.setIndeterminate(true);
        progress.show();

        setStatus("Searching");

        //DIGAMOS QUE ENCONTROU O GUIA - MÉTODO A FAZER (fazer um try ou if?)
        if(searchRam(search)) {
            setStatus("Found");
            setConnectGuides(search);
            getActivity().recreate();
        }
        else {
            progress.cancel();
            Toast.makeText(getContext(), Message.noneGuide, Toast.LENGTH_LONG).show();
        }
    }

    public void setStatus(String status) {
        search.setStatus(Enum.valueOf(StatusSearch.class,status));

        Gson g = new GsonBuilder().registerTypeAdapter(Search.class, new SearchDeserializer())
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(UserService.BASE_URL)
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
                    Toast.makeText(getContext(), aux, Toast.LENGTH_LONG).show();
                }
                else if(response.isSuccessful()) {
                    System.out.print("Entrou no sucesso");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                String aux = " Deu falha: " + t.getMessage();
                Log.e("TAG", aux);
                Toast.makeText(getContext(), aux, Toast.LENGTH_LONG).show();
            }
        });
    }

    public void setConnectGuides(Search search) {

        Gson g = new GsonBuilder().registerTypeAdapter(ConnectGuides.class, new SearchDeserializer())
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(UserService.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(g))
                .build();

        ConnectGuidesService service = retrofit.create(ConnectGuidesService.class);

        Call<Void> requestSearch = service.update(search);

        requestSearch.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                String aux;
                if(!response.isSuccessful()) {
                    aux = "Deu falha no sucesso: " + (response.code());
                    Log.i("TAG", aux);
                    Toast.makeText(getContext(), aux, Toast.LENGTH_LONG).show();
                }
                else if(response.isSuccessful()) {
                    System.out.print("Entrou no sucesso");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                String aux = " Deu falha: " + t.getMessage();
                Log.e("TAG", aux);
                Toast.makeText(getContext(), aux, Toast.LENGTH_LONG).show();
            }
        });
    }

    public boolean searchRam(Search search) {

        final boolean[] out = {false};

        Gson g = new GsonBuilder().registerTypeAdapter(ConnectGuides.class, new SearchDeserializer())
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(UserService.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(g))
                .build();

        ConnectGuidesService service = retrofit.create(ConnectGuidesService.class);

        Call<Boolean> requestSearch = service.searchRandomly(search);

        requestSearch.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                String aux;
                if(!response.isSuccessful()) {
                    Log.i("TAG", "Falhou no searchRam" + response.code());
                }
                else if(response.isSuccessful()) {
                    out[0] = response.body();
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                String aux = " Deu falha: " + t.getMessage();
                Log.e("TAG", aux);
                Toast.makeText(getContext(), aux, Toast.LENGTH_LONG).show();
            }
        });
        return out[0];
    }
}
