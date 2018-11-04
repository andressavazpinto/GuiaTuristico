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

import java.util.ArrayList;
import java.util.List;

import adapter.CountryCityAdapter;
import model.SearchByRegion;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import service.SearchService;
import util.DBController;

public class ByRegionActivity extends ExpandableListActivity implements ExpandableListView.OnChildClickListener {
    private static final String TAG = "ByRegionActivity";
    private DBController crud;
    private ExpandableListView expandableListVew;
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
        expandableListVew = getExpandableListView();
        expandableListVew.setDividerHeight(2);
        expandableListVew.setGroupIndicator(null);
        expandableListVew.setClickable(true);
    }

    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
        return true;
    }

    public void getRegions() {
        Log.d(TAG, "Entrou no getRegions()");
        Log.d(TAG, "id user: " + crud.getUser().getIdUser());

        Gson g = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(SearchService.BASE_URL)
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
                    Log.d(TAG, "searchByRegions: " + response.body().toString());
                    searchByRegions = response.body();
                    listRegions(searchByRegions);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<SearchByRegion>> call, @NonNull Throwable t) {
                String aux = " Erro: " + t.getMessage();
                Log.e(TAG, aux);
                Toast.makeText(getApplicationContext(), aux, Toast.LENGTH_LONG).show();
                getRegions();
            }
        });
    }

    private void listRegions(List<SearchByRegion> searchByRegions) {

        ArrayList<SearchByRegion> child = new ArrayList<SearchByRegion>();

        for(int i=0; i<searchByRegions.size(); i++) {
            String country = searchByRegions.get(i).getCountry();

            if(groupItem.size() == 0) {
                groupItem.add(country);
            } else if(groupItem != null & ! country.equals(groupItem.get(groupItem.size()-1))) {
                groupItem.add(country);
                child = new ArrayList<SearchByRegion>();
            }

            child.add(searchByRegions.get(i));
            childItem.add(child);
        }

        CountryCityAdapter countryCityAdapter = new CountryCityAdapter(groupItem, childItem, crud);
        countryCityAdapter.setInflater((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE),this);
        getExpandableListView().setAdapter(countryCityAdapter);
        expandableListVew.setOnChildClickListener(this);
    }
}