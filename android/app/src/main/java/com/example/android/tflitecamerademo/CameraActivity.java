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
import android.content.DialogInterface;
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
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.AbstractMap;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.PriorityQueue;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;




/**
 * Main {@code Activity} class for the Camera app.
 */

public class CameraActivity extends AppCompatActivity implements ImageServiceCallbacks {


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

    //Tag Fragment views;
    TagListBottomSheetDialogFragment bottomSheet;
    Button btnNext;
    Button btnAddTag;

    Camera2BasicFragment camera2BasicFragment;


    public boolean classifyLocally = false;
    //TODO - should not be global
    ImageData imageData;
    //TODO - remove
    Location photoLocation;


    // Request permission codes
    public static final int REQUEST_PERMISSION = 200;

    // Tag for error logging
    private static final String TAG = CameraActivity.class.getSimpleName();



    //region Activity/View transitions

    //View to transitions to
    private enum ScreenTransition {
        ToReview,
        ToPreview
    }

    /**
     * Switch to a separate screen allowing the user to input their location
     * after the user has taken a photo and hit the next button
     */
    private void openEnterLocationActivity(){
        // Start NewActivity.class
        Intent myIntent = new Intent(CameraActivity.this,
                EnterLocationActivity.class);
        startActivity(myIntent);
    }

    /**
     * Switch views by hiding the old view and setting the new one to visible
     * TODO - this should be a View transition, if sending an object to a new View is efficient enough (yikes)
     * @param transition enum indicating the screen to switch to
     */
    private void switchScreen(ScreenTransition transition) {
        if (transition == ScreenTransition.ToReview) {
            screenLayout_Review.setVisibility(View.VISIBLE);
            screenLayout_Camera.setVisibility(View.INVISIBLE);
        } else if (transition == ScreenTransition.ToPreview) {
            screenLayout_Camera.setVisibility(View.VISIBLE);
            screenLayout_Review.setVisibility(View.INVISIBLE);
        }
    }
    //endregion

    //region Activity Lifecycle overrides

    /**
     * Callback to run after creating the activity
     * @param savedInstanceState previous saved state, null if the activity is being created for the first time
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //sets the screen to activity_camera.xml screen
        setContentView(R.layout.activity_camera);
        //Removed null check because we do want to recreate the fragment when the user turns the screen sideways
        camera2BasicFragment = Camera2BasicFragment.newInstance();
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.screenLayout_Camera, camera2BasicFragment)
                .commit();


        locateControls();
        RequestUserPermissions();
        setSupportActionBar(bottomAppBar);
        setButtonEventListeners();
    }

    /**
     * Callback to run after the activity becomes visible on the screen
     */
    @Override
    protected void onStart() {
        super.onStart();

        //Start App Services
        startLocationService();
        startSaveImageService();
        startSendImageDataService();
    }


