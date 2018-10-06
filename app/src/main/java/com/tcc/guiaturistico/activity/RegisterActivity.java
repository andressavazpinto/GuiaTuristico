//https://www.youtube.com/watch?v=bP9RYHKJzNs
//https://www.youtube.com/watch?v=ScK-z8paLlc
//https://www.youtube.com/watch?v=YvPAOLV-jk8
//https://www.youtube.com/watch?v=gLI_jopCS3Y
package com.tcc.guiaturistico.activity;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.support.v7.app.AppCompatActivity;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.tcc.guiaturistico.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import me.drakeet.materialdialog.MaterialDialog;
import model.Language;
import model.LanguageDeserializer;
import model.Localization;
import model.LocalizationDeserializer;
import model.Translate;
import model.User;
import model.UserDeserializer;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import service.LocalizationService;
import service.TranslationService;
import service.UserService;
import util.DBController;
import util.Mask;
import util.Message;
import util.StatusUser;

/**
 * Created by Andressa on 13/05/2018.
 */

public class RegisterActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener{
    private static final String TAG = "RegisterActivity";
    private static final int ERROR_DIALOG_REQUEST = 9001;
    private EditText editTextName, editTextDateOfBirth, editTextUserEmail, editTextPassword, editTextLocalization;
    private Spinner spinnerLanguage;
    private ProgressBar spinner;
    private DBController crud;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Localization localization;
    private Location location;
    private MaterialDialog mMaterialDialog;
    private static final int REQUEST_PERMISSIONS_CODE = 128;
    List<String> list_languages;
    List<Language> languages;
    String language;
    private FusedLocationProviderClient mFusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        crud = new DBController(this);
        localization = new Localization();
        callAccessLocation(super.getCurrentFocus());

        editTextName = findViewById(R.id.editTextName);

        editTextDateOfBirth = findViewById(R.id.editTextDateOfBirth);
        editTextDateOfBirth.addTextChangedListener(Mask.insert(Mask.FORMAT_DATE, editTextDateOfBirth));

        editTextUserEmail = findViewById(R.id.editTextUserEmail);
        editTextPassword = findViewById(R.id.editTextPassword);

        spinnerLanguage = findViewById(R.id.spinnerLanguage);

