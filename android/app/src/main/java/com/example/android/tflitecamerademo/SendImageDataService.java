package com.example.android.tflitecamerademo;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import javax.net.ssl.HttpsURLConnection;

import static com.loopj.android.http.AsyncHttpClient.log;

public class SendImageDataService extends Service {

    //AWS Custom Vision Services Connection Strings
    private final static String PREDICTION_SERVER = "https://southcentralus.api.cognitive.microsoft.com/customvision/v3.0/Prediction/ffdbd3a6-dda5-4c8c-9828-4b6653d17475/detect/iterations/Iteration6";
    private final static String PREDICTION_API = "/url";
    private final static String PREDICTION_KEY = "611391a6e2b84ec89a68bed3492a7ffc";
    private final static Float PREDICTION_THRESHOLD = 0.20f;

    //Pinpointr Database Connection Strings

    private final static String DB_SERVER = "https://pinpointr.azurewebsites.net";
    private final static String DB_POST_IMAGE_API = "/api/Submission/PostImage";
    private final static String DB_POST_SUBMISSION_API = "/api/Submission/PostSubmission";
    private final static String DB_VERIFY_LOCATION_API = "/api/Submission/VerifyLocation";


    private final static String S3_BUCKET_URL = "https://s3.us-east-2.amazonaws.com/pinpointrbucket/";

    //Default file upload data (since IMGURL is created by the server)
    private final static String IMG_NAME = "file";
    private final static String IMG_FILE_NAME = "test.jpg";

    //HTTP Client helper strings
    private final static String CRLF = "\r\n";
    private final static String TWOHYPHENS = "--";
    private final static String BOUNDARY = "*****";

    private ImageServiceCallbacks imgServiceCallbacks;




    //Check if we received enough classification and location data to make a service request
    public boolean CheckImageDataComplete(){
        if (this.imgData.CheckComplete()) {
            return true;
        }
        return false;
    }

    public SendImageDataService() {

    }
    private ImageData imgData;

    public boolean SendImageData(ImageData imageData) {
        this.imgData = imageData;
        AsyncTask sendImgTask = new SendImageTask(this).execute(this.imgData.image);
        return true;
    }

    //Called when the user hits the submit button
    public boolean SendClassificationData() {
        if (CheckImageDataComplete()) {
            log.d("ClassificationData","Received response " + this.imgData.GetImageURL());
            AsyncTask sendImageClassDataTask = new SendImageClassDataTask(imgData).execute();
            return true;
        }
        else {
            log.d("ClassificationData", "Insufficient data to send request");
        }
        return false;
    }

    public boolean GetRemoteClassificationInfo(String imgUrl) {
        log.d("RemoteClassification","Sending Image data for image at URL " + S3_BUCKET_URL + imgUrl);
        this.imgData.SetURL(imgUrl);
        AsyncTask getRemoteClassificationInfoTask = new GetRemoteClassificationInfoTask(this).execute();
        return true;
    }

    public boolean GetLocationDatafromCoordinates() {
        if (this.imgData.PrintCoords() != "") {
            log.d("Coordinate data","Sending coordinate data " + this.imgData.PrintCoords());
            // TODO Remote location, for now location is checked against local files
/*            AsyncTask getLocationDataTask = new GetLocationDataFromCoordinatesTask(this).execute();
            return true;*/
            return true;
        }
        else {
            log.d("Unable to send Coordinate data","Location not set");
            return false;
        }
    }



    public boolean SetLocationOnCampus(String location) {
        log.d("Location data","Setting location data " + location);
        return true;
    };

    @Override
    public IBinder onBind(Intent intent) {
        return new SendImageDataService.SendImageDataServiceBinder();
    }

    public void setCallbacks(ImageServiceCallbacks i) {
        imgServiceCallbacks = i;
    }

    public class SendImageDataServiceBinder extends Binder {
        public SendImageDataService getService() {return SendImageDataService.this; }
    }

