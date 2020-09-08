package com.amjadnas.listeners;

public interface ProgressListener {

    void onPreStart();
    void onProgress(String message, int progress);


}
