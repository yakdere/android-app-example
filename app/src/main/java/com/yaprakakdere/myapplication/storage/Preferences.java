package com.yaprakakdere.myapplication.storage;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by yaprakakdere on 5/5/17.
 */

public class Preferences {

    private static final String PREFS_FILE = "dd.pref.file";
    private static final String PREFS_FAV_RES = "fav_rest";
    private SharedPreferences sharedPrefs;
    private static Preferences theInstance;

    private Preferences(Context ctx) {
        sharedPrefs = ctx.getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE);
    }

    public static synchronized Preferences getInstance(Context ctx) {
        if (theInstance == null) {
            theInstance = new Preferences(ctx);
        }
        return theInstance;
    }

    public SharedPreferences.Editor beginTransaction() {
        return sharedPrefs.edit();
    }

    public boolean endTransaction(SharedPreferences.Editor editor) {
        if (editor != null) {
            editor.apply();
            return true;
        }
        return false;
    }

    public void addResToFavs(String id, String restaurant) {
        if (restaurant == null) return;
        Map<String, String> allfavs = getFavsRes();
        SharedPreferences.Editor editor = beginTransaction();
        allfavs.put(id, restaurant);
        Gson gson = new GsonBuilder().disableHtmlEscaping().create();
        editor.putString(PREFS_FAV_RES, gson.toJson(allfavs));
        endTransaction(editor);
    }

    public Map<String, String> getFavsRes() {
        String cachedResString = sharedPrefs.getString(PREFS_FAV_RES, null);
        if (cachedResString == null) return new HashMap<>();

        java.lang.reflect.Type type = new TypeToken<LinkedHashMap<String, String>>() {}.getType();
        try {
            Gson gson = new GsonBuilder().disableHtmlEscaping().create();
            LinkedHashMap<String, String> favResMap = gson.fromJson(cachedResString, type);
            if (favResMap != null) {
                StringBuilder sb = new StringBuilder();
                for (String key : favResMap.keySet()) {
                    sb.append("\n\t" + key + "->" + favResMap.get(key));
                }
            }
            return favResMap;

        } catch (JsonSyntaxException e) {
            // better to return empty map than null, because you can start writing into it right away
            return new HashMap<>();
        }
    }

    public boolean isFav(String resID) {
        return getFavsRes().containsKey(resID);
    }

    public void removeFromFav(String resID) {
        if (!isFav(resID)) return;
        Map<String, String> allfavs = getFavsRes();
        allfavs.remove(resID);
        SharedPreferences.Editor editor = beginTransaction();
        Gson gson = new GsonBuilder().disableHtmlEscaping().create();
        editor.putString(PREFS_FAV_RES, gson.toJson(allfavs));
        endTransaction(editor);
    }
}
