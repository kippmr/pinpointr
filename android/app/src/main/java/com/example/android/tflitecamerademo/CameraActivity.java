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
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v13.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


/** Main {@code Activity} class for the Camera app. */
public class CameraActivity extends Activity {

    ImageView imgView_Camera;
    ImageView imgView_Review;

    RelativeLayout screenLayout_Camera;
    RelativeLayout screenLayout_Review;

    Button btnCamera;
    Button btnBack;
    Button btnSend;

    TextView tvLabels;

    public static final int REQUEST_IMAGE = 100;
    public static final int REQUEST_PERMISSION = 200;
    private String imageFilePath = "";

    private Camera2BasicFragment camera2BasicFragment;


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




        // check permissions
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_PERMISSION);
        }


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
        btnSend = findViewById(R.id.btnSend);
        tvLabels = findViewById(R.id.tvLabels);
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

    private void setReviewLabels(Camera2BasicFragment fragment){
        // TODO: this doesn't work
        //tvLabels.setText(fragment.GetClassifierText());
    }


    private void takePhoto(){

        // Make a copy of the Camera2BasicFragment as is
        Camera2BasicFragment camera2BasicFragment_copy = camera2BasicFragment;
        setReviewImage(camera2BasicFragment_copy);
        //setReviewLabels(camera2BasicFragment_copy);


        switchToReviewScreen();
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


