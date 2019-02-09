package com.example.android.tflitecamerademo;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Binder;
import android.os.IBinder;
import android.util.Base64;
import android.util.Log;

import com.android.volley.*;
import com.android.volley.toolbox.*;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class SendClassificationData extends Service {

    private static final String TAG = "TfLiteCameraDemo";
    private String url = "http://pinpointr-test.us-east-2.elasticbeanstalk.com";

    private RequestQueue mRequestQueue;
    private StringRequest stringRequest;
    private JSONObject jsonObject;

    public boolean SendImage(ImageData data) {
        mRequestQueue = Volley.newRequestQueue(this);
        StringBuilder tags = new StringBuilder();
        if (!data.modelGeneratedLabels.isEmpty()) {
            for (String tag : data.modelGeneratedLabels) { //Add the model generated tags
                tags.append(tag + ",");
            }
        }
        if (!data.userCorrectedLabels.isEmpty()) {
            for (String tag : data.userCorrectedLabels) { //Add the user corrected tags
                tags.append(tag + ",");
            }
        }
        if (tags.length() > 0) {
            tags.substring(0, tags.length() - 1); //Remove last comma in tags
        }
        String img = getStringImage(data.bitmapImage);

        stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e(TAG, response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                        } catch (JSONException e) {
                            Log.e(TAG, "Error parsing JSON object from server");
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error sending image data to server");
            }
        }) {
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("tags", tags.toString());
                params.put("coordinates", Double.toString(data.longitude) + "," + Double.toString(data.latitude));
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("file", img);
                return params;
            }

        };
        mRequestQueue.add(stringRequest);
        return true;
    }

    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    @Override
    public void onCreate() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("LocalService", "Received start id " + startId + ": " + intent);
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new MyBinder();
    }

    public class MyBinder extends Binder {
        public SendClassificationData getService() {
            return SendClassificationData.this;
        }
    }
}