    private static class GetLocationDataFromCoordinatesTask extends AsyncTask<Void, Void, String> {
        private SendImageDataService caller;

        GetLocationDataFromCoordinatesTask(SendImageDataService caller) {
            this.caller = caller;
        }

        @Override
        protected void onPostExecute(String s) {
            caller.SetLocationOnCampus(s);
        }

        protected String doInBackground(Void... voids) { return GetLocationData();}

        private String GetLocationData() {
            HttpsURLConnection urlConnection = null;
            URL url = null;
            try {
                String urlString = DB_SERVER + DB_VERIFY_LOCATION_API;
                urlString += "?coordinates=" + caller.imgData.PrintLatitude() + "&coordinates=" + caller.imgData.PrintLongitude();
                urlString += "&image_url=" + caller.imgData.GetImageURL();
                urlString += "&altitude=" + caller.imgData.PrintAltitude();
                url = new URL(urlString);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            DataOutputStream request = null;
            try {

                Log.e("Server","Sending Location Data to Server");
                urlConnection = (HttpsURLConnection) url.openConnection();
                urlConnection.setUseCaches(false);
                urlConnection.setDoOutput(false);
                urlConnection.setDoInput(true);

                urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:56.0) Gecko/20100101 Firefox/56.0");
                urlConnection.setRequestProperty("Cache-Control", "no-cache");
                urlConnection.setRequestProperty("Accept-encoding", "gzip, deflate, br");


                Log.d("Server", caller.imgData.PrintCoords());
                Log.d("Server", caller.imgData.GetImageURL());
                Log.d("Server", caller.imgData.PrintAltitude());

                Log.d("Server","Opening Data Connection");
                urlConnection.connect();


                Log.d("Server","Sent Location Data to Server");

                //Read data from the response
                String status = urlConnection.getResponseMessage();
                Log.e("status", status);
               InputStream responseStream =  new GZIPInputStream(urlConnection.getInputStream());
                BufferedReader responseStreamReader = new BufferedReader(new InputStreamReader(responseStream, "utf-8"));
                String line;
                String responseText = "";
                while ((line = responseStreamReader.readLine()) != null) {
                    Log.e("response", line);
                    responseText += line;
                }
                Log.d("Server","Received Response from Server");

                JSONObject jsonResponse = new JSONObject(responseText);

                responseStreamReader.close();
                responseStream.close();
                urlConnection.disconnect();
                return jsonResponse.toString();


            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private static class SendImageClassDataTask extends AsyncTask<Void, Void, String> {

        private ImageData data;
        SendImageClassDataTask(ImageData data) {
            this.data = data;
        }

        protected String doInBackground(Void...voids) {
            return SendClassificationData();
        }

        private String SendClassificationData() {
            HttpsURLConnection urlConnection = null;
            URL url = null;
            try {
                url = new URL(DB_SERVER + DB_POST_SUBMISSION_API);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            DataOutputStream request = null;

            try {
                Log.e("Server","Sending Classification Data to Server");
                urlConnection = (HttpsURLConnection) url.openConnection();
                urlConnection.setUseCaches(false);
                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);
                urlConnection.setRequestProperty("user_id", "0");
                urlConnection.setRequestProperty("coordinates", data.PrintCoords());
                urlConnection.setRequestProperty("image_url", data.GetImageURL());
                urlConnection.setRequestProperty("altitude", data.PrintAltitude());
                urlConnection.setRequestProperty("building_name", data.GetBuildingName());
                urlConnection.setRequestProperty("room_number", data.GetRoomNumber());
                urlConnection.setRequestProperty("comment", data.GetComment());
                urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:56.0) Gecko/20100101 Firefox/56.0");
                urlConnection.setRequestProperty("Cache-Control", "no-cache");
                urlConnection.setRequestProperty("Accept-encoding", "gzip, deflate");
                urlConnection.setRequestProperty("content-type", "application/json");

                JSONArray tagsArray = new JSONArray();
                Iterator tagInfo = data.SortedLabels.iterator();
                while (tagInfo.hasNext()) {
                    Map.Entry<String, Float> tag = (Map.Entry<String, Float>) tagInfo.next();
                    JSONObject testTag = new JSONObject();
                    testTag.put("name", tag.getKey());
                    testTag.put("user_submitted", "false");
                    testTag.put("ai_percentage", tag.getValue());
                    tagsArray.put(testTag);
                }

                String requestText = "";
                if (tagsArray.length() > 0) {
                    requestText = tagsArray.toString();
                }



                Log.d("Server", data.PrintCoords());
                Log.d("Server", data.GetImageURL());
                Log.d("Server", data.PrintAltitude());
                Log.d("Server", requestText);
                //Open Request
                request = new DataOutputStream(urlConnection.getOutputStream());

                Log.d("Server","Opening Data Connection");
                //Write data to the request
                request.writeBytes(requestText);
                request.flush();
                request.close();
                Log.d("Server","Sent Classification Data to Server");

                //Read data from the response
                String status = urlConnection.getResponseMessage();
                Log.e("status", status);
                InputStream responseStream =(urlConnection.getInputStream());
                BufferedReader responseStreamReader = new BufferedReader(new InputStreamReader(responseStream, "utf-8"));
                String line;
                String responseText = "";
                while ((line = responseStreamReader.readLine()) != null) {
                    Log.e("response", line);
                    responseText += line;
                }
                Log.d("Server","Received Response from Server");

                JSONObject jsonResponse = new JSONObject(responseText);

                responseStreamReader.close();
                responseStream.close();
                urlConnection.disconnect();
                return jsonResponse.toString();


            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private static class SendImageTask extends AsyncTask<Bitmap, Void, String> {



        private SendImageDataService caller;

        SendImageTask(SendImageDataService caller) {
            this.caller = caller;
        }

        @Override
        protected void onPostExecute(String s) {
            caller.GetRemoteClassificationInfo(s);
        }

        protected String doInBackground(Bitmap... imgs) {
            try {
                Bitmap img = imgs[0];
                if (img != null) return SendImage(imgs[0]);
                else return null;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        private String SendImage(Bitmap img) throws IOException {
            HttpsURLConnection urlConnection = null;
            URL url = new URL(DB_SERVER + DB_POST_IMAGE_API);
            DataOutputStream request = null;

            try {

                //Set Request Properties
                urlConnection = (HttpsURLConnection) url.openConnection();
                urlConnection.setUseCaches(false);
                urlConnection.setDoOutput(true);
                urlConnection.setChunkedStreamingMode(0);
                urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:56.0) Gecko/20100101 Firefox/56.0");
                urlConnection.setRequestProperty("Connection", "keep-Alive");
                urlConnection.setRequestProperty("Cache-Control", "no-cache");
                urlConnection.setRequestProperty("Accept-encoding", "gzip, deflate, br");
                urlConnection.setRequestProperty("Accept-Language", "en-GB,en-US;q=0.9,en;q=0.8,ja;q=0.7");
                urlConnection.setRequestProperty("content-type", "multipart/form-data; boundary=" + BOUNDARY);

                //Open Request
                request = new DataOutputStream(urlConnection.getOutputStream());


                //Write data to the request
                request.writeBytes(TWOHYPHENS + BOUNDARY + CRLF);
                request.writeBytes("Content-Disposition: form-data; name=\"" + IMG_NAME + "\";filename=\"" + IMG_FILE_NAME + "\"");
                request.writeBytes(CRLF);
                request.writeBytes("Content-Type: image/jpeg");
                request.writeBytes(CRLF);
                request.writeBytes(CRLF);
                byte[] pixels = getImageBytes(img);
                request.write(pixels);
                request.writeBytes(CRLF);
                request.writeBytes(TWOHYPHENS + BOUNDARY + TWOHYPHENS + CRLF);
                request.flush();
                request.close();


                //Read data from the response
                String status = urlConnection.getResponseMessage();
                String contentEncoding = urlConnection.getHeaderField("Content-Encoding");
                String contentType = urlConnection.getContentType();
                Log.e("status", status);

                InputStream responseStream = new GZIPInputStream(urlConnection.getInputStream());
                BufferedReader responseStreamReader = new BufferedReader(new InputStreamReader(responseStream, "utf-8"));
                String line;
                String content = "";
                while ((line = responseStreamReader.readLine()) != null) {
                    Log.e("response", line);
                    content += line + "\n";
                }

                responseStreamReader.close();
                responseStream.close();
                urlConnection.disconnect();
                return content;


            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private static class GetRemoteClassificationInfoTask extends AsyncTask<Void, Void, String> {

        private SendImageDataService caller;
        GetRemoteClassificationInfoTask(SendImageDataService caller) {
            this.caller = caller;
        }

        protected String doInBackground(Void...voids) {
            return SendImageURL();
        }

        @Override
        protected void onPostExecute(String s) {
            caller.imgServiceCallbacks.SetLabels();
        }

        private String SendImageURL() {
            HttpsURLConnection urlConnection = null;
            URL url = null;
            try {
                url = new URL(PREDICTION_SERVER + PREDICTION_API);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            DataOutputStream request = null;


            try {
                Log.e("Server", "Sending Classification Data to Server");
                urlConnection = (HttpsURLConnection) url.openConnection();
                urlConnection.setUseCaches(false);
                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);
                urlConnection.setRequestProperty("Prediction-Key", PREDICTION_KEY);
                urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:56.0) Gecko/20100101 Firefox/56.0");
                urlConnection.setRequestProperty("Cache-Control", "no-cache");
                urlConnection.setRequestProperty("Accept-encoding", "gzip, deflate");
                urlConnection.setRequestProperty("content-type", "application/json");

                String imgUrl = S3_BUCKET_URL + caller.imgData.GetImageURL();

                JSONObject urlInfo = new JSONObject();
                urlInfo.put("Url", imgUrl);
                String requestText = urlInfo.toString();

                request = new DataOutputStream(urlConnection.getOutputStream());
                Log.e("Server", "Opening Data Connection");
                //Write data to the request
                request.writeBytes(requestText);
                request.flush();
                request.close();
                String status = urlConnection.getResponseMessage();
                //String contentEncoding = urlConnection.getHeaderField("Content-Encoding");
                //String contentType = urlConnection.getContentType();
                Log.e("status", status);
                //Log.e("Encoding", contentEncoding);
                //Log.e("Content", contentType);

                InputStream responseStream = urlConnection.getInputStream();
                BufferedReader responseStreamReader = new BufferedReader(new InputStreamReader(responseStream, "utf-8"));
                String line;
                String responseText = "";
                while ((line = responseStreamReader.readLine()) != null) {
                    Log.e("response", line);
                    responseText += line;
                }
                Log.e("Server", "Received Response from Server");

                JSONObject jsonResponse = new JSONObject(responseText);
                String predictionID = jsonResponse.getString("id");
                String projectID = jsonResponse.getString("project");
                String iterationID = jsonResponse.getString("iteration");
                JSONArray predictions = jsonResponse.getJSONArray("predictions");
                Map.Entry<String, Float> tag;

                for (int i = 0; i < predictions.length(); i++) {
                    JSONObject prediction = predictions.getJSONObject(i);
                    String tagName = prediction.getString("tagName");
                    float probability = (float) prediction.getDouble("probability");
                    JSONObject boundingBox = prediction.getJSONObject("boundingBox");
                    if (probability > PREDICTION_THRESHOLD) {
                        tag = new AbstractMap.SimpleEntry<String, Float>(tagName, probability);
                        caller.imgData.SortedLabels.add(tag);
                    }
                }

                responseStreamReader.close();
                responseStream.close();
                urlConnection.disconnect();
                return jsonResponse.toString();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }


    public static byte[] getImageBytes(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        return imageBytes;
    }
}

