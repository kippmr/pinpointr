package com.example.android.tflitecamerademo;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SaveImageService extends Service {
    public SaveImageService() {
    }

    File galleryFolder = null;

    public String SaveImage(Bitmap img) {
        FileOutputStream outputPhoto = null;
        if (galleryFolder == null) {
            createImageGallery();
        }
        try {
            File outputLocn = createImageFile(galleryFolder);
            outputPhoto = new FileOutputStream(outputLocn);
            img.compress(Bitmap.CompressFormat.PNG, 100, outputPhoto);
            outputPhoto.close();
            return outputLocn.getPath();
        }

        catch (IOException e) {
            e.printStackTrace();
        }
        return null;

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


    @Override
    public IBinder onBind(Intent intent) {
        return new SaveImageService.SaveImageServiceBinder();
    }

    public class SaveImageServiceBinder extends Binder {
        public SaveImageService getService() {return SaveImageService.this; }
    }
}
