/* Copyright 2017 The TensorFlow Authors. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
==============================================================================*/

package com.example.android.tflitecamerademo;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


/** Main {@code Activity} class for the Camera app. */
public class CameraActivity extends AppCompatActivity {

    /* Screen Controls */

    RelativeLayout screenLayout_Camera;
    RelativeLayout screenLayout_Review;
    RelativeLayout buttonPanel;

    BottomAppBar bottomAppBar;

    ImageButton btnCamera;
    ImageButton btnBack;
    FloatingActionButton btnNavBar_Send;



    ImageData imgData;
    public static final int REQUEST_IMAGE = 100;
    public static final int REQUEST_PERMISSION = 200;
    private String imageFilePath = "";

    private Camera2BasicFragment camera2BasicFragment;

    // Tag for error logging
    private static final String TAG = CameraActivity.class.getSimpleName();

    private enum ScreenTransition{
        ToReview,
        ToPreview
    }
    //Location Service Variables
    private Boolean locationServiceBound = false;
    private LocationService locationService; //Reference to the location service
    private ServiceConnection locationServiceConnection = new ServiceConnection() { //Bind to the location service
        @Override
        public void onServiceConnected(ComponentName name, IBinder service){
            locationService = ((LocationService.LocationServiceBinder)service).getService();
            locationService.StartLocationServices();
            locationServiceBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            locationServiceBound = false;
        }
    };

    //Save Image Service Variables
    private Boolean saveImageServiceBound = false;
    private SaveImageService saveImageService; //Reference to save image service
    private ServiceConnection saveImageServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            saveImageService = ((SaveImageService.SaveImageServiceBinder) service).getService();
            saveImageServiceBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            saveImageServiceBound = false;
        }
    };

    //Send Image Data Service Variables
    private Boolean sendImageDataServiceBound = false;
    private SendImageDataService sendImageDataService; //Reference to send image data service
    private ServiceConnection sendImageDataServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            sendImageDataService = ((SendImageDataService.SendImageDataServiceBinder)service).getService();
            sendImageDataServiceBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            sendImageDataServiceBound = false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //sets the screen to activity_camera.xml screen
        setContentView(R.layout.activity_camera);
        if (null == savedInstanceState) {
            camera2BasicFragment = Camera2BasicFragment.newInstance();
            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.screenLayout_Camera, camera2BasicFragment)
                    .commit();
        }

        locateControls();
        checkUserPermissions();
        setSupportActionBar(bottomAppBar);
        setButtonEventListeners();
    }
    @Override
    protected void onStart() {
        super.onStart();

        //Start App Services
        startLocationService();
        startSaveImageService();
        startSendImageDataService();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (locationServiceConnection != null) {
            unbindService(locationServiceConnection);
        }
        if (saveImageServiceConnection!= null) {
            unbindService(saveImageServiceConnection);
        }
        if (sendImageDataServiceConnection!= null) {
            unbindService(sendImageDataServiceConnection);
        }
    }

    //Bind Location services
    private void startLocationService() {
        if (locationServiceBound == false) {
            Intent locnIntent = new Intent(this, LocationService.class);
            bindService(locnIntent, locationServiceConnection, Context.BIND_AUTO_CREATE);
        }
    }

    //Bind Image saving for file I/O
    private void startSaveImageService() {
        if (saveImageServiceBound == false ) {
            Intent imgSaveIntent = new Intent(this, SaveImageService.class);
            bindService(imgSaveIntent, saveImageServiceConnection, Context.BIND_AUTO_CREATE);
        }
    }
    //Bind Image saving for file I/O
    private void startSendImageDataService() {
        if (sendImageDataServiceBound == false ) {
            Intent imgSaveIntent = new Intent(this, SendImageDataService.class);
            bindService(imgSaveIntent, sendImageDataServiceConnection, Context.BIND_AUTO_CREATE);
        }
    }
    private void locateControls(){
        // locate controls
        try {
            bottomAppBar = findViewById(R.id.bottom_app_bar);

            screenLayout_Camera = findViewById(R.id.screenLayout_Camera);
            screenLayout_Review = findViewById(R.id.screenLayout_Review);
            btnCamera = findViewById(R.id.btnCamera);
            btnBack = findViewById(R.id.btnBack);
            buttonPanel = findViewById(R.id.buttonPanel);
            btnNavBar_Send = findViewById(R.id.btnNavBar_Send);
        } catch(Exception ex){
            Log.e(TAG, "Exception caught when locating CameraActivity View's controls.");
            ex.printStackTrace();
        }

    }

    private void setButtonEventListeners(){
        // set button onclick events
        btnCamera.setOnClickListener((View v) -> {
            takePhoto();
        });
        btnBack.setOnClickListener((View v) -> {
            switchScreen(ScreenTransition.ToPreview);
        });
        btnNavBar_Send.setOnClickListener((View v) -> {
            showLabels();
        });
    }

    private void checkUserPermissions(){
        // check permissions
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED  ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) !=
                        PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_PERMISSION);
        }
    }

    private void showLabels(){
        // TODO show top labels and user editing controls
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = this.getMenuInflater();
        inflater.inflate(R.menu.bottomappbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()){
            case android.R.id.home:
                BottomNavigationDrawerFragment bottomNavDrawerFragment = new BottomNavigationDrawerFragment();
                bottomNavDrawerFragment.show(this.getSupportFragmentManager(), bottomNavDrawerFragment.getTag());
                break;
        }

        //TODO other nav bar menu items

        return true;
    }
    // TODO - this should be a View transition, if sending an object to a new View is efficient enough (yikes)
    private void switchScreen(ScreenTransition transition){
        if(transition == ScreenTransition.ToReview){
            screenLayout_Review.setVisibility(View.VISIBLE);
            screenLayout_Camera.setVisibility(View.INVISIBLE);
        }
        else if(transition == ScreenTransition.ToPreview){
            screenLayout_Camera.setVisibility(View.VISIBLE);
            screenLayout_Review.setVisibility(View.INVISIBLE);
        }
    }
    private void takePhoto(){

        // Make a copy of the Camera2BasicFragment as is
        Camera2BasicFragment camera2BasicFragment_copy = camera2BasicFragment;
        Bitmap imgCapture = ARGBBitmap(camera2BasicFragment_copy.getTextureView().getBitmap());

        Drawable d = new BitmapDrawable(getResources(), imgCapture);

        screenLayout_Review.setBackground(d);

        String outputPhotoPath = saveImageService.SaveImage(imgCapture);
        if (outputPhotoPath != null) {
            Log.d("FileSaving","Saved Photo succesfully");
        }

        //Get the last known location for the photo
        Location photoLocation = locationService.getLocation();

        //Send the photo with or without location
        if (photoLocation != null) {
            ImageData data = new ImageData(imgCapture, photoLocation.getLongitude(), photoLocation.getLatitude(), photoLocation.getAltitude());
            sendImageDataService.SendImageData(data);
        } else {
            ImageData data = new ImageData(imgCapture);
            sendImageDataService.SendImageData(data);
        }


        switchScreen(ScreenTransition.ToReview);
    }

    private Bitmap ARGBBitmap(Bitmap img) {
        return img.copy(Bitmap.Config.ARGB_8888,true);
    }

}


