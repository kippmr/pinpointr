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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Iterator;
import java.util.Map;
import java.util.PriorityQueue;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


/** Main {@code Activity} class for the Camera app. */
public class CameraActivity extends AppCompatActivity {

    /* Screen Controls */

    /* V1.5 changes:
    Cleaning code
        - began adding function documentation
        - modularizing code
        - reducing global variables (e.g. ImageData object)
        - added version to commented-out code
    */

    RelativeLayout screenLayout_Camera;
    RelativeLayout screenLayout_Review;
    RelativeLayout buttonPanel;
    BottomAppBar bottomAppBar;
    ImageButton btnCamera;
    ImageButton btnBack;
    FloatingActionButton btnNavBar_Send;
    TextView tvLabels;
    TagListBottomSheetDialogFragment bottomSheet;

    Camera2BasicFragment camera2BasicFragment;
    //TODO - should not be global
    ImageData imageData;

    // Request permission codes
    public static final int REQUEST_IMAGE = 100;
    public static final int REQUEST_PERMISSION = 200;

    // Tag for error logging
    private static final String TAG = CameraActivity.class.getSimpleName();

    // (1.5) private String imageFilePath = "";

    private enum ScreenTransition{
        ToReview,
        ToPreview
    }

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
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = this.getMenuInflater();
        inflater.inflate(R.menu.bottomappbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()){
            case android.R.id.home:
                fillandShowTagsMenu();
                break;

        }
        return true;
    }

    private void fillandShowTagsMenu(){
        bottomSheet = new TagListBottomSheetDialogFragment();
        bottomSheet.showNow(getSupportFragmentManager(), "TAG");

        String textToShow = "";
        PriorityQueue<Map.Entry<String, Float>> sortedLabels = imageData.SortedLabels;
        Iterator labelIterator = sortedLabels.iterator();
        final int size = sortedLabels.size();
        // TODO - the tags should be ordered by probability
        while(labelIterator.hasNext()) {
            Map.Entry<String, Float> label = (Map.Entry<String, Float>)labelIterator.next();
            String tag = label.getKey();
            String tagLong = tag + ", "+ label.getValue();

            textToShow += "\n"+ tagLong;

            bottomSheet.tagListBottomFragment.addGeneratedTag(tag);
        }
        tvLabels.setText(textToShow);
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
            tvLabels = findViewById(R.id.labels);
        } catch(Exception ex){
            Log.e(TAG, "Exception caught when locating CameraActivity View's controls.");
            ex.printStackTrace();
        }

    }

    private void setButtonEventListeners(){
        // set button onclick events
        btnCamera.setOnClickListener((View v) -> {
            takePhoto();
            switchScreen(ScreenTransition.ToReview);
        });
        btnBack.setOnClickListener((View v) -> {
            switchScreen(ScreenTransition.ToPreview);
        });
        btnNavBar_Send.setOnClickListener((View v) -> {

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

    /**
     * Called when the user presses the shutter button from Camera Mode
     * Captures photo, initializes the ImageData object (classification, location), and switches
     * the screen to Review mode.
     **/
    private void takePhoto(){

        // TODO - implement actual object clone OR clone useful object attributes
        Camera2BasicFragment camera2BasicFragment_copy = camera2BasicFragment;
        // Instantiate the ImageData object
        imageData = camera2BasicFragment_copy.getImageClassificationData();
        setReviewScreenImage(camera2BasicFragment_copy);
        //TODO - make Location property of ImageData
        //Get the last known location for the photo
        Location photoLocation = locationService.getLocation();

        //TODO - move to btnNavBar_Send.setOnClickListener()
//        savePhotoLocally();
//        sendPhotoToServer(photoLocation);
    }

    private void setReviewScreenImage(Camera2BasicFragment c2bf){
        AutoFitTextureView textureView = c2bf.getTextureView();
        //Matrix m = c2bf.getTransformMatrix();
        //textureView.setTransform(m);
        Bitmap bitmap = textureView.getBitmap(textureView.mRatioWidth, textureView.mRatioHeight);
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), textureView.getTransform(c2bf.getTransformMatrix()), true);
        Drawable d = new BitmapDrawable(getResources(), bitmap);
        screenLayout_Review.setBackground(d);

    }



    private void savePhotoLocally(){
        if (imageData.image != null) {
            String outputPhotoPath = saveImageService.SaveImage(imageData.image);
            if (outputPhotoPath != null) {
                Log.d("FileSaving","Saved Photo successfully");
            }
        }
    }

    // TODO - photoLocation should be part of ImageData
    private void sendPhotoToServer(Location photoLocation){
        //Send the photo with or without location
        if (photoLocation != null) {
            //ImageData data = new ImageData(imageData.image, photoLocation.getLongitude(), photoLocation.getLatitude(), photoLocation.getAltitude());
            imageData.SetLocation(photoLocation.getLatitude(), photoLocation.getLongitude(), photoLocation.getAltitude());
            sendImageDataService.SendImageData(imageData);
        } else {
            sendImageDataService.SendImageData(imageData);
        }
    }

//    private Bitmap ARGBBitmap(Bitmap img) {
//        return img.copy(Bitmap.Config.ARGB_8888,true);
//    }

}

