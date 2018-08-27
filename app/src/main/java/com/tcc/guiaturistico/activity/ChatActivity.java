package com.tcc.guiaturistico.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.tcc.guiaturistico.R;

import util.DBController;

public class ChatActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DBController crud;
    private ProgressBar spinner;
    private ConstraintLayout layout;
    private ConstraintLayout contentMain;
    private CoordinatorLayout appBar;
    public TextView nameNavHeader, localizationNavHeader;
    private View headerView;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private DrawerLayout drawer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        crud = new DBController(this);
        setupComponents();
    }

    public void setupComponents() {

        spinner = findViewById(R.id.progressBar);
        spinner.setVisibility(View.VISIBLE);

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        headerView = navigationView.getHeaderView(0);

        nameNavHeader = headerView.findViewById(R.id.nameNavHeader);
        nameNavHeader.setText(getIntent().getStringExtra("name")); //pegando o que foi passado pela activity anterior

        localizationNavHeader = headerView.findViewById(R.id.localizationNavHeader);
        localizationNavHeader.setText(getIntent().getStringExtra("localization"));

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        appBar = findViewById(R.id.appbarlayout);
        contentMain = appBar.findViewById(R.id.contentChat);
        contentMain.setVisibility(View.VISIBLE);
        //layout = contentMain.findViewById(R.id.fragHome);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_profile) {
            openProfile();
        } else if (id == R.id.nav_guide) {

        } else if (id == R.id.nav_settings) {

        } else if (id == R.id.nav_chats) {

        } else if (id == R.id.nav_changeInterests) {
            openChangeInterests();
        } else if (id == R.id.nav_logout) {
            //fechar sessão
            startActivity(new Intent(this, LoginActivity.class));  //O efeito ao ser pressionado do botão (no caso abre a activity)
            finishAffinity();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void openProfile(){
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
    }

    public void openChangeInterests(){
        Intent intent = new Intent(this, InterestsActivity.class);
        startActivity(intent);
    }
}
