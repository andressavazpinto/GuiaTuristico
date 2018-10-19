package com.tcc.guiaturistico.activity;

import android.app.ExpandableListActivity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import adapter.CountryCityAdapter;
import model.Search;
import model.SearchByRegion;
import model.SearchByRegionDeserializer;
import model.SearchDeserializer;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import service.SearchService;
import service.UserService;
import util.DBController;
import util.StatusSearch;

public class ByRegionActivity extends ExpandableListActivity implements ExpandableListView.OnChildClickListener {
    private static final String TAG = "ByRegionActivity";
    private DBController crud;
    private List<SearchByRegion> searchByRegions = new ArrayList<SearchByRegion>();

    ArrayList<String> groupItem = new ArrayList<String>();
    ArrayList<Object> childItem = new ArrayList<Object>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        crud = new DBController(this);
        getRegions();
        setupComponents();
    }

    public void setupComponents() {
        ExpandableListView expandbleListVew = getExpandableListView();
        expandbleListVew.setDividerHeight(2);
        expandbleListVew.setGroupIndicator(null);
        expandbleListVew.setClickable(true);

        setData();

        CountryCityAdapter countryCityAdapter = new CountryCityAdapter(groupItem, childItem);
        countryCityAdapter.setInflater((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE),this);
        getExpandableListView().setAdapter(countryCityAdapter);
        expandbleListVew.setOnChildClickListener(this);
    }

    public void setData() {
        groupItem.add("Alemanha");
        groupItem.add("Brazil");

        ArrayList<String> child;
        child = new ArrayList<String>();
        child.add("Cidade 1");
        child.add("Cidade 2");
        child.add("Cidade 3");
        child.add("Cidade 4");
        childItem.add(child);

        child = new ArrayList<String>();
        child.add("Salvador");
        child.add("São Paulo");
        childItem.add(child);
    }

    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
        Toast.makeText(this, "Clicked On Child", Toast.LENGTH_SHORT).show();
        return true;
    }

    public void getRegions() {
        Log.d(TAG, "Entrou no getRegions()");

        Gson g = new GsonBuilder().registerTypeAdapter(SearchByRegion.class, new SearchByRegionDeserializer())
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(UserService.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(g))
                .build();

        SearchService service = retrofit.create(SearchService.class);

        Call<List<SearchByRegion>> requestSearch = service.getRegions(crud.getUser().getIdUser());

        requestSearch.enqueue(new Callback<List<SearchByRegion>>() {
            @Override
            public void onResponse(@NonNull Call<List<SearchByRegion>> call, @NonNull Response<List<SearchByRegion>> response) {
                String aux;
                if(!response.isSuccessful()) {
                    aux = "Erro: " + (response.code());
                    Log.i(TAG, aux);
                }
                else {
                    searchByRegions = response.body();
                    Log.d(TAG, "searchByRegions: " + response.body().toString());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<SearchByRegion>> call, @NonNull Throwable t) {
                String aux = " Erro: " + t.getMessage();
                Log.e(TAG, aux);
                Toast.makeText(getApplicationContext(), aux, Toast.LENGTH_LONG).show();
            }
        });
    }
}