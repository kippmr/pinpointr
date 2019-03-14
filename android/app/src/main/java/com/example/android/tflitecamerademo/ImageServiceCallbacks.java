package com.example.android.tflitecamerademo;


//We need this interface so the Image Service can trigger updating of the labels on the UI thread after it receives the classification data

public interface ImageServiceCallbacks {
    public void SetLabels();

}