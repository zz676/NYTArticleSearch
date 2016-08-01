package com.codepath.nytarticlesearch.Receivers;

import android.app.Application;

/**
 * Created by sam on 7/31/16.
 */
public class NYTApplication extends Application {
    private static NYTApplication mInstance;

    @Override
    public void onCreate() {
        super.onCreate();

        mInstance = this;
    }

    public static synchronized NYTApplication getInstance() {
        return mInstance;
    }

    public void setConnectivityListener(ConnectivityReceiver.ConnectivityReceiverListener listener) {
        ConnectivityReceiver.connectivityReceiverListener = listener;
    }
}