    /**
     * Callback to run after the activity become visible again after being paused
     */
    @Override
    protected void onResume() {
        super.onResume();
        startLocationUpdates();
    }
    /**
     * Callback to run after the activity has become partially visible e.g. minimized
     */
    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }


    /**
     * Callback to run after the activity has been disposed of
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (locationServiceConnection != null) {
            unbindService(locationServiceConnection);
        }
        if (saveImageServiceConnection != null) {
            unbindService(saveImageServiceConnection);
        }
        if (sendImageDataServiceConnection != null) {
            unbindService(sendImageDataServiceConnection);
        }
    }



    //endregion

    //region Options Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = this.getMenuInflater();
        inflater.inflate(R.menu.bottomappbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                SetLabels();
                break;

        }
        return true;
    }
    //endregion

    //region Permissions
    /**
     * Request permissions for writing images to file, taking photos using the device camera, and accessing the device location
     */
    private void RequestUserPermissions(){
        // check permissions
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) !=
                        PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_PERMISSION);
        }
    }
    //endregion

    //region Service Binding and Calls
    //Location Service Variables
    private Boolean locationServiceBound = false;
    public static LocationService locationService; //Reference to the location service
    private ServiceConnection locationServiceConnection = new ServiceConnection() { //Bind to the location service
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            locationService = ((LocationService.LocationServiceBinder) service).getService();
            locationService.StartLocationServices();
            locationServiceBound = true;
            startLocationUpdates();
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
    public static SendImageDataService sendImageDataService; //Reference to send image data service
    private ServiceConnection sendImageDataServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            sendImageDataService = ((SendImageDataService.SendImageDataServiceBinder) service).getService();
            sendImageDataServiceBound = true;
            sendImageDataService.setCallbacks(CameraActivity.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            sendImageDataServiceBound = false;
        }
    };

    //Bind Location services
    private void startLocationService() {
        if (locationServiceBound == false) {
            Intent locnIntent = new Intent(this, LocationService.class);
            bindService(locnIntent, locationServiceConnection, Context.BIND_AUTO_CREATE);
        }
    }
    //Bind Image saving for file I/O
    private void startSaveImageService() {
        if (saveImageServiceBound == false) {
            Intent imgSaveIntent = new Intent(this, SaveImageService.class);
            bindService(imgSaveIntent, saveImageServiceConnection, Context.BIND_AUTO_CREATE);
        }
    }

    //Bind Image saving for file I/O
    private void startSendImageDataService() {
        if (sendImageDataServiceBound == false) {
            Intent imgSaveIntent = new Intent(this, SendImageDataService.class);
            bindService(imgSaveIntent, sendImageDataServiceConnection, Context.BIND_AUTO_CREATE);
        }
    }
    //Start Requesting Location Updates from the service
    private void startLocationUpdates() {
        if (locationServiceBound){
            locationService.StartLocationServices();
        }
    }

    //Stop Requesting Location Updates
    private void stopLocationUpdates() {
        if (locationServiceBound){
            locationService.StopLocationServices();
        }
    }
    //endregion

    //region Fragment and View binding
    /**
     * Locate views and fragments referenced by the activity e.g. when updating the label list
     */
    private void locateControls() {
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
        } catch (Exception ex) {
            Log.e(TAG, "Exception caught when locating CameraActivity View's controls.");
            ex.printStackTrace();
        }

    }

    /**
     * Get a reference to the buttons in the tag menu if they exist, and set the event listener for the next button
     */

    public void locateControlsInTagMenu() {
        if (bottomSheet != null && bottomSheet.tagListBottomFragment != null) {
            btnNext = bottomSheet.tagListBottomFragment.getNextButton();
            btnAddTag = bottomSheet.tagListBottomFragment.getAddTagButton();

            //Set the listener for going to the next menu fragment
            btnNext.setOnClickListener((View v) -> {
                UpdateLabelsFromView();
                if (!imageData.SortedLabels.isEmpty()) {
                    // TODO - Switch to next menu
                    sendImageDataService.GetLocationDatafromCoordinates();
                    openEnterLocationActivity();
                    //Remove the fragment
                    bottomSheet.dismiss();
                    bottomSheet = null;
                }
            });
        }
    }


    /**
     * Set the listeners for the camera button, back button, and send button
     */
    private void setButtonEventListeners() {
        btnCamera.setOnClickListener((View v) -> {
            takePhoto();
            switchScreen(ScreenTransition.ToReview);
            sendPhoto();
        });
        btnBack.setOnClickListener((View v) -> {
            switchScreen(ScreenTransition.ToPreview);
        });
        btnNavBar_Send.setOnClickListener((View v) -> {

        });

    }
    //endregion

    //region Label/Tag Model + View updates
    /**
     * Set the labels in the tag menu from the model stored in our Imagedata object
     */
    public void SetLabels() {
        if (bottomSheet == null) {
            bottomSheet = new TagListBottomSheetDialogFragment();
        }
        // We show the bottom sheet dialog fragment before filling the tags, because the bottom tag list
        // is not created until the bottom sheet dialog becomes visible
        if (!bottomSheet.isAdded()) {
            bottomSheet.showNow(getSupportFragmentManager(), "TAG");
        }
        PriorityQueue<Map.Entry<String, Float>> sortedLabels = imageData.SortedLabels;

        //Get Reference to the buttons in the tag menu fragment
        locateControlsInTagMenu();
        Iterator labelIterator = sortedLabels.iterator();
        final int size = sortedLabels.size();
        // TODO - the tags should be ordered by probability
        while (labelIterator.hasNext()) {
            Map.Entry<String, Float> label = (Map.Entry<String, Float>) labelIterator.next();
            String tag = label.getKey();
            String tagLong = tag + ", " + label.getValue();
            bottomSheet.tagListBottomFragment.addGeneratedTag(tag);
        }

    }

    /**
     *  Retrieves all current labels from the tag list fragment after user has updated them (adding/deleting tags) and updates our ImageData object accordingly
     *  TODO would be better if updates to the adapter in the fragment were directly tied to the ImageData object
     */

    private void UpdateLabelsFromView() {
        PriorityQueue<Map.Entry<String, Float>> newLabels = new PriorityQueue<>(                3,
                new Comparator<Map.Entry<String, Float>>() {
                    @Override
                    public int compare(Map.Entry<String, Float> o1, Map.Entry<String, Float> o2) {
                        return (o1.getValue()).compareTo(o2.getValue());
                    }
                });
        if (bottomSheet != null) {
            if (bottomSheet.tagListBottomFragment.adapter.getItemCount() > 0) {
                PriorityQueue<Map.Entry<String, Float>> sortedLabels = imageData.SortedLabels;
                Iterator labelIterator = sortedLabels.iterator();

                //We iterate over all the tags in our list, only adding tags that were not deleted from the original SortedLabels, or were added by the user
                for (String tag : bottomSheet.tagListBottomFragment.adapter.mTagListItems) {
                    boolean foundEntry = false;
                    while (labelIterator.hasNext()) {
                        Map.Entry<String, Float> labelEntry = (Map.Entry<String, Float>) labelIterator.next();
                        if (labelEntry.getKey() == tag) {
                            newLabels.add(labelEntry);
                            foundEntry = true;
                            break;
                        }
                    }
                    if (!foundEntry) {
                        Map.Entry<String, Float> newTag = new AbstractMap.SimpleEntry<String, Float>(tag, 100.0f);
                        newLabels.add(newTag);
                    }
                }
            }
        }
        imageData.SortedLabels.clear();
        imageData.SortedLabels = newLabels;
    }


    /**
     * Clear all the current labels from the tag list by recreating the entire object
     * TODO Better way to do this?
     */
    private void ResetLabels() {
        bottomSheet = new TagListBottomSheetDialogFragment();
    }
    //endregion

    //region App actions
    /**
     * Called when the user presses the shutter button from Camera Mode
     * Captures photo, initializes the ImageData object (classification, location), and switches
     * the screen to Review mode.
     **/
    private void takePhoto() {
        ResetLabels();
        // TODO - implement actual object clone OR clone useful object attributes
        Camera2BasicFragment camera2BasicFragment_copy = camera2BasicFragment;
        //If we are classifying remotely, we don't want to call local classification and we don't want to set the labels until we have received the classification data
        if (classifyLocally) {
            imageData = camera2BasicFragment_copy.getImageClassificationData();
            // TODO - replace with filltagsmenu
            SetLabels();
        } else {
            imageData = camera2BasicFragment_copy.getImageData();
        }
        setReviewScreenImage(camera2BasicFragment_copy);

        //Get the last known location for the photo
        photoLocation = locationService.getLocation();
        if (photoLocation != null) {
            //ImageData data = new ImageData(imageData.image, photoLocation.getLongitude(), photoLocation.getLatitude(), photoLocation.getAltitude());
            // TODO - add to ImageData constructor
            imageData.SetLocation(photoLocation.getLatitude(), photoLocation.getLongitude(), photoLocation.getAltitude());
        }
    }

    /**
     * Set the review screen image to the ones the user took
     * @param c2bf The users image
     */
    private void setReviewScreenImage(Camera2BasicFragment c2bf) {
        //TODO - fix screen squishing issue
        AutoFitTextureView textureView = c2bf.getTextureView();
        //Matrix m = c2bf.getTransformMatrix();
        //textureView.setTransform(m);
        Bitmap bitmap = textureView.getBitmap(textureView.mRatioWidth, textureView.mRatioHeight);
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), textureView.getTransform(c2bf.getTransformMatrix()), true);
        Drawable d = new BitmapDrawable(getResources(), bitmap);
        screenLayout_Review.setBackground(d);

    }

    /**
     * Send a captured photo to the server, called after the shutter button is pressed
     */
    private void sendPhoto() {
        showConfirmationDialog(sendPhotoToServer());
    }

    /**
     * Upload the image to azure image bucket. Then send the photo url to object detection model for classification
     * @return true if image was uploaded to the bucket successfully, false otherwise
     * // TODO - return boolean success
     */
    private boolean sendPhotoToServer() {
        //Send the photo with or without location
        return sendImageDataService.SendImageData(imageData);

    }

    /**
     * Alert the user to the status of the image upload
     * @param sentSuccessfully
     */
    private void showConfirmationDialog(boolean sentSuccessfully){
        AlertDialog alertDialog = new AlertDialog.Builder(CameraActivity.this).create();
        if(sentSuccessfully){
            alertDialog.setTitle("Success!");
            alertDialog.setMessage("Your image was sent successfully.");
        }
        else{
            alertDialog.setTitle("Oops!");
            alertDialog.setMessage("There was a problem sending your image. Please check your network status and try again.");
        }
        // Thanks for helping keep McMaster Clean ?
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    /**
     * Save the photo locally
     * @return true if the photo saved without errors, false otherwise
     */
    private boolean savePhotoLocally() {
        if (imageData.image != null) {
            String outputPhotoPath = saveImageService.SaveImage(imageData.image);
            if (outputPhotoPath != null) {
                Log.d("FileSaving", "Saved Photo successfully");
                return true;
            }
        }
        return false;
    }
    //endregion
}



