package com.example.mohsin.bikebud;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

//Interface for communicating through the MapsActivity and asyncRequests

interface MapsActivityInterface
{

    void parseCitiBikeJSON(JSONObject result) throws JSONException, ExecutionException, InterruptedException;

    void parseSafetyJSON(JSONObject result) throws JSONException;

    void parseCitiBikeError();
}