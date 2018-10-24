package com.tcc.guiaturistico.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tcc.guiaturistico.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import me.drakeet.materialdialog.MaterialDialog;
import model.Localization;
import model.LocalizationDeserializer;
import model.Search;
import model.SearchDeserializer;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import service.LocalizationService;
import service.SearchService;
import service.UserService;
import util.DBController;
import util.StatusSearch;

/**
 * Created by Andressa on 27/05/2018.
 */

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {
    private static final String TAG = "HomeActivity";
    public TextView nameNavHeader, localizationNavHeader;
    private GoogleApiClient mGoogleApiClient;
    private Localization localization;
    private Location location;
    private MaterialDialog mMaterialDialog;
    private static final int REQUEST_PERMISSIONS_CODE = 128;
    private static final long TIME = (1000*15);
    private DBController crud;
    private ConstraintLayout layout;
    private ConstraintLayout contentMain;
    private ProgressBar spinner;
    private Timer timer;
    private boolean first = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        LocationServices.getFusedLocationProviderClient(this);

        crud = new DBController(this);
        setupComponents();

        verifyStatusSearch();

        //verifica de tempos em tempos o estado da busca
        if(timer == null) {
            timer = new Timer();
            TimerTask tarefa = new TimerTask() {
                @Override
                public void run() {
                    try {
                        verifyStatusSearch();
                    } catch (Exception e) {
                        String aux = e.getMessage();
                        Log.d(TAG, aux);
                        Toast.makeText(getApplicationContext(), aux, Toast.LENGTH_SHORT).show();
                    }
                }
            };
            timer.scheduleAtFixedRate(tarefa, TIME, TIME);
        }
    }

    public void verifyStatusSearch() {
        Log.d(TAG, "Entrou no verifyStatusSearch()");
        final Search s = new Search(0, null, crud.getUser().getIdUser());

        Gson g = new GsonBuilder().registerTypeAdapter(Search.class, new SearchDeserializer())
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(UserService.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(g))
                .build();

        SearchService service = retrofit.create(SearchService.class);

        Call<Search> requestSearch = service.read(crud.getUser().getIdUser());

        requestSearch.enqueue(new Callback<Search>() {
            @Override
            public void onResponse(@NonNull Call<Search> call, @NonNull Response<Search> response) {
                String aux;
                if(!response.isSuccessful()) {
                    aux = "Erro: " + (response.code());
                    Log.i(TAG, aux);
                }
                else if(response.isSuccessful()) {
                    try {
                        JSONObject jsonSearch = new JSONObject(new Gson().toJson(response.body()));

                        s.setIdUser(jsonSearch.getInt("idUser"));
                        s.setIdSearch(jsonSearch.getInt("idSearch"));
                        s.setStatus(Enum.valueOf(StatusSearch.class, jsonSearch.getString("status")));

                        Log.d(TAG, "Resultado do status na HomeActivity: " + s.getStatus());

                        setMiddle(s);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<Search> call, @NonNull Throwable t) {
                String aux = " Erro: " + t.getMessage();
                Log.e(TAG, aux);
                Toast.makeText(getApplicationContext(), aux, Toast.LENGTH_LONG).show();
            }
        });
    }

    public void setMiddle(Search sea) {
        StatusSearch status;
        status = (Enum.valueOf(StatusSearch.class, sea.getStatus().toString()));
        String aux = crud.getStatusSearch();
        Log.d(TAG, "Entrou no setMiddle(), status: " + status.toString());

        if(aux != null & aux.equals(status.toString()) & !first) {
            //não faz nada
            Log.d(TAG, "Entrou no setMiddle(), aux: " + aux);
        }
        else {
            switch (status) {
                case Accepted:
                    Intent intent = new Intent(this, ChatActivity.class);
                    startActivity(intent);
                    finishAffinity();
                    break;
                case Searching:
                    layout.setVisibility(View.GONE);
                    layout = contentMain.findViewById(R.id.fragHome);
                    layout.setVisibility(View.VISIBLE);
                    break;
                case Found:
                    layout.setVisibility(View.GONE);
                    layout = contentMain.findViewById(R.id.fragFound);
                    layout.setVisibility(View.VISIBLE);
                    break;
                case Rejected:
                    layout.setVisibility(View.GONE);
                    layout = contentMain.findViewById(R.id.fragRejected);
                    layout.setVisibility(View.VISIBLE);
                    break;
                case WaitingAnswer:
                    layout.setVisibility(View.GONE);
                    layout = contentMain.findViewById(R.id.fragWaiting);
                    layout.setVisibility(View.VISIBLE);
                    break;

                default:
                    layout.setVisibility(View.VISIBLE);
            }
            first = false;
        }
        spinner.setVisibility(View.GONE);

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
        contentMain = appBar.findViewById(R.id.contentMain);
        contentMain.setVisibility(View.VISIBLE);
        layout = contentMain.findViewById(R.id.fragHome);
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

        if (id == R.id.nav_profile) {
            openProfile();
        //} else if (id == R.id.nav_settings) {

        } else if (id == R.id.nav_changeInterests) {
            openChangeInterests();
        } else if (id == R.id.nav_logout) {
            //fechar sessão
            try {crud.deleteUser(crud.getUser()); } catch (Exception e){e.printStackTrace();}
            try {crud.deleteChat(crud.getChat());} catch (Exception e){e.printStackTrace();}
            try {crud.deleteStatusSearch();} catch (Exception e){e.printStackTrace();}


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

    @Override
    public void onResume() {
        super.onResume();

        if(mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            startLocationUpdate();
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        if(mGoogleApiClient != null) {
            stopLocationUpdate();
        }
    }

    private synchronized void callConnection() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addOnConnectionFailedListener(this)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    private void initLocationRequest() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(5000); //de cinco em cinco
        mLocationRequest.setFastestInterval(2000); //no mínimo
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void startLocationUpdate() {
        initLocationRequest();

        Log.i("", "startLocationUpdate()");

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                callDialog(getString(R.string.messageAskPermissionAgain), new String[]{Manifest.permission.ACCESS_FINE_LOCATION});
                Log.i(TAG, "permissão negada");
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSIONS_CODE);
            }
        } else {
            LocationServices.getFusedLocationProviderClient(this);

            if(location != null) {
                try {
                    Address ad = getAddress(location.getLatitude(), location.getLongitude());
                    localization = setLocalization(localization, ad);
                } catch (Exception e) {
                    e.getMessage();
                }
                String aux = localization.getCity() + ", " + localization.getUf();
                localizationNavHeader.setText(aux);
            }
        }
    }

    private void stopLocationUpdate() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.i("LOG", "onConnected(" + bundle + ")");

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                callDialog(getString(R.string.messageAskPermissionAgain), new String[]{Manifest.permission.ACCESS_FINE_LOCATION});
                Log.i(TAG, "permissão negada");
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSIONS_CODE);
            }
        } else {
            //obter a última localização conhecida do device
            location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        }

        if(location != null) {
            try {
                Address ad = getAddress(location.getLatitude(), location.getLongitude());
                localization = setLocalization(localization, ad);
            } catch (Exception e) {
                e.getMessage();
            }
            String aux = localization.getCity() + ", " + localization.getUf();
            localizationNavHeader.setText(aux);
        }
        startLocationUpdate();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i("LOG", "onConnectionSuspended(" + i + ")");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        try {
            Address ad = getAddress(location.getLatitude(), location.getLongitude());
            localization = setLocalization(localization, ad);
        } catch (Exception e) {
            e.getMessage();
        }
        String aux = localization.getCity() + ", " + localization.getUf();
        localizationNavHeader.setText(aux);
        updateLocalization(localization);
    }

    public Address getAddress(double latitude, double longitude) throws IOException {
        Geocoder geocoder;
        Address address = null;
        List<Address> addresses;

        geocoder = new Geocoder(getApplicationContext());
        addresses = geocoder.getFromLocation(latitude, longitude, 1);
        if(addresses.size() > 0)
            address = addresses.get(0);

        return address;
    }

    public Localization setLocalization(Localization l, Address a) {
        l.setLatitude(a.getLatitude());
        l.setLongitude(a.getLongitude());
        l.setCity(a.getLocality());
        l.setUf(a.getAdminArea());
        l.setCountry(a.getCountryName());
        return l;
    }

    private void callDialog( String message, final String[] permissions ) {
        mMaterialDialog = new MaterialDialog(this)
                .setTitle(getString(R.string.permission))
                .setMessage(message)
                .setPositiveButton(getString(R.string.AGREE), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ActivityCompat.requestPermissions(HomeActivity.this, permissions, REQUEST_PERMISSIONS_CODE);
                        mMaterialDialog.dismiss();
                    }
                })
                .setNegativeButton(getString(R.string.DENY), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mMaterialDialog.dismiss();
                    }
                });
        mMaterialDialog.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.i("", "test");
        switch( requestCode ){
            case REQUEST_PERMISSIONS_CODE:
                for( int i = 0; i < permissions.length; i++ ){

                    if( permissions[i].equalsIgnoreCase( Manifest.permission.ACCESS_FINE_LOCATION )
                            && grantResults[i] == PackageManager.PERMISSION_GRANTED ){

                        callConnection();
                    }
                }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public void updateLocalization(Localization localization) {
        Log.d(TAG, "Entrou no updateLocalization()");
        Gson g = new GsonBuilder().registerTypeAdapter(Localization.class, new LocalizationDeserializer())
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(UserService.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(g))
                .build();

        LocalizationService service = retrofit.create(LocalizationService.class);

        Call<Void> requestLocalization = service.update(localization);

        requestLocalization.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                String aux;
                if(!response.isSuccessful()) {
                    aux = "Erro: " + (response.code());
                    Log.i(TAG, aux);
                    Toast.makeText(getApplicationContext(), aux, Toast.LENGTH_LONG).show();
                }
                else if(response.isSuccessful()) {
                    Log.i("", "localização atualizada");
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                String aux = " Erro: " + t.getMessage();
                Log.e(TAG, aux);
                Toast.makeText(getApplicationContext(), aux, Toast.LENGTH_LONG).show();
            }
        });
    }
}