package com.example.snehapantam.runtime;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.service.carrier.CarrierMessagingService;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.*;




import com.google.android.gms.maps.GoogleMap;


import com.google.android.gms.maps.model.Marker;
import com.indooratlas.android.sdk.IALocation;
import com.indooratlas.android.sdk.IALocationListener;
import com.indooratlas.android.sdk.IALocationManager;
import com.indooratlas.android.sdk.IALocationRequest;



public class MainActivity extends AppCompatActivity implements IALocationListener,AdapterView.OnItemSelectedListener {
    private static final String TAG = "MainActivity";

    private IALocationManager mLocationManager;




    private static final int REQUEST_CODE_ACCESS_COARSE_LOCATION = 1;





    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLocationManager = IALocationManager.create(this);


        ensurePermissions();

        mLocationManager = IALocationManager.create(this);



        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(this);
// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.planets_array, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        spinner.setAdapter(adapter);


    }




    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLocationManager.destroy();
    }

    @Override
    protected void onResume() {
        super.onResume();

        mLocationManager.requestLocationUpdates(IALocationRequest.create(), this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mLocationManager != null) {
            mLocationManager.removeLocationUpdates(this);
        }
    }






    public void onLocationChanged(IALocation location) {
        ensurePermissions();


        Log.d(TAG, "Latitude: " + location.getLatitude());
        Log.d(TAG, "Longitude: " + location.getLongitude());


        }


    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // N/A
    }







    private void ensurePermissions() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)
                 {

            // we don't have access to coarse locations, hence we have not access to wifi either
            // check if this requires explanation to user
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) ){

                new AlertDialog.Builder(this)
                        .setTitle(R.string.location_permission_request_title)
                        .setMessage(R.string.location_permission_request_rationale)
                        .setPositiveButton(R.string.permission_button_accept, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.d(TAG, "request permissions");
                                ActivityCompat.requestPermissions(MainActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        REQUEST_CODE_ACCESS_COARSE_LOCATION);
                            }
                        })
                        .setNegativeButton(R.string.permission_button_deny, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(MainActivity.this,
                                        R.string.location_permission_denied_message,
                                        Toast.LENGTH_LONG).show();
                            }
                        })
                        .show();

            } else {
                Log.d(TAG, "asking permissions to user");

                // ask user for permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_CODE_ACCESS_COARSE_LOCATION);

            }

        }

        }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        switch (requestCode) {
            case REQUEST_CODE_ACCESS_COARSE_LOCATION:

                if (grantResults.length > 0
                        || grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permission granted thank you",
                            Toast.LENGTH_LONG).show();


                }
                break;
        }

    }




    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

        final Intent intent;
        switch(i){

            case 0:
break;

            case 1:
                intent= new Intent(MainActivity.this, CommonActivity.class);

                intent.putExtra("someVariable", "1");
                startActivity(intent);
                break;





            case 2:
                intent= new Intent(MainActivity.this,CommonActivity.class);
                intent.putExtra("someVariable", "2");
                startActivity(intent);
                break;

            case 3:
                intent= new Intent(MainActivity.this,CommonActivity.class);
                intent.putExtra("someVariable", "3");
                startActivity(intent);
                break;

            case 4:
                intent= new Intent(MainActivity.this,CommonActivity.class);
                intent.putExtra("someVariable", "4");
                startActivity(intent);
                break;
            case 5:
                intent= new Intent(MainActivity.this,CommonActivity.class);
                intent.putExtra("someVariable", "5");
                startActivity(intent);
                break;

        }



    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}



