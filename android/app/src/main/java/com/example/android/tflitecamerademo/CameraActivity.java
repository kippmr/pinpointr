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
import android.app.Activity;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
//import android.support.v13.app.ActivityCompat;


import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.os.IBinder;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import android.util.Log;


import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.io.*;
import java.util.*;

/** Main {@code Activity} class for the Camera app. */
public class CameraActivity extends Activity implements MyRecyclerViewAdapter.ItemClickListener{

    /* Screen Controls */
    ImageView imgView_Camera;
    ImageView imgView_Review;
    RelativeLayout screenLayout_Camera;
    RelativeLayout screenLayout_Review;
    RelativeLayout buttonPanel;

    Button btnCamera;
    Button btnBack;



    FloatingActionButton btnNavBar_Main;

    TextView tvLabels;
    RecyclerView recyclerView;

    ImageData imgData;

    MyRecyclerViewAdapter adapter;

    boolean showLabels;



    public static final int REQUEST_IMAGE = 100;
    public static final int REQUEST_PERMISSION = 200;
    public static final int REQUEST_LOCATION = 300;

    private String imageFilePath = "";

    private Camera2BasicFragment camera2BasicFragment;
    private LocationActivity locationactivity = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //sets the screen to activity_camera.xml screen
        setContentView(R.layout.activity_camera);
        if (null == savedInstanceState) {
            camera2BasicFragment = Camera2BasicFragment.newInstance();
            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, camera2BasicFragment)
                    .commit();
        }

        locateControls();
        checkUserPermissions();
        showLabels = false;

        // set button onclick events
        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePhoto();
            }
        });


        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchToCameraScreen();
            }
        });

        btnNavBar_Main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLabels();
            }
        });

        ArrayList<String> imgLabels = new ArrayList<>();
        //TODO move recyclerView data binding to takePhoto() stage
        imgLabels.add("Label1");
        imgLabels.add("Label2");
        imgLabels.add("Label3");
        imgLabels.add("Label4");
        imgLabels.add("Label5");

        // set up the RecyclerView
        recyclerView = findViewById(R.id.rvLabels);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MyRecyclerViewAdapter(this, imgLabels);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);

        // TODO - send button click listener
    }

    private void locateControls(){
        // locate controls
        imgView_Camera = findViewById(R.id.imgView_Camera);
        imgView_Review = findViewById(R.id.imgView_Review);
        screenLayout_Camera = findViewById(R.id.screenLayout_Camera);
        screenLayout_Review = findViewById(R.id.screenLayout_Review);
        btnCamera = findViewById(R.id.btnCamera);
        btnBack = findViewById(R.id.btnBack);

        tvLabels = findViewById(R.id.tvLabels);
        buttonPanel = findViewById(R.id.buttonPanel);
        btnNavBar_Main = findViewById(R.id.btnNavBar_Main);
    }

    private void checkUserPermissions(){
        // check permissions
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_PERMISSION);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_PERMISSION);
        }
    }

    private void showLabels(){
        showLabels = !showLabels;
        if(showLabels){
            recyclerView.setVisibility(View.VISIBLE);
        }
        else{
            recyclerView.setVisibility(View.INVISIBLE);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = this.getMenuInflater();
        inflater.inflate(R.menu.bottomappbar_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        //TODO nav menu bar item click handler (handle by id)
        if (item.getItemId() == btnNavBar_Main.getId()){

        }

        return true;
    }



    @Override
    public void onItemClick(View view, int position) {
        //Toast.makeText(this, "You clicked " + adapter.getItem(position) + " on row number " + position, Toast.LENGTH_SHORT).show();
    }



    private void switchToReviewScreen(){
        screenLayout_Review.setVisibility(View.VISIBLE);
        screenLayout_Camera.setVisibility(View.INVISIBLE);
    }

    private void switchToCameraScreen(){

        screenLayout_Camera.setVisibility(View.VISIBLE);
        screenLayout_Review.setVisibility(View.INVISIBLE);
    }

    private void setReviewImage(Camera2BasicFragment fragment){

        Bitmap imgCapture = fragment.getTextureView().getBitmap();
        imgView_Review.setImageBitmap(imgCapture);
    }

//    private void addLabel(String text, int uniqueID){
//        // TODO: this doesn't work
//        //tvLabels.setText(fragment.GetClassifierText());
//
//
//        CheckedTextView ctvLabel = new CheckedTextView(this);
//        ctvLabel.setChecked(true);
//        ctvLabel.setText(text);
//        ctvLabel.setId(uniqueID);
//        ctvLabel.setTextSize(26);
//
//        if (ctvLabel != null) {
//            ctvLabel.setChecked(false);
//            ctvLabel.setCheckMarkDrawable(android.R.drawable.checkbox_off_background);
//
//            ctvLabel.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    ctvLabel.setChecked(!ctvLabel.isChecked());
//                    ctvLabel.setCheckMarkDrawable(ctvLabel.isChecked() ? android.R.drawable.checkbox_on_background : android.R.drawable.checkbox_off_background);
//
////                    String msg = getString(R.string.pre_msg) + " " + (checkedTextView.isChecked() ? getString(R.string.checked) : getString(R.string.unchecked));
////                    Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
//                }
//            });
//        }
//
//        // Set the label's layout with margins on the side and bottom
//        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//        lp.setMargins(20,5,20,5);
//        labelList.addView(ctvLabel, lp);
//
//
//    }


//    private void clearLabels(){
//        // TODO clear all labels from labelsList
//        if((labelList).getChildCount() > 0)
//            labelList.removeAllViews();
//    }


    File galleryFolder = null;
    private SendClassificationData dataSender;
    // capture the
    private void takePhoto(){

        // Make a copy of the Camera2BasicFragment as is
        Camera2BasicFragment camera2BasicFragment_copy = camera2BasicFragment;

        Bitmap imgCapture = camera2BasicFragment_copy.getTextureView().getBitmap();
        savePhoto(imgCapture);
        imgView_Review.setImageBitmap(imgCapture);

        if (locationactivity == null) {
            Intent locnIntent = new Intent(CameraActivity.this, LocationActivity.class);
            startActivityForResult(locnIntent, REQUEST_LOCATION);
        }



        Intent sendDataIntent = new Intent(CameraActivity.this, SendClassificationData.class);
        ServiceConnection conn = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                dataSender = ((SendClassificationData.MyBinder) service).getService();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        };

        if (bindService(sendDataIntent, conn, 0)) {
            dataSender.SendImage(new ImageData(imgCapture, lastPhotoLongitude, lastPhotoLatitude, lastPhotoAltitude, null, null, false));
        } else {
            Log.d("DataBinding", "Could not bind to data sender service");
        };


        //List<String> imgLabels = Arrays.asList("Label1", "Label2", "Label3", "Label 4");


        //addAllLabelsToListView(imgLabels);
        switchToReviewScreen();
    }



    private void savePhoto(Bitmap img){
        FileOutputStream outputPhoto = null;
        if (galleryFolder == null) {
            createImageGallery();
        }
        try {
            outputPhoto = new FileOutputStream(createImageFile(galleryFolder));
            img.compress(Bitmap.CompressFormat.PNG, 100, outputPhoto);
        }
        catch (IOException e) {
            e.printStackTrace();
//          return;
        }
    }

    private File createImageFile(File galleryFolder) throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "image_" + timeStamp + "_";
        return File.createTempFile(imageFileName, ".jpg", galleryFolder);

    }

    private void createImageGallery() {
        File storageDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        galleryFolder = new File(storageDirectory, getResources().getString(R.string.app_name));
        if (!galleryFolder.exists()) {
            boolean wasCreated = galleryFolder.mkdirs();
            if (!wasCreated) {
                Log.e("CapturedImages", "Failed to create directory");
            }
        }
    }

    double lastPhotoLongitude = 0;
    double lastPhotoLatitude = 0;
    double lastPhotoAltitude = 0;

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

       if (requestCode == REQUEST_IMAGE) {
          if (resultCode == RESULT_OK) {
          }
          else if (resultCode == RESULT_CANCELED) {
          }
      }
      else if (requestCode ==REQUEST_LOCATION) {
          if (resultCode == RESULT_OK) {
              Bundle locnDataBundle = data.getExtras();
              if (!locnDataBundle.isEmpty()) {
                  double[] locnArray = locnDataBundle.getDoubleArray("location");
                  lastPhotoLongitude = locnArray[0];
                  lastPhotoLatitude = locnArray[1];
                  lastPhotoAltitude = locnArray[2];
              }


          }
          else if (resultCode == RESULT_CANCELED) {
          }
       }
  }

    //**********************************************************************************************
    // </ TODO - CODE FOR DELEGATING TO ANDROID CAMERA & SAVING IMG FILE LOCALLY
    //**********************************************************************************************
