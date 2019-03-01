package com.example.android.tflitecamerademo;

import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

public class ImageData {
    // The image itself
    public Bitmap image;
    //Location data for the image
    private double longitude;
    private double latitude;
    private double altitude;
    // The label array generated by the model (ordered by probability)
    public List<String> modelGeneratedLabels;
    // The probability of the labels  (as generated by the model)
    public List<String> labelProbabilities;
    // The label array following user-correction
    public List<String> userCorrectedLabels;
    // Did the user verify the labels (or just send?)
    public boolean UserVerified;
    //Blank if the user has not uploaded the image
    public String URL = "";

    public long ClassificationTimeMs;

    public PriorityQueue<Map.Entry<String, Float>> SortedLabels;
    public String errorString;



    public ImageData(Bitmap img, double lon, double lat, double alt, List<String> modelLabels, List<String> userLabels, boolean verified){
        this.image = img;
        this.longitude = lon;
        this.latitude = lat;
        this.altitude = alt;
        this.modelGeneratedLabels = modelLabels;
        this.userCorrectedLabels = userLabels;
        this.UserVerified = verified;
    }

    public ImageData(Bitmap img, List<String> modelLabels, List<String> userLabels, boolean verified) {
        this(img, 0, 0, 0, modelLabels, userLabels, verified);
    }

    public ImageData(Bitmap img, double lon, double lat, double alt){
        this(img, lon, lat, alt, new ArrayList<String>(), new ArrayList<String>(), false);

    }

    public ImageData(Bitmap img){
        this(img, 0, 0, 0, new ArrayList<String>(), new ArrayList<String>(), false);

    }

    public ImageData(){}

    public void SetURL(String url) {
        this.URL = url;
    }

    public void SetLocation(double lat, double lon, double alt) {
        this.latitude = lat;
        this.longitude = lon;
        this.altitude = alt;
    }


    //For setting headers of request
    public String PrintCoords() {
        return (Double.toString(latitude) + "," + Double.toString(latitude));
    }

    public String PrintAltitude() {
        return Double.toString(altitude);
    }

    public String GetImageURL() {
        return URL;
    }


}
