package fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tcc.guiaturistico.R;

import model.Search;
import model.SearchDeserializer;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import service.SearchService;
import util.DBController;
import util.Message;
import util.StatusSearch;

public class RejectedFragment extends Fragment {

    public Button buttonRamdom, buttonByRegion;
    private DBController crud;
    private Search search1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup v, Bundle b) {
        View view = inflater.inflate(R.layout.middle_rejected, v, false);

        crud = new DBController(getContext());
        search1 = new Search(1, Enum.valueOf(StatusSearch.class,"Initial"), crud.getUser().getIdUser());

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
        ProgressDialog progress = new ProgressDialog(fragment.RejectedFragment.this.getContext());
        progress.setMessage(Message.searchingGuide);
        progress.setIndeterminate(true);
        progress.show();

        search1.setStatus(Enum.valueOf(StatusSearch.class, "Initial"));
        setStatusSearch(search1);
        progress.cancel();
        //getActivity().recreate();
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
}