//    private void openCameraIntent() {
//        Intent pictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        if (pictureIntent.resolveActivity(getPackageManager()) != null) {
//
//            File photoFile = null;
//            try {
//                photoFile = createImageFile();
//            }
//            catch (IOException e) {
//                e.printStackTrace();
//                return;
//            }
//            Uri photoUri = FileProvider.getUriForFile(this, getPackageName() +".provider", photoFile);
//            pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
//            startActivityForResult(pictureIntent, REQUEST_IMAGE);
//        }
//    }
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//
//        if (requestCode == REQUEST_PERMISSION && grantResults.length > 0) {
//            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                Toast.makeText(this, "Thanks for granting Permission", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == REQUEST_IMAGE) {
//            if (resultCode == RESULT_OK) {
//                imgView_Camera.setImageURI(Uri.parse(imageFilePath));
//
//
//            }
//            else if (resultCode == RESULT_CANCELED) {
//                Toast.makeText(this, "You cancelled the operation", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }
//    private File createImageFile() throws IOException{
//
//        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
//        String imageFileName = "IMG_" + timeStamp + "_";
//        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
//        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
//        imageFilePath = image.getAbsolutePath();
//
//        return image;
//    }
    //**********************************************************************************************
    // TODO - CODE FOR DELEGATING TO ANDROID CAMERA />
    //**********************************************************************************************



}


