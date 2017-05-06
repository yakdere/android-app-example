package com.yaprakakdere.myapplication.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.ResultReceiver;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by yaprakakdere on 5/4/17.
 */

public class RequestIntentService extends IntentService implements UINotifier {
    private static final String RES_URL = "https://api.doordash.com/v2/restaurant/?lat=37.422740&lng=-122.139956";
    private static final String RES_DETAILS_URL = "https://api.doordash.com/v2/restaurant";

    private static final String ACTION_FETCH_RES = "action_fetch_res" ;
    private static final String ACTION_FETCH_RES_DETAILS = "action_fetch_res_details";

    private static final String RESULT_RECEIVER_PARAM = "result_receiver";
    private static final String RESULT_RES_ID = "result_restaurant_id" ;


    public static final String RESULT_FIELD_STATUS = "field_status";
    public static final String RESULT_FIELD_TYPE = "field_type";
    public static final String RESULT_ACTUAL_RESULT = "actual_result";

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public RequestIntentService(String name) {
        super(name);
    }

    public RequestIntentService() {
        super("RequestIntentService");
    }

    public static void startActionFetchRes(Context context, MResultReceiver receiver) {
        Intent intent = new Intent(context, RequestIntentService.class);
        intent.setAction(ACTION_FETCH_RES);
        intent.putExtra(RESULT_RECEIVER_PARAM, receiver);
        context.startService(intent);
    }

    public static void startActionFetchResDetails(Context context, String resID, MResultReceiver receiver) {
        Intent intent = new Intent(context, RequestIntentService.class);
        intent.setAction(ACTION_FETCH_RES_DETAILS);
        intent.putExtra(RESULT_RECEIVER_PARAM, receiver);
        intent.putExtra(RESULT_RES_ID, resID);
        context.startService(intent);
    }


    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            ResultReceiver receiver = intent.getExtras().getParcelable(RESULT_RECEIVER_PARAM);
            switch (action) {
                case ACTION_FETCH_RES:
                    handleFetchRes(receiver);
                    break;
                case ACTION_FETCH_RES_DETAILS:
                    String resID = intent.getExtras().getString(RESULT_RES_ID);
                    handleFetchResDetails(resID, receiver);
                    break;
            }
        }
    }

    private void handleFetchRes(final ResultReceiver receiver) {
        // Formulate the request and handle the response.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, RES_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        notifyUI(STATUS_SUCCESS, RESULT_TYPE_DISCOVER, response, receiver);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle error
                    }
                });

        // Add the request to the RequestQueue.
        SingletonRequestQueue.getInstance(this).addToRequestQueue(stringRequest);
    }

    private void handleFetchResDetails(final String resID, final ResultReceiver receiver) {

        Uri uri = Uri.parse(RES_DETAILS_URL);
        Uri.Builder builder = uri.buildUpon().appendPath(resID);
        String url = builder.build().toString();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("details", response);
                        notifyUI(STATUS_SUCCESS, RESULT_TYPE_DISCOVER_DETAILS, response, receiver);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle error
                    }
                });

        // Add the request to the RequestQueue.
        SingletonRequestQueue.getInstance(this).addToRequestQueue(stringRequest);
    }

    @Override
    public void notifyUI(int status, int typeResult, Bundle bundle, ResultReceiver receiver) {
        Bundle b = bundle;
        if (b == null)
            b = new Bundle();
        b.putInt(RESULT_FIELD_STATUS, status);
        b.putInt(RESULT_FIELD_TYPE, typeResult);
        if (receiver != null) {
            receiver.send(0, b);
        }
    }

    @Override
    public void notifyUI(int status, int typeResult, String result, ResultReceiver receiver) {
        Bundle b = new Bundle();
        b.putInt(RESULT_FIELD_STATUS, status);
        b.putInt(RESULT_FIELD_TYPE, typeResult);
        b.putString(RESULT_ACTUAL_RESULT, result);
        if (receiver != null) {
            receiver.send(0, b);
        }
    }

    @Override
    public void notifyUI(int status, int typeResult, ResultReceiver receiver) {
        Bundle b = new Bundle();
        b.putInt(RESULT_FIELD_STATUS, status);
        b.putInt(RESULT_FIELD_TYPE, typeResult);
        if (receiver != null) {
            receiver.send(0, b);
        }
    }
}
