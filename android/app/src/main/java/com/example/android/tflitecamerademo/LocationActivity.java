package com.example.android.tflitecamerademo;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.google.android.gms.location.*;
import com.google.android.gms.tasks.*;



public class LocationActivity extends AppCompatActivity {


    public static final int REQUEST_PERMISSION = 200;

    // location last updated time
    private String mLastUpdateTime;
    private static final String TAG = "TfLiteCameraDemo";

    // location updates interval - 10sec
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;

    // fastest updates interval - 5 sec
    // location updates will be received if another app is requesting the locations
    // than your app can handle
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = 5000;

    private static final int REQUEST_CHECK_SETTINGS = 100;

    // bunch of location related apis
    private FusedLocationProviderClient mFusedLocationClient;
    public Location mCurrentLocation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Get the last known location
        getLocation();
    }

    private void getLocation() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mFusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        mCurrentLocation = location;
                        Intent data = new Intent();
                        Bundle bundle = new Bundle();
                        bundle.putDoubleArray("location", new double[] {location.getLongitude(), location.getLatitude(), location.getAltitude()});
                        data.putExtras(bundle);
                        setResult(RESULT_OK, data);
                        finish();
                    }
                }
            });
        }
        else {
            Log.e("Insufficient permission", "Failed to get location");
        }
    }
}
