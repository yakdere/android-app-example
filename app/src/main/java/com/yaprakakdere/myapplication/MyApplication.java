package com.yaprakakdere.myapplication;

import android.app.Application;
import android.content.Context;

import com.android.volley.RequestQueue;
import com.yaprakakdere.myapplication.service.SingletonRequestQueue;

/**
 * Created by yaprakakdere on 5/4/17.
 */

public class MyApplication extends Application {
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        MyApplication.context = this;
        // Get a RequestQueue
        RequestQueue queue = SingletonRequestQueue.getInstance(this.getApplicationContext()).getRequestQueue();
    }
}
