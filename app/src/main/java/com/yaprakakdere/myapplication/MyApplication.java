package com.yaprakakdere.myapplication;

import android.app.Application;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Created by yaprakakdere on 5/4/17.
 */

public class MyApplication extends Application {
    private static Gson gson;
    @Override
    public void onCreate() {
        super.onCreate();
        gson = new GsonBuilder().disableHtmlEscaping().create();
    }

    public static Gson getGson() {
        return gson;
    }
}
