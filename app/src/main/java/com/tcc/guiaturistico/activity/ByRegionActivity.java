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

import adapter.ByRegionAdapter;
import model.SearchByRegion;
import model.UserInterest;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import service.InterestService;
import service.SearchService;
import service.UserInterestService;
import util.DBController;

public class ByRegionActivity extends ExpandableListActivity implements ExpandableListView.OnChildClickListener {
    private static final String TAG = "ByRegionActivity";
    private DBController crud;
    private ExpandableListView expandableListView;
    private List<SearchByRegion> searchByRegions = new ArrayList<SearchByRegion>();

    ArrayList listInterests = new ArrayList();
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
        expandableListView = getExpandableListView();
        expandableListView.setDividerHeight(2);
        expandableListView.setGroupIndicator(null);
        expandableListView.setClickable(true);
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
                childItem.add(child);
                child = new ArrayList<SearchByRegion>();
            }

            readUserInterests(searchByRegions.get(i).getIdUser());
            child.add(searchByRegions.get(i));
        }
        childItem.add(child);
        setAdapter();
    }

    public void setAdapter() {
        ByRegionAdapter byRegionAdapter = new ByRegionAdapter(groupItem, childItem, crud, listInterests, expandableListView);
        byRegionAdapter.setInflater((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE),this);
        getExpandableListView().setAdapter(byRegionAdapter);
        expandableListView.setOnChildClickListener(this);
    }

    public void readUserInterests(int idUser) {
        Gson g = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(InterestService.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(g))
                .build();

        UserInterestService service = retrofit.create(UserInterestService.class);

        Call<List<UserInterest>> requestInterest = service.listByUser(idUser);

        requestInterest.enqueue(new Callback<List<UserInterest>>() {
            @Override
            public void onResponse(@NonNull Call<List<UserInterest>> call, @NonNull Response<List<UserInterest>> response) {
                if (!response.isSuccessful()) {
                    String aux = "Erro: " + (response.code());
                    Log.i(TAG, aux);
                } else {
                    List<UserInterest> output = response.body();

                    listInterests.add(output);
                    if (output.size() > 0) {

                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<UserInterest>> call, @NonNull Throwable t) {
                String aux = " Erro: " + t.getMessage();
                Log.e(TAG, aux);
                Toast.makeText(getApplicationContext(), aux, Toast.LENGTH_LONG).show();
            }
        });
    }
}