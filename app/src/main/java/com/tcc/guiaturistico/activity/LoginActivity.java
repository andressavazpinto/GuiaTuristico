package com.tcc.guiaturistico.activity;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import android.Manifest;
import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tcc.guiaturistico.R;

import java.io.IOException;
import java.util.List;

import me.drakeet.materialdialog.MaterialDialog;
import model.Localization;
import model.Search;
import model.SearchDeserializer;
import model.UserDeserializer;
import model.User;
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
 * Created by Andressa on 31/03/2018.
 */

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {
    private static final String TAG = "LoginActivity";
    private EditText editTextUserEmail, editTextPassword;
    private ProgressBar spinner;
    private DBController crud;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Localization localization;
    private Location location;
    private MaterialDialog mMaterialDialog;
    private static final int REQUEST_PERMISSIONS_CODE = 128;
    private User u;
    private String localizationDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        crud = new DBController(this);
        localization = new Localization();
        localizationDescription = "";
        callAccessLocation(super.getCurrentFocus());
        u = new User();

        spinner = findViewById(R.id.progressBar);

        editTextUserEmail = findViewById(R.id.editTextUserEmail);
        editTextPassword = findViewById(R.id.editTextPassword);

        editTextPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    login();
                }
                return false;
            }
        });

        Button buttonLogin = findViewById(R.id.buttonLogin);
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                spinner.setVisibility(View.VISIBLE);

                InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                if (imm.isActive())
                    hideSoftKeyboard();

                if(validateFields())
                    login();
                else
                    spinner.setVisibility(View.GONE);
            }
        });

        TextView textForgetPass =  findViewById(R.id.textForgotPass);
        textForgetPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment d = new DialogForgotPass();
                d.show(getFragmentManager(), "esqueceu");
            }
        });
    }

    public void hideSoftKeyboard() {
        if(getCurrentFocus()!=null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    public void setLocalizationDescription(String localizationDescription) {
        this.localizationDescription = localizationDescription;
    }

    public String getLocalizationDescription() {
        return localizationDescription;
    }

    public void login() {
        Gson g = new GsonBuilder().registerTypeAdapter(User.class, new UserDeserializer())
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(UserService.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(g))
                .build();

        UserService service = retrofit.create(UserService.class);

        u.setEmail(editTextUserEmail.getText().toString());
        u.setPassword(editTextPassword.getText().toString());

        Call<User> requestUser = service.login(u);

        requestUser.enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                if(response.isSuccessful()) {
                    try {
                        u = response.body();

                        if (u == null) {
                            Toast.makeText(getApplicationContext(), getString(R.string.emailOrPass), Toast.LENGTH_SHORT).show();
                        } else {
                            try {
                                if (crud.getUser() != null)
                                    try {
                                        crud.deleteUser(crud.getUser());
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                crud.insertUser(u);
                                System.out.println("Resultado do crud: " + u.getIdUser());
                                verifyStatusSearch(u.getIdUser());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    } catch(Exception e){
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                String aux = "Erro: " + t.getMessage();
                Log.e(TAG, aux);
                Toast.makeText(getApplicationContext(), aux, Toast.LENGTH_LONG).show();
            }
        });
        spinner.setVisibility(View.GONE);
    }

    public void openHome(Search s, Localization loc){
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
        intent.putExtra("localization", loc.getCity() + ", " + loc.getUf());
        intent.putExtra("score", u.getScoreS());
        intent.putExtra("scoreDouble", u.getScore());
        startActivity(intent);
        finishAffinity();
    }

    public boolean validateFields() {
        Boolean aux = true;
        if(editTextUserEmail.getText().length() == 0 | !editTextUserEmail.getText().toString().contains("@")){
            editTextUserEmail.setBackground(ContextCompat.getDrawable(LoginActivity.this, R.drawable.shape_line_error));
            aux = false;
        }
        else
            editTextUserEmail.setBackground(ContextCompat.getDrawable(LoginActivity.this, R.drawable.shape_line_success));
        if(editTextPassword.getText().length() == 0){
            editTextPassword.setBackground(ContextCompat.getDrawable(LoginActivity.this, R.drawable.shape_line_error));
            aux = false;
        }
        else
            editTextPassword.setBackground(ContextCompat.getDrawable(LoginActivity.this, R.drawable.shape_line_success));
        return aux;
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
                callDialog(getString(R.string.messageAskPermissionAgain), new String[]{Manifest.permission.ACCESS_FINE_LOCATION});
                Log.i("TAG", "permissão negada");
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSIONS_CODE);
            }
        } else {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

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
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    //listener
    @Override
    public void onConnected(Bundle bundle) {
        Log.i("LOG", "onConnected(" + bundle + ")");

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                callDialog(getString(R.string.messageAskPermissionAgain), new String[]{Manifest.permission.ACCESS_FINE_LOCATION});
                Log.i("TAG", "permissão negada");
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
            setLocalizationDescription(localization.getCity() + ", " + localization.getUf());
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
        setLocalizationDescription(localization.getCity() + ", " + localization.getUf());
    }

    public void callAccessLocation(View view) {
        Log.i(TAG, "callAccessLocation()");

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            //solicitar novamente permissão ao usuário
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                callDialog(getString(R.string.messageAskPermissionAgain), new String[]{Manifest.permission.ACCESS_FINE_LOCATION});
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
                .setTitle(getString(R.string.permission))
                .setMessage(message)
                .setPositiveButton(getString(R.string.AGREE), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ActivityCompat.requestPermissions(LoginActivity.this, permissions, REQUEST_PERMISSIONS_CODE);
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

    public void verifyStatusSearch(int id) {
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
                        Search s = response.body();

                        try{crud.insertStatusSearch(s.getStatus().toString());} catch(Exception e){Log.i(TAG, e.getMessage());}

                        getLocalization(u.getIdLocalization(), s);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<Search> call, @NonNull Throwable t) {
                String aux = " Deu falha no login: " + t.getMessage();
                Log.e("TAG", aux);
                Toast.makeText(getApplicationContext(), aux, Toast.LENGTH_LONG).show();
            }
        });
    }

    public void getLocalization(int idLocalization, Search s) {
        final int idAux = idLocalization;
        final Search search = s;
        Gson g = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(LocalizationService.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(g))
                .build();

        LocalizationService service = retrofit.create(LocalizationService.class);

        Call<Localization> requestLocalization = service.read(idLocalization);

        requestLocalization.enqueue(new Callback<Localization>() {
            @Override
            public void onResponse(@NonNull Call<Localization> call, @NonNull Response<Localization> response) {
                if (response.isSuccessful()) {
                    openHome(search, response.body());
                } else {
                    Log.i(TAG, "Erro: " + (response.code()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<Localization> call, @NonNull Throwable t) {
                Log.e(TAG, "Erro: " + t.getMessage());
                getLocalization(idAux, search);
            }
        });
    }
}