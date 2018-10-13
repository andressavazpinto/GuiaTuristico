package com.tcc.guiaturistico.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tcc.guiaturistico.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import me.drakeet.materialdialog.MaterialDialog;
import model.Localization;
import model.Search;
import model.SearchDeserializer;
import model.User;
import model.UserDeserializer;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import service.SearchService;
import service.UserService;
import util.DBController;
import util.Message;
import util.StatusSearch;
import util.StatusUser;

/**
 * Created by Andressa on 27/05/2018.
 */

public class SplashActivity extends Activity implements Runnable {
    private static final String TAG = "SplashActivity";
    private DBController crud;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Localization localization;
    private Location location;
    private MaterialDialog mMaterialDialog;
    private static final int REQUEST_PERMISSIONS_CODE = 128;
    private User u;
    private String localizationDescription;
    FusedLocationProviderClient mFusedLocationClient;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        crud = new DBController(this);
        u = crud.getUser();

        Handler splashScreen = new Handler();
        splashScreen.postDelayed(SplashActivity.this, 2800);
    }

    public void run() {
        try {
            if (u.getIdUser() != 0) {
                login(u.getEmail(), u.getPassword());
                Log.i(TAG, "entrou na cond do login");
            } else {
                startActivity(new Intent(this, MainActivity.class));
                Log.i(TAG, "entrou na cond do main");
                finish();
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void login(String email, final String password) {
        Gson g = new GsonBuilder().registerTypeAdapter(User.class, new UserDeserializer())
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(UserService.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(g))
                .build();

        UserService service = retrofit.create(UserService.class);

        Log.i(TAG, "email: " + email + " senha: " + password);
        u.setEmail(email);
        u.setPassword(password);

        Call<User> requestUser = service.login(u);

        requestUser.enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                if(response.isSuccessful()) {
                    System.out.println("response" + response.body());
                    try {

                        JSONObject jsonUser = new JSONObject(new Gson().toJson(response.body()));

                        u.setIdUser(jsonUser.getInt("idUser"));
                        u.setName(jsonUser.getString("name"));
                        u.setDateOfBirth(jsonUser.getString("dateOfBirth"));
                        u.setEmail(jsonUser.getString("email"));
                        u.setPassword(jsonUser.getString("password"));
                        u.setLanguage(jsonUser.getString("language"));
                        u.setIdLocalization(jsonUser.getInt("idLocalization"));
                        u.setStatusAccount(Enum.valueOf(StatusUser.class, jsonUser.getString("statusAccount")));

                        System.out.println("Resultado do login: " + u.toString());

                        try {
                            //try {crud.deleteUser(crud.getUser());} catch (Exception e) {e.printStackTrace();}
                            //try{ crud.updateUser(u);} catch (Exception e) {e.printStackTrace();}
                            System.out.println("Resultado do crud: " + u.getIdUser());
                            verifyStatusSearch(u.getIdUser());
                        } catch(Exception e) {
                            e.printStackTrace();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    System.out.print("Id: " + u.getIdUser());
                }
                else {
                    String aux = "Erro: " + response.code();
                    Log.e(TAG, aux);
                    Toast.makeText(getApplicationContext(), aux, Toast.LENGTH_LONG).show();
                }

            }
            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                String aux = "Erro: " + t.getMessage();
                Log.e(TAG, aux);
                Toast.makeText(getApplicationContext(), aux, Toast.LENGTH_LONG).show();
                login(u.getEmail(), u.getPassword());
            }
        });
    }

    public void verifyStatusSearch(int id) {
        final Search s = new Search(1, Enum.valueOf(StatusSearch.class,"Initial"), crud.getUser().getIdUser());

        Gson g = new GsonBuilder().registerTypeAdapter(Search.class, new SearchDeserializer())
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(UserService.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(g))
                .build();

        SearchService service = retrofit.create(SearchService.class);

        Call<Search> requestSearch = service.read(id);

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

                        openHome(s);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<Search> call, @NonNull Throwable t) {
                String aux = "Erro: " + t.getMessage();
                Log.e(TAG, aux);
                Toast.makeText(getApplicationContext(), aux, Toast.LENGTH_LONG).show();
            }
        });
    }

    public void openHome(Search s){
        System.out.print("Resultado entrou no openHome()");

        Intent intent;
        StatusSearch status = (Enum.valueOf(StatusSearch.class, s.getStatus().toString()));
        switch (status) {
            case Accepted:
                intent = new Intent(this, ChatActivity.class);
                break;
            default:
                intent = new Intent(this, HomeActivity.class);
        }

        intent.putExtra("name", u.getName());
        intent.putExtra("localization", getLocalizationDescription());
        startActivity(intent);
        finish();
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
                .addOnConnectionFailedListener((GoogleApiClient.OnConnectionFailedListener) this)
                .addConnectionCallbacks((GoogleApiClient.ConnectionCallbacks) this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    private void initLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(5000); //de cinco em cinco
        mLocationRequest.setFastestInterval(2000); //no mínimo
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void startLocationUpdate() {
        initLocationRequest();

        Log.i(TAG, "startLocationUpdate()");

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                callDialog(Message.messageAskPermissionAgain, new String[]{Manifest.permission.ACCESS_FINE_LOCATION});
                Log.i("TAG", "permissão negada");
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSIONS_CODE);
            }
        } else {
            //LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, (LocationListener) this);
            LocationServices.getFusedLocationProviderClient(this);

            if(location != null) {
                try {
                    Address ad = getAddress(location.getLatitude(), location.getLongitude());
                    localization = setLocalization(localization, ad);
                } catch (Exception e) {
                    e.getMessage();
                }
                setLocalizationDescription(localization.getCity() + ", " + localization.getUf());
            }
        }
    }

    private void stopLocationUpdate() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, (LocationListener) this);
    }

    //listener
    public void onConnected(Bundle bundle) {
        Log.i("LOG", "onConnected(" + bundle + ")");

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                callDialog(Message.messageAskPermissionAgain, new String[]{Manifest.permission.ACCESS_FINE_LOCATION});
                Log.i("TAG", "permissão negada");
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSIONS_CODE);
            }
        } else {
            //obter a última localização conhecida do device
            //location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            LocationServices.getFusedLocationProviderClient(this);
        }

        if(location != null) {
            try {
                Address ad = getAddress(location.getLatitude(), location.getLongitude());
                localization = setLocalization(localization, ad);
            } catch (Exception e) {
                e.getMessage();
            }
            String aux = localization.getCity() + ", " + localization.getUf();
            Log.d(TAG, "localizationDescription: " + aux);
            setLocalizationDescription(aux);
        }
        startLocationUpdate();
    }

    public void callAccessLocation(View view) {
        Log.i(TAG, "callAccessLocation()");

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            //solicitar novamente permissão ao usuário
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                callDialog(Message.messageAskPermissionAgain, new String[]{Manifest.permission.ACCESS_FINE_LOCATION});
                Log.i("TAG", "permissão negada");
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSIONS_CODE);
            }
        } else {
            callConnection();
        }
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
                .setTitle(Message.permission)
                .setMessage(message)
                .setPositiveButton(Message.agree, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ActivityCompat.requestPermissions(SplashActivity.this, permissions, REQUEST_PERMISSIONS_CODE);
                        mMaterialDialog.dismiss();
                    }
                })
                .setNegativeButton(Message.deny, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mMaterialDialog.dismiss();
                    }
                });
        mMaterialDialog.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.i(TAG, "test");
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

    public void setLocalizationDescription(String localizationDescription) {
        this.localizationDescription = localizationDescription;
    }

    public String getLocalizationDescription() {
        return localizationDescription;
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
