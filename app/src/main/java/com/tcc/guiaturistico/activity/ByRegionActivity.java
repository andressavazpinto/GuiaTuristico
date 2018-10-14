package com.tcc.guiaturistico.activity;

import android.app.ExpandableListActivity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import adapter.CountryCityAdapter;
import model.Country;

public class ByRegionActivity extends ExpandableListActivity implements ExpandableListView.OnChildClickListener {
    List<Country> groupItem = new ArrayList<Country>();
    ArrayList<Object> childItem = new ArrayList<Object>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupComponents();
    }

    public void setupComponents() {
        ExpandableListView expandbleLis = getExpandableListView();
        expandbleLis.setDividerHeight(2);
        expandbleLis.setGroupIndicator(null);
        expandbleLis.setClickable(true);

        setData();

        CountryCityAdapter mCountryCityAdapter = new CountryCityAdapter(groupItem, childItem);
        mCountryCityAdapter.setInflater((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE),this);
        getExpandableListView().setAdapter(mCountryCityAdapter);
        expandbleLis.setOnChildClickListener(this);
    }

    public void setData() {

        Country co1 = new Country(1, "Alemanha", null);
        groupItem.add(co1);

        Country co2 = new Country(2, "Brazil", null);
        groupItem.add(co2);

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
}