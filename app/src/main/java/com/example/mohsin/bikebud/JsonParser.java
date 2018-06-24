package com.example.mohsin.bikebud;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class JsonParser {

    //Responsible for iterating through the returned JSON objects from the APIs and saves them to an array of classes each holding the relevant value types

    private static final String TAG = "JSON PARSER";

    public ArrayList<CitiBike> ParseCitiBikeData(JSONObject result) throws JSONException {
        ArrayList<CitiBike> citiBikeArrayList = new ArrayList<>();
        JSONObject network = result.getJSONObject("network");
        Log.d(TAG,"network = " + network.toString());
        JSONArray dataset = network.getJSONArray("stations");
        CitiBike citiBike;
        for(int i = 0; i < dataset.length();i++)
        {
            citiBike = new CitiBike();
            citiBike.setEmptySlots(dataset.getJSONObject(i).getInt("empty_slots"));
            citiBike.setFreeBikes(dataset.getJSONObject(i).getInt("free_bikes"));
            citiBike.setLatitude(dataset.getJSONObject(i).getDouble("latitude"));
            citiBike.setLongitude(dataset.getJSONObject(i).getDouble("longitude"));
            citiBike.setName(dataset.getJSONObject(i).getString("name"));
            citiBikeArrayList.add(citiBike);
        }
        return citiBikeArrayList;
    }

    public ArrayList<Safety> ParseSafetyData(JSONObject result) throws JSONException {
        ArrayList<Safety> safetyArrayList = new ArrayList<>();
        JSONArray features = result.getJSONArray("features");
        Log.d(TAG,"features = " + features);
        Safety safety;
        for(int i = 0; i < features.length();i++)
        {
            safety = new Safety();
            safety.setType(features.getJSONObject(i).getJSONObject("properties").getString("type"));
            safety.setDate(features.getJSONObject(i).getJSONObject("properties").getLong("occurred_at"));
            safety.setLatitude(features.getJSONObject(i).getJSONObject("geometry").getJSONArray("coordinates").getDouble(1));
            safety.setLongitude(features.getJSONObject(i).getJSONObject("geometry").getJSONArray("coordinates").getDouble(0));
            safetyArrayList.add(safety);
            Log.d(TAG,"lat is = " + features.getJSONObject(i).getJSONObject("geometry").getJSONArray("coordinates").get(1));
            Log.d(TAG,"long is = " + features.getJSONObject(i).getJSONObject("geometry").getJSONArray("coordinates").get(0));
            Log.d(TAG,"occured at = " + features.getJSONObject(i).getJSONObject("properties").getLong("occurred_at"));
            Log.d(TAG,"type is = " + features.getJSONObject(i).getJSONObject("properties").getString("type"));
        }
        return safetyArrayList;

    }
}
