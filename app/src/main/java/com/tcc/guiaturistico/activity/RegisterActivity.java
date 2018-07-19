//https://www.youtube.com/watch?v=bP9RYHKJzNs
package com.tcc.guiaturistico.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.support.v7.app.AppCompatActivity;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tcc.guiaturistico.R;

import model.User;
import model.UserDeserializer;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import service.UserService;
import util.DBController;
import util.Mask;
import util.Status;

/**
 * Created by Andressa on 13/05/2018.
 */

public class RegisterActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener{
    private static final String TAG = "Error";
    private EditText editTextName, editTextDateOfBirth, editTextUserEmail, editTextPassword, editTextOccupation, editTextLocalization;
    private Spinner spinnerLanguage;
    private Button buttonRegister;
    private ProgressBar spinner;
    private DBController crud;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //callConnection();

        crud = new DBController(this);

        editTextName = findViewById(R.id.editTextName);

        editTextDateOfBirth = findViewById(R.id.editTextDateOfBirth);
        editTextDateOfBirth.addTextChangedListener(Mask.insert(Mask.FORMAT_DATE, editTextDateOfBirth));

        editTextUserEmail = findViewById(R.id.editTextUserEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextOccupation = findViewById(R.id.editTextOccupation);

        //String[] list_languages = getResources().getStringArray(R.array.list_languages);

        spinnerLanguage = findViewById(R.id.spinnerLanguage);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.list_languages, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLanguage.setAdapter(adapter);

        //pegar o idioma do celular spinnerLanguage.setSelection();


        editTextLocalization = findViewById(R.id.editTextLocalization);
        editTextLocalization.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RegisterActivity.this.callAccessLocation(view);
            }
        });

        spinner = findViewById(R.id.progressBar);

        buttonRegister = findViewById(R.id.buttonContinue);
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                if (imm.isActive()) {
                    imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                }
                if(validateFields()) {
                    spinner.setVisibility(View.VISIBLE);
                    register();
                }
            }
        });
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

    public String selectLanguage() {
        return (String) spinnerLanguage.getSelectedItem();
    }

    public void register() {
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
        u.setOccupation(editTextOccupation.getText().toString());

        u.setLanguage(selectLanguage());

        u.setLocalization(editTextLocalization.getText().toString());
        u.setStatusAccount(Status.Active);

        Call<Integer> requestUser = service.register(u);

        requestUser.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
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
            public void onFailure(Call<Integer> call, Throwable t) {
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
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    private void stopLocationUpdate() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    //listener
    @Override
    public void onConnected(Bundle bundle) {
        Log.i("LOG", "onConnected(" + bundle + ")");

        //obter a última localização conhecida do device
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if(location != null) {
            Log.i("LOG", "latitude: " + location.getLatitude());
            Log.i("LOG", "longitude: " + location.getLongitude());
        }

        startLocationUpdate();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i("LOG", "onConnectionSuspended(" + i + ")");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        editTextLocalization.setText(("Latitude: " + location.getLatitude() +
                                      "Longitude: " + location.getLongitude()));
    }

    public void callAccessLocation(View view) {
        Log.i(TAG, "callAccessLocation()");

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                //callDialog("É preciso a permission ACCESS_FINE_LOCATION para apresentação dos eventos locais.", new String[]{Manifest.permission.ACCESS_FINE_LOCATION});
                Log.i("TAG", "permissão negada");
            } else {
                //ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSIONS_CODE);
            }
        } else {
            callConnection();
        }
    }

    /*private void callDialog( String message, final String[] permissions ){
        mMaterialDialog = new MaterialDialog(this)
                .setTitle("Permission")
                .setMessage( message )
                .setPositiveButton("PERMITIR", new View.OnClickListener() { //colocar no geralzao p traduzir
                    @Override
                    public void onClick(View v) {
                        ActivityCompat.requestPermissions(this, permissions, REQUEST_PERMISSIONS_CODE);
                        mMaterialDialog.dismiss();
                    }
                })
                .setNegativeButton("NEGAR", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mMaterialDialog.dismiss();
                    }
                });
        mMaterialDialog.show();
    }*/

    /*@Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
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
    }*/
}