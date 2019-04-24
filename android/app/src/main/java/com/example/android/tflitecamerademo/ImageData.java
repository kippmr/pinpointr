package com.example.android.tflitecamerademo;

import android.graphics.Bitmap;
import android.location.Location;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import static java.sql.Types.NULL;

public class ImageData {
    // The image itself
    public Bitmap image;
    //Location data for the image
    private double longitude;
    private double latitude;
    private double altitude;

    //Campus location
    private int roomNumber;
    private int floorNumber;
    private String buildingName;


    // Did the user verify the labels (or just send?)
    public boolean UserVerified;

    //Blank if the user has not uploaded the image
    public String URL = "";

    public Location photoLocation;

    public long ClassificationTimeMs;

    public PriorityQueue<Map.Entry<String, Float>> SortedLabels = new PriorityQueue<>();
    public String errorString;


    public ImageData(Bitmap img,
                     double lon,
                     double lat,
                     double alt,
                     boolean verified)
    {
        this.image = img;
        this.longitude = lon;
        this.latitude = lat;
        this.altitude = alt;
        this.UserVerified = verified;
    }

    public ImageData(Bitmap img, boolean verified) {
        this(img, 0, 0, 0, verified);
    }

    public ImageData(Bitmap img, double lon, double lat, double alt) {
        this(img, lon, lat, alt, false);

    }

    public ImageData(Bitmap img) {
        this(img, 0, 0, 0, false);

    }

    public ImageData() {
    }

    public void SetURL(String url) {
        this.URL = url;
    }

    public void SetLocation(double lat, double lon, double alt) {
        this.latitude = lat;
        this.longitude = lon;
        this.altitude = alt;
    }

    public void SetCampusLocation(String buildName) {
        this.buildingName = buildName;
    }

    public void SetCampusLocation(String buildName, int floorNumber, int RoomNumber) {
        this.buildingName = buildName;
        this.roomNumber = roomNumber;
        this.floorNumber = floorNumber;
    }

    //For setting headers of request
    public String PrintCoords() {
        if (latitude != (NULL) && longitude != (NULL)) {
            return (Double.toString(latitude) + "," + Double.toString(longitude));
        } else {
            return "";
        }
    }

    public String PrintLatitude() {
        if (latitude != (NULL)) {
            return (Double.toString(latitude));
        } else {
            return "";
        }
    }
    public String PrintLongitude() {
        if (longitude != (NULL)) {
            return (Double.toString(longitude));
        } else {
            return "";
        }
    }

    public String PrintAltitude() {
        if (altitude != (NULL)) {
            return Double.toString(altitude);
        } else {
            return "";
        }
    }

    public String GetImageURL() {
        return URL;
    }
    public Double GetLong() {return longitude;}
    public Double GetLat() {return latitude;}
    public Double GetAlt() {return altitude;}
    public String GetBuildingName() {return buildingName;}
    public int GetFloorNumber(){ return floorNumber;}
    public int GetRoomNumber() {return roomNumber;}

    //Returns true if the Image has the required information to be sent in as a service request
    public boolean CheckComplete() {
        if ( this.latitude == NULL || this.longitude == NULL || this.latitude == 0.0 || this.longitude == 0.0 || this.SortedLabels == null || this.SortedLabels.isEmpty() || this.URL == null) {
            return false;
        } else {
            return true;
        }
    }
}
