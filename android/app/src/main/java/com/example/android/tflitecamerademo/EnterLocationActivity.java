package com.example.android.tflitecamerademo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Spinner;

import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import java.util.ArrayList;
import java.util.List;

public class EnterLocationActivity extends AppCompatActivity {




    MaterialBetterSpinner mbsBuilding;
    MaterialBetterSpinner mbsFloor;
    MultiAutoCompleteTextView mactvRoom;

    /* arrays for populating dropdowns */
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
        // TODO set the event listeners
    }

    private void getControls() {
        // locate controls
        try {
            mbsBuilding = findViewById(R.id.mbsBuilding);
            mbsFloor = findViewById(R.id.mbsFloor);
            mactvRoom = findViewById(R.id.mactvRoom);
        } catch (Exception ex) {
           // TODO
            ex.printStackTrace();
        }
    }

    private void setOnClickListeners(){
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
    }
    private void fillBuildingList(){
        // fill the array
        buildingsArray.add("building1");
        buildingsArray.add("building2");
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
        // Initialize the ArrayAdapter
        final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(this,R.layout.spinner_item,floorsArray);
        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
        mbsFloor.setAdapter(spinnerArrayAdapter);

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






