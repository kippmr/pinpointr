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
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class SendClassificationData extends Service {

    private static final String TAG = "TfLiteCameraDemo";
    private String serverUrl = "https://pinpointr-test.azurewebsites.net";
    private String postSubmissionUrl = "/api/Submission/PostSubmission";
    private String postImageUrl = "/api/Submission/PostImage";
    private String img_Url = "";
    private RequestQueue mRequestQueue;
    private StringRequest stringRequest;
    private JSONObject jsonObject;



    public boolean SendSubmission(ImageData data) {
        mRequestQueue = Volley.newRequestQueue(this);
        StringBuilder tags = new StringBuilder();
        if (data.modelGeneratedLabels != null && !data.modelGeneratedLabels.isEmpty()) {
            for (String tag : data.modelGeneratedLabels) { //Add the model generated tags
                tags.append(tag + ",");
            }
        }
        if (data.modelGeneratedLabels != null && !data.userCorrectedLabels.isEmpty()) {
            for (String tag : data.userCorrectedLabels) { //Add the user corrected tags
                tags.append(tag + ",");
            }
        }
        if (tags.length() > 0) {
            tags.substring(0, tags.length() - 1); //Remove last comma in tags
        }
        //String img = getStringImage(data.bitmapImage);
        //SendImage(img);
        SendImageData(0, Double.toString(data.longitude) + "," + Double.toString(data.latitude), "30ee03f5-4955-4bcd-9430-0018ef8d7778", data.altitude);
        return true;

    }

    public boolean SendImageData(int user_id, String coords, String image_url, double altitude) {

        Map<String, String> postParam= new HashMap<String, String>();
        postParam.put("name", "desk");
        postParam.put("user_submitted", Boolean.toString(true));
        postParam.put("ai_percentage", Integer.toString(0));

        JSONArray array = new JSONArray();
        array.put(new JSONObject(postParam));


        JsonArrayRequest jsrequest = new JsonArrayRequest(Request.Method.POST, serverUrl + postSubmissionUrl, array, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                    response.toString();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error sending image data to server" + error.toString());
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("user_id", Integer.toString(user_id));
                headers.put("coordinates", coords);
                headers.put("image_url", img_Url);
                headers.put("altitude", Double.toString(altitude));
                return headers;
            }
            @Override
            protected Response<JSONArray> parseNetworkResponse(NetworkResponse response) {
                String responseString;
                JSONArray array = new JSONArray();
                if (response != null) {

                    try {
                        responseString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
                        JSONObject obj = new JSONObject(responseString);
                        (array).put(obj);
                    } catch (Exception ex) {
                    }
                }
                //return array;
                return Response.success(array, HttpHeaderParser.parseCacheHeaders(response));
            }

        };
        mRequestQueue.add(jsrequest);
        return true;
    };

    public boolean SendImage(String img) {
        stringRequest = new StringRequest(Request.Method.POST, serverUrl + postImageUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e(TAG, response);
                    img_Url = response;
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
                params.put("file", img);
                return params;
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                return createPostBody(getParams()).getBytes();
            }

        };
        mRequestQueue.add(stringRequest);
        return true;
    }
    private String createPostBody(Map<String, String> params) {
        StringBuilder sbPost = new StringBuilder();
        if (params != null) {
            for (String key : params.keySet()) {
                if (params.get(key) != null) {
                    sbPost.append("\r\n" + "--" + "\r\n");
                    sbPost.append("Content-Disposition: form-data; name=\"" + key + "\"" + "\r\n\r\n");
                    sbPost.append(params.get(key).toString());
                }
            }
        }
        return sbPost.toString();
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
        Log.d("SendClassificationData", "Created new classification data service");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("SendClassificationData", "Received start id " + startId + ": " + intent);
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
