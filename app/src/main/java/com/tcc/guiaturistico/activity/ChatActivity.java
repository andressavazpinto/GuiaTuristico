//https://www.youtube.com/watch?v=U0xcNBg3Lhw até 4'
//https://www.youtube.com/watch?v=wVCz1a3ogqk ver a partir do 6'
//https://www.youtube.com/watch?v=AnNpUGyryiE - foto galeria
package com.tcc.guiaturistico.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.tcc.guiaturistico.R;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import adapter.ChatAdapter;
import model.Message;

public class ChatActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private final int GALERY_IMAGE = 1;
    private final int PERMISSION_REQUEST = 2;
    private ProgressBar spinner;
    private TextView nameNavHeader, localizationNavHeader;
    private ConstraintLayout contentMain;
    private List<Message> list;
    private ListView chat;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private EditText message;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
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

        ImageButton sendButton = contentMain.findViewById(R.id.imageButtonSend);
        ImageButton camButton = contentMain.findViewById(R.id.imageButtonCam);
        message = contentMain.findViewById(R.id.editTextWrite);

        chat = contentMain.findViewById(R.id.list_messages);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = message.getText().toString();
                sendMessage(text, "text", 1);
            }
        });

        camButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ContextCompat.checkSelfPermission(getApplicationContext(),
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    if(ActivityCompat.shouldShowRequestPermissionRationale(ChatActivity.this,
                            Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    } else {
                        ActivityCompat.requestPermissions(ChatActivity.this,
                                new String[]{ Manifest.permission.READ_EXTERNAL_STORAGE },
                                PERMISSION_REQUEST);
                    }
                }
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, GALERY_IMAGE);
            }
        });

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
            case R.id.suggestions:
                break;
            case R.id.reportGuide:
                break;
            case R.id.leftSession:
                break;
            default:
                //pegar a sugestão
                message.setText(item.getTitle());
                //posicionar o cursor no final do texto
                message.setSelection(message.getText().length());
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
        final ChatAdapter adapter = new ChatAdapter(list, this, this);
        chat.setAdapter(adapter);
        chat.setStackFromBottom(true);
    }

    private void getMessages() {
        Log.d("ChatActivity", "Inside of getMessages(): " + list.toString());
        //AQUI DEVO PASSAR O ID DA CONVERSA
        final DatabaseReference myRef = database.getReference("/chat/1/messages/");
        Query recentMessagesQuery = myRef.limitToLast(10);

        recentMessagesQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                list.clear();
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    Message a = postSnapshot.getValue(Message.class);
                    list.add(a);
                }
                listMessages();
            }

            @NonNull
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("ChatActivity", "Failed to read value.", error.toException());
            }
        });

        spinner.setVisibility(View.GONE);
    }

    public void sendMessage(String text, String type, int idChat) {
        if(text != null & ! text.equals("") & ! text.matches(" ")) {
            DatabaseReference myRef = database.getReference();

            final Message m2 = new Message(11, 3, text, type);

            Map<String, Object> postValues = m2.toMap();
            Map<String, Object> childUpdates = new HashMap();
            childUpdates.put("/chat/" + idChat + "/messages/" + m2.getIdMessage(), postValues);

            myRef.updateChildren(childUpdates);
        }
        message.setText("");
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && requestCode == 1) {
            Uri selectedImage = data.getData();
            String[] filePath = { MediaStore.Images.Media.DATA };
            Cursor c = getContentResolver().query(selectedImage, filePath, null, null, null);
            c.moveToFirst();
            int columnIndex = c.getColumnIndex(filePath[0]);
            String picturePath = c.getString(columnIndex);
            c.close();

            Bitmap image = (BitmapFactory.decodeFile(picturePath));
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object
            byte[] b = baos.toByteArray();
            String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
            Log.w("ChatActivity", "Encode image: " + encodedImage);
            sendMessage(encodedImage, "Image", 1);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if(requestCode == PERMISSION_REQUEST) {
            if(grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            } else {

            }
        }
    }
}