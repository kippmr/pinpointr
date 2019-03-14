package com.example.android.tflitecamerademo;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import androidx.core.content.ContextCompat;

public class LocationService extends Service {

    private FusedLocationProviderClient mFusedLocationClient;
    public Location mCurrentLocation;


    public LocationService() {

    }



    public boolean StartLocationServices() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mFusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        mCurrentLocation = location;

                    }
                }
            });
            return true;
        }
        else {
            Log.e("Insufficient permission", "Failed to get location");
        }

        return false;
    }

    public Location getLocation() {
        return mCurrentLocation;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new LocationServiceBinder();
    }

    public class LocationServiceBinder extends Binder {
        public LocationService getService() {return LocationService.this; }
    }

}


