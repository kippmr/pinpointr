package com.example.android.tflitecamerademo;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

public class SendImageDataService extends Service {
    public SendImageDataService() {
    }

    public boolean SendImageData(ImageData imageData) {
        throw new UnsupportedOperationException("Not yet Implemented");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new SendImageDataService.SendImageDataServiceBinder();
    }

    public class SendImageDataServiceBinder extends Binder {
        public SendImageDataService getService() {return SendImageDataService.this; }
    }
}