        spinnerLanguage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
                //pega nome pela posição
                Log.d(TAG, "Nome Selecionado: " + selectLanguage(position));
            }
            public void onNothingSelected(AdapterView<?> adapterView) {
                //return;
            }
        });

        if(isServicesOK())
            listLanguages();

        editTextLocalization = findViewById(R.id.editTextLocalization);

        spinner = findViewById(R.id.progressBar);
        spinner.setVisibility(View.VISIBLE);

        Button buttonRegister = findViewById(R.id.buttonContinue);
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                if (imm.isActive()) {
                    hideSoftKeyboard();
                }
                if(validateFields()) {
                    spinner.setVisibility(View.VISIBLE);
                    registerLocalization();
                }
            }
        });
    }

    public boolean isServicesOK() {
        Log.d(TAG, "isServicesOK: checking google services version");
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(RegisterActivity.this);
        if(available == ConnectionResult.SUCCESS) {
            Log.d(TAG, "isServicesOK: Google Play services is working");
            return true;
        } else if(GoogleApiAvailability.getInstance()
                .isUserResolvableError(available)) {
            Log.d(TAG, "isSErvicesOK: an error occured but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(RegisterActivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        } else {
            Toast.makeText(this, "You can't connect", Toast.LENGTH_LONG).show();
        }
        return false;
    }

    private void listLang(List<Language> output) {
        this.languages = output;
        int systemLanguage = 0;
        list_languages = new ArrayList<String>();

        if(languages.size() > 0) {
            int i=0;
            do {
                if(languages.get(i).getLanguage().equals(System.getProperty("user.language")))
                    systemLanguage = i;

                list_languages.add(languages.get(i).getName());
                i++;
            } while(i<languages.size());
        }

        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item,
                list_languages);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spinnerLanguage.setAdapter(spinnerArrayAdapter);
        spinnerLanguage.setSelection(systemLanguage);
        spinner.setVisibility(View.GONE);
    }

    public void listLanguages() {

        Gson g = new GsonBuilder().registerTypeAdapter(Language.class, new LanguageDeserializer())
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(TranslationService.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(g))
                .build();

        TranslationService service = retrofit.create((TranslationService.class));

        final Translate t = new Translate("", "", "pt", "text");
        String API_KEY = "AIzaSyByLqEvttULJFQRbNxpPqa4dxETVOgP_e8";

        Call<JsonObject> request = service.listLanguages(t, API_KEY);

        request.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JsonObject body = response.body();
                    JsonArray langItens = body.get("data").getAsJsonObject().get("languages").getAsJsonArray();
                    List<Language> output = new ArrayList<Language>();

                    for (int i = 0; i < langItens.size(); i++) {
                        Language lang = new Language();
                        lang.setName(langItens.get(i).getAsJsonObject().get("name").getAsString());
                        lang.setLanguage(langItens.get(i).getAsJsonObject().get("language").getAsString());
                        output.add(lang);
                        //System.out.println("Indice: " + i + " ---> " + languages.get(i).toString());
                    }
                    System.out.println("Dentro do listLanguages ---> " + output.toString());

                    listLang(output);
                }
            }
            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                String aux = " Erro: " + t.getMessage();
                Log.e("listLanguages", aux);
            }
        });
    }

    public void hideSoftKeyboard() {
        if(getCurrentFocus()!=null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
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

    public String selectLanguage(int position) {
        language = languages.get(position).getLanguage();
        return language;
    }

    public void registerLocalization() {
        Gson g = new GsonBuilder().registerTypeAdapter(Localization.class, new LocalizationDeserializer())
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(LocalizationService.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(g))
                .build();

        LocalizationService service = retrofit.create((LocalizationService.class));

        final Localization l = localization;

        Call<Integer> requestLocalization = service.register(l);

        requestLocalization.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(@NonNull Call<Integer> call, @NonNull Response<Integer> response) {
                String aux;
                if(!response.isSuccessful()) {
                    aux = "Erro: " + (response.code());
                    Log.i(TAG, aux);
                    Toast.makeText(getApplicationContext(), aux, Toast.LENGTH_LONG).show();
                }
                else {
                    //setar o id do endereço no usuário, realizando seu cadastro
                    registerUser(Integer.parseInt(response.body().toString()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<Integer> call, @NonNull Throwable t) {
                String aux = " Erro: " + t.getMessage();
                Log.e(TAG, aux);
                Toast.makeText(getApplicationContext(), aux, Toast.LENGTH_LONG).show();
            }
        });
    }

    public void registerUser(int idLocalization) {
        Gson g = new GsonBuilder().registerTypeAdapter(User.class, new UserDeserializer())
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(UserService.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(g))
                .build();

        UserService service = retrofit.create(UserService.class);

        final User u = new User();
        u.setName(editTextName.getText().toString());

        String array[];
        array = editTextDateOfBirth.getText().toString().split("/");
        String aux = array[2]+"-"+array[1]+"-"+array[0];
        u.setDateOfBirth(aux);

        u.setEmail(editTextUserEmail.getText().toString());
        u.setPassword(editTextPassword.getText().toString());

        u.setLanguage(language);

        //setar o id da localização após cadastrar
        u.setIdLocalization(idLocalization);
        u.setStatusAccount(StatusUser.Active);

        Call<Integer> requestUser = service.register(u);

        requestUser.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(@NonNull Call<Integer> call, @NonNull Response<Integer> response) {
                String aux;
                if(!response.isSuccessful()) {
                    aux = "Erro: " + (response.code());
                    Log.i(TAG, aux);
                    Toast.makeText(getApplicationContext(), aux, Toast.LENGTH_LONG).show();
                }
                else {
                    u.setIdUser(Integer.parseInt(response.body().toString()));
                    System.out.print("Id: " + u.getIdUser() + "\n");
                    crud.insertUser(u);
                    finishAffinity();
                    openInterests();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Integer> call, @NonNull Throwable t) {
                String aux = " Erro: " + t.getMessage();
                Log.e(TAG, aux);
                Toast.makeText(getApplicationContext(), aux, Toast.LENGTH_LONG).show();
            }
        });
        spinner.setVisibility(View.GONE);
    }

    private void openInterests() {
        Intent intent = new Intent(this, InterestsActivity.class);
        startActivity(intent);
    }

    public boolean validateFields() {
        Boolean aux = true;
        if(editTextName.getText().length() == 0){
            editTextName.setBackground(ContextCompat.getDrawable(this, R.drawable.shape_line_error));
            aux = false;
        }
        else
            editTextName.setBackground(ContextCompat.getDrawable(this, R.drawable.shape_line_success));

        if(editTextDateOfBirth.getText().length() == 0){
            editTextDateOfBirth.setBackground(ContextCompat.getDrawable(this, R.drawable.shape_line_error));
            aux = false;
        }
        else
            editTextDateOfBirth.setBackground(ContextCompat.getDrawable(this, R.drawable.shape_line_success));

        if(editTextLocalization.getText().length() == 0){
            editTextLocalization.setBackground(ContextCompat.getDrawable(this, R.drawable.shape_line_error));
            aux = false;
        }
        else
            editTextLocalization.setBackground(ContextCompat.getDrawable(this, R.drawable.shape_line_success));

        if(editTextUserEmail.getText().length() == 0 | !editTextUserEmail.getText().toString().contains("@")){
            editTextUserEmail.setBackground(ContextCompat.getDrawable(this, R.drawable.shape_line_error));
            aux = false;
        }
        else
            editTextUserEmail.setBackground(ContextCompat.getDrawable(this, R.drawable.shape_line_success));

        if(editTextPassword.getText().length() == 0){
            editTextPassword.setBackground(ContextCompat.getDrawable(this, R.drawable.shape_line_error));
            aux = false;
        }
        else
            editTextPassword.setBackground(ContextCompat.getDrawable(this, R.drawable.shape_line_success));

        return aux;
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
                callDialog(Message.messageAskPermissionAgain, new String[]{Manifest.permission.ACCESS_FINE_LOCATION});
                Log.i("TAG", "permissão negada");
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
                editTextLocalization.setText(localization.getCity() + ", " + localization.getUf());
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
                callDialog(Message.messageAskPermissionAgain, new String[]{Manifest.permission.ACCESS_FINE_LOCATION});
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
            editTextLocalization.setText(localization.getCity() + ", " + localization.getUf());
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
        editTextLocalization.setText(aux);
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
                        ActivityCompat.requestPermissions(RegisterActivity.this, permissions, REQUEST_PERMISSIONS_CODE);
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
}