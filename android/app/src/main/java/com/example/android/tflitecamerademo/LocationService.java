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
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import androidx.core.content.ContextCompat;

public class LocationService extends Service {

    private FusedLocationProviderClient mFusedLocationClient;
    public Location mCurrentLocation;

    private LocationCallback locationCallback;

    public LocationService() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    mCurrentLocation = location;
                }
            };
        };
    }

    protected LocationRequest CreateLocationRequest() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return locationRequest;
    }



    public boolean StartLocationServices() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mFusedLocationClient.requestLocationUpdates(CreateLocationRequest(), locationCallback, null);
            return true;
        }
        else {
            Log.e("Insufficient permission", "Failed to get location");
        }

        return false;
    }

    public boolean StopLocationServices() {
        try {
            mFusedLocationClient.removeLocationUpdates(locationCallback);
            return true;
        } catch(Exception ex){
            Log.e("Location","Error Removing Location Updates");
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


