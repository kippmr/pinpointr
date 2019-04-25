package com.example.android.tflitecamerademo;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Spinner;

import com.google.android.material.textfield.TextInputEditText;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import java.util.ArrayList;
import java.util.List;

public class EnterLocationActivity extends AppCompatActivity {

    public static ImageData imageData;

    MaterialBetterSpinner mbsBuilding;
    MaterialBetterSpinner mbsFloor;
    MultiAutoCompleteTextView mactvRoom;
    TextInputEditText mactvDescription;
    Button btnSubmit;

    /* arrays for populating Dropdowns */
    List<String> buildingsArray =  new ArrayList<String>();
    List<String> floorsArray =  new ArrayList<String>();
    List<String> roomsArray =  new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_location);
        getControls();

        // TODO - fill the dropdownlists
        fillBuildingList();
        fillFloorList();
        fillRoomList();

        setBuildingNameFromImageData();

        // TODO set the event listeners
        setOnClickListeners();

    }

    private void setBuildingNameFromImageData() {
        if (CameraActivity.imageData.GetBuildingName() != "") {
            mbsBuilding.setText(CameraActivity.imageData.GetBuildingName());
        }
    }

    private void getControls() {
        // locate controls
        try {
            btnSubmit = findViewById(R.id.btnSubmit);
            mactvDescription = findViewById(R.id.mactvDescription);
            mbsBuilding = findViewById(R.id.mbsBuilding);
            mbsFloor = findViewById(R.id.mbsFloor);
            mactvRoom = findViewById(R.id.mactvRoom);

        } catch (Exception ex) {
           // TODO
            ex.printStackTrace();
        }
    }

    private void setOnClickListeners() {
        mbsBuilding.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // your code here
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

        btnSubmit.setOnClickListener((View v) -> {
            UpdateImageData();
            if (CameraActivity.sendImageDataService.SendClassificationData()) {
                //If request was sent successfully, alert the user and switch back to the camera activity
                showConfirmationDialog(true);

            } else {
                showConfirmationDialog(false);
            }
        });
    }

    private void UpdateImageData() {
        String building = mbsBuilding.getText().toString();
        String floor = mbsFloor.getText().toString();
        String room = mactvRoom.getText().toString();
        String comment = mactvDescription.getText().toString();
        CameraActivity.imageData.SetCampusLocation(building, room);
        CameraActivity.imageData.SetComment(comment);
        Log.e("Updating img data info", building + " " + room);
        Log.e("Updating img data info", comment);
    }

    /**
     * Alert the user to the status of the submission
     * @param sentSuccessfully true if the submission was able to be sent to the server, false otherwise
     */
    private void showConfirmationDialog(boolean sentSuccessfully){
        AlertDialog alertDialog = new AlertDialog.Builder(EnterLocationActivity.this).create();
        if(sentSuccessfully){
            alertDialog.setTitle("Success!");
            alertDialog.setMessage("Your submission was sent successfully. An email was sent to campus services with the information you provided");
        }
        else{
            alertDialog.setTitle("Oops!");
            alertDialog.setMessage("There was a problem sending your submission. Please ensure you have filled in all the required information");
        }
        // Thanks for helping keep McMaster Clean ?
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        openCameraActivity();
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

        /**
         * Switch back to the camera screen after the user has submitted their submission
         *
         */
        private void openCameraActivity(){
            // Start NewActivity.class
            Intent myIntent = new Intent(EnterLocationActivity.this,
                    CameraActivity.class);
            startActivity(myIntent);
        }

//        spFloor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
//                // your code here
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parentView) {
//                // your code here
//            }
//
//        });
//        mactvRoom.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
//                // your code here
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parentView) {
//                // your code here
//            }
//
//        });
    private void fillBuildingList(){
        // fill the array
        buildingsArray.add("ETB");
        buildingsArray.add("Hatch");
        buildingsArray.add("IAHS");
        buildingsArray.add("ITB");
        buildingsArray.add("JHE");
        buildingsArray.add("Thode");
        // Initialize the ArrayAdapter
        final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(this,R.layout.spinner_item,buildingsArray);
        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
        mbsBuilding.setAdapter(spinnerArrayAdapter);


    }
    private void fillFloorList(){
        // fill the array
        floorsArray.add("B1");
        floorsArray.add("1");
        floorsArray.add("2");
        floorsArray.add("3");
        floorsArray.add("4");
        floorsArray.add("5");
        // Initialize the ArrayAdapter
        final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(this,R.layout.spinner_item,floorsArray);
        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
        mbsFloor.setAdapter(spinnerArrayAdapter);

    }

    private int getIndex(MaterialBetterSpinner spinner, String searchString) {
        for (int i=0;i<spinner.getAdapter().getCount();i++){
            if (spinner.getAdapter().getItem(i).toString().equalsIgnoreCase(searchString)){
                return i;
            }
        }
        return -1;
    }

    private void fillRoomList() {
        // fill the array
        roomsArray.add("B100");
        roomsArray.add("B101");
        roomsArray.add("100");
        roomsArray.add("101");
        // Initialize the ArrayAdapter
        final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, roomsArray);
        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
        mactvRoom.setAdapter(spinnerArrayAdapter);
        mactvRoom.setThreshold(1);
        mactvRoom.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());

//        Button button = findViewById(R.id.button);
//        if (button != null) {
//            button.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    String text = "Entered text:" + " " + multiAutoCompleteTextView.getText();
//                    Toast.makeText(MainActivity.this, text, Toast.LENGTH_SHORT).show();
//                }
//            });


    }
}






