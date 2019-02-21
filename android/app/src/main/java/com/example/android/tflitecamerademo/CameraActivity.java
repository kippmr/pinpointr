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
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
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
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
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

        switchScreen(ScreenTransition.ToReview);
    }

    private Bitmap ARGBBitmap(Bitmap img) {
        return img.copy(Bitmap.Config.ARGB_8888,true);
    }


}


