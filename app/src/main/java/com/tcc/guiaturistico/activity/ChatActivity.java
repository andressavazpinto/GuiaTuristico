//https://www.youtube.com/watch?v=U0xcNBg3Lhw até 4'
//https://www.youtube.com/watch?v=wVCz1a3ogqk ver a partir do 6'
package com.tcc.guiaturistico.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tcc.guiaturistico.R;

import java.util.ArrayList;
import java.util.List;

import adapter.ChatAdapter;
import model.Message;

public class ChatActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private ProgressBar spinner;
    public TextView nameNavHeader, localizationNavHeader;
    ConstraintLayout contentMain;
    FirebaseDatabase database;
    List<Message> list;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        database = FirebaseDatabase.getInstance();
        list = new ArrayList<Message>();
        setupComponents();
    }

    public void setupComponents() {

        spinner = findViewById(R.id.progressBar);
        spinner.setVisibility(View.VISIBLE);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);

        nameNavHeader = headerView.findViewById(R.id.nameNavHeader);
        nameNavHeader.setText(getIntent().getStringExtra("name")); //pegando o que foi passado pela activity anterior

        localizationNavHeader = headerView.findViewById(R.id.localizationNavHeader);
        localizationNavHeader.setText(getIntent().getStringExtra("localization"));

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        CoordinatorLayout appBar = findViewById(R.id.appbarlayout);
        contentMain = appBar.findViewById(R.id.contentChat);
        ConstraintLayout otherContent = appBar.findViewById(R.id.contentMain);
        otherContent.setVisibility(View.GONE);
        contentMain.setVisibility(View.VISIBLE);

        getMessages();
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
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.nav_profile:
                openProfile();
                break;
            case R.id.nav_guide:
                break;
            case R.id.nav_settings:
                break;
            case R.id.nav_chats:
                break;
            case R.id.nav_changeInterests:
                openChangeInterests();
                break;
            case R.id.nav_logout:
                //fechar sessão
                startActivity(new Intent(this, LoginActivity.class));  //O efeito ao ser pressionado do botão (no caso abre a activity)
                finishAffinity();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.chat_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.automaticTranslation:
                break;
            case R.id.suggestion:
                break;
            case R.id.reportGuide:
                break;
            case R.id.leftSession:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void openProfile(){
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
    }

    public void openChangeInterests(){
        Intent intent = new Intent(this, InterestsActivity.class);
        startActivity(intent);
    }

    private void listMessages() {
        //chamar lista de mensagens pela API. Trabalhar com paginação e pilha, ou leitura desc.
        //Apresnder a pegar mensagem em tempo real. Setar balão correto na exibição
        ListView chat = contentMain.findViewById(R.id.list_messages);

        final ChatAdapter adapter = new ChatAdapter(list, this, this);
        chat.setAdapter(adapter);
    }

    private void getMessages() {
        Log.d("ChatActivity", "Inside of getMessages(): " + list.toString());
        final Message m1 = new Message();

        // Read from the database
        DatabaseReference myRef = database.getReference("message");
        //DatabaseReference myRef = database.getReference().getRoot();
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value = dataSnapshot.getValue(String.class);
                //String value = dataSnapshot.getValue(Message.class);
                //
                Log.d("ChatActivity", "Value is: " + value);

                m1.setContent(dataSnapshot.getValue(String.class));
                m1.setDataHora("18:30");
                m1.setIdUser(3);
                list.add(m1);

                listMessages();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("ChatActivity", "Failed to read value.", error.toException());
            }
        });

        // Write a message to the database
        //DatabaseReference myRef = database.getReference("message");
        //myRef.setValue("Hello, World!");

        spinner.setVisibility(View.GONE);
    }
}