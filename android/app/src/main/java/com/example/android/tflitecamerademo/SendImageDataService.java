package com.example.android.tflitecamerademo;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.zip.GZIPInputStream;

import javax.net.ssl.HttpsURLConnection;

import static com.loopj.android.http.AsyncHttpClient.log;

public class SendImageDataService extends Service {
    public SendImageDataService() {

    }
    private ImageData imgData;

    public boolean SendImageData(ImageData imageData) {
        this.imgData = imageData;
        AsyncTask sendImgTask = new SendImageTask(this).execute(this.imgData.image);
        return true;
    }
    public boolean SendClassificationData(String imgUrl) {
        log.e("Classification","Received response " + imgUrl);
        StringBuilder tags = new StringBuilder();
        if (imgData.modelGeneratedLabels != null && !imgData.modelGeneratedLabels.isEmpty()) {
            for (String tag : imgData.modelGeneratedLabels) { //Add the model generated tags
                tags.append(tag + ",");
            }
        }
        if (imgData.modelGeneratedLabels != null && !imgData.userCorrectedLabels.isEmpty()) {
            for (String tag : imgData.userCorrectedLabels) { //Add the user corrected tags
                tags.append(tag + ",");
            }
        }
        if (tags.length() > 0) {
            tags.substring(0, tags.length() - 1); //Remove last comma in tags
        }
        AsyncTask sendImageClassDataTask = new SendImageClassDataTask(imgData).execute();
        return true;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new SendImageDataService.SendImageDataServiceBinder();
    }

    public class SendImageDataServiceBinder extends Binder {
        public SendImageDataService getService() {return SendImageDataService.this; }
    }

    private static class SendImageClassDataTask extends AsyncTask<Void, Void, String> {

        String crlf = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        private String serverUrl = "https://pinpointr-test.azurewebsites.net";
        private String postSubmissionUrl = "/api/Submission/PostSubmission";
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
                url = new URL(serverUrl + this.postSubmissionUrl);
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
                urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:56.0) Gecko/20100101 Firefox/56.0");
                urlConnection.setRequestProperty("Cache-Control", "no-cache");
                urlConnection.setRequestProperty("Accept-encoding", "gzip, deflate");
                urlConnection.setRequestProperty("content-type", "application/json");



                JSONObject testTag = new JSONObject();
                testTag.put("name", "object");
                testTag.put("user_submitted", "true");
                testTag.put("ai_percentage", "100");

                JSONArray tagsArray = new JSONArray();
                tagsArray.put(testTag);
                String requestText = tagsArray.toString();

                Log.e("Server", data.PrintCoords());
                Log.e("Server", data.GetImageURL());
                Log.e("Server", data.PrintAltitude());
                Log.e("Server", requestText);
                //Open Request
                request = new DataOutputStream(urlConnection.getOutputStream());

                Log.e("Server","Opening Data Connection");
                //Write data to the request
                request.writeBytes(requestText);
                request.flush();
                request.close();

                Log.e("Server","Sent Classification Data to Server");
                //Read data from the response
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
                Log.e("Server","Received Response from Server");

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

        String attachmentName = "test";
        String attachmentFileName = "test.jpg";
        String crlf = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        private String serverUrl = "https://pinpointr-test.azurewebsites.net";
        private String postImageUrl = "/api/Submission/PostImage";
        private SendImageDataService caller;

        SendImageTask(SendImageDataService caller) {
            this.caller = caller;
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
            URL url = new URL(serverUrl + postImageUrl);
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
                urlConnection.setRequestProperty("content-type", "multipart/form-data; boundary=" + this.boundary);

                //Open Request
                request = new DataOutputStream(urlConnection.getOutputStream());


                //Write data to the request
                request.writeBytes(this.twoHyphens + this.boundary + this.crlf);
                request.writeBytes("Content-Disposition: form-data; name=\"file\";filename=\"testingimage.jpg\"");
                request.writeBytes(this.crlf);
                request.writeBytes("Content-Type: image/jpeg");
                request.writeBytes(this.crlf);
                request.writeBytes(this.crlf);
                byte[] pixels = getImageBytes(img);
                request.write(pixels);
                request.writeBytes(this.crlf);
                request.writeBytes(this.twoHyphens + this.boundary + this.twoHyphens + this.crlf);
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

        @Override
        protected void onPostExecute(String s) {
            caller.SendClassificationData(s);
        }
    }


    public static byte[] getImageBytes(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        return imageBytes;
    }
}


