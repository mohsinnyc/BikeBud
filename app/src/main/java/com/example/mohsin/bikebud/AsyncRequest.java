package com.example.mohsin.bikebud;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Log;

import com.goebl.david.Webb;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 AsyncRequest is responsible for making network calls to the API and passing the JSON results to the MapsActivity to parse
 */

public class AsyncRequest {

    private static final String TAG = "asyncRequests";
    private final MapsActivityInterface mapsActivityListener;

    //Webb is Lightweight Java HTTP-Client for calling JSON REST-Service
    final Webb webb = Webb.create();



    public AsyncRequest(MapsActivityInterface listener)
    {
        this.mapsActivityListener = listener;
    }

    @SuppressLint("StaticFieldLeak")
    public JSONObject getCitiBikeResponse() throws ExecutionException, InterruptedException, TimeoutException {
        String serverURL = "https://api.citybik.es/v2/networks";
        webb.setBaseUri(serverURL);
        new AsyncTask< Void, Void, JSONObject >()
        {
            @Override
            protected JSONObject doInBackground(Void...params)
            {
                JSONObject response = new JSONObject();
                try
                {
                    response = webb
                            .post("/citi-bike-nyc")
                            .asJsonObject()
                            .getBody();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
                return response;
            }
            @Override
            protected void onPostExecute(JSONObject result)
            {
                if(result == null || !result.has("network")) {
                   mapsActivityListener.parseCitiBikeError();
                }
                else
                {
                    try {
                        Log.d(TAG, "response is = " + result);
                        Log.d(TAG, "async made it");
                        mapsActivityListener.parseCitiBikeJSON(result);
                        return;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.execute().get(30000, TimeUnit.MILLISECONDS);;
        return null;
    }

    @SuppressLint("StaticFieldLeak")
    public JSONObject getSafetyConcerns() throws ExecutionException, InterruptedException {
        String serverURL = "https://bikewise.org:443/api/v2";
        webb.setBaseUri(serverURL);
        new AsyncTask< Void, Void, JSONObject >()
        {
            @Override
            protected JSONObject doInBackground(Void...params)
            {
                JSONObject response = new JSONObject();
                try
                {
                    response = webb
                            .get("/locations?proximity=40.693364%2C-73.985715&proximity_square=100")
                            .asJsonObject()
                            .getBody();
                    Log.d(TAG, "response = " + response.toString());
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
                return response;
            }
            @Override
            protected void onPostExecute(JSONObject result)
            {
                try{
                    mapsActivityListener.parseSafetyJSON(result);

                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }.execute();
        return null;
    }



}
