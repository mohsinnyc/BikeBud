package com.example.mohsin.bikebud;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Build;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.gms.common.internal.Constants;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ExecutionException;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final String TAG = "MapsActivity";
    private GoogleMap mMap;
    AsyncRequest asyncRequest;
    JsonParser jsonParser;
    ArrayList<CitiBike> citiBikeArrayList;
    ArrayList<Safety> safetyArrayList;
    SeekBar simpleSeekBar;
    TextView seekbarTV;
    Double range;
    Button resendRequestBTN;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //Interface Functions to communicate between MapsActivity and asyncRequests
        final MapsActivityInterface mapsActivityInterface = new MapsActivityInterface() {
            @Override
            public int hashCode() {
                return super.hashCode();
            }
            @Override
            public void parseCitiBikeJSON(JSONObject result) throws JSONException, ExecutionException, InterruptedException {
                parseCitiBikeData(result);
            }

            @Override
            public void parseSafetyJSON(JSONObject result) throws JSONException {
                parseSafetyData(result);
            }

            @Override
            //Function to handle no internet or if the server is unable to return a value
            public void parseCitiBikeError() {
                AlertDialog.Builder builder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = new AlertDialog.Builder(MapsActivity.this, android.R.style.Theme_Material_Dialog_Alert);
                } else {
                    builder = new AlertDialog.Builder(MapsActivity.this);
                }
                builder.setTitle("Server Error")
                        .setMessage("Unable to connect to the server. Try again?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    simpleSeekBar.setProgress(10);
                                    asyncRequest.getCitiBikeResponse();
                                } catch (ExecutionException e) {
                                    e.printStackTrace();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }

                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                               return;
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        };
        range = 1.0;
        jsonParser = new JsonParser();
        asyncRequest = new AsyncRequest(mapsActivityInterface);

        simpleSeekBar=(SeekBar)findViewById(R.id.simpleSeekBar);
        seekbarTV=(TextView)findViewById(R.id.rangeTV);
        resendRequestBTN=(Button)findViewById(R.id.resendRequestBTN);

        simpleSeekBar.setProgress(10);


        try {
            asyncRequest.getCitiBikeResponse();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        //Listener implemented on the seekbar to listen for changes and returning results of the selected range
        simpleSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                range = i / 10.0;
                seekbarTV.setText(range + " miles");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                dropPinsonBikeLocation(citiBikeArrayList,range);
                dropPinsonSafetyLocation(safetyArrayList,range);
            }
        });

        //Resend button to resend the request incase of server or internet issues
        resendRequestBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    mMap.clear();
                    asyncRequest.getCitiBikeResponse();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

    }



    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Central Park, New York.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                LinearLayout info = new LinearLayout(getApplicationContext());
                info.setOrientation(LinearLayout.VERTICAL);

                TextView title = new TextView(getApplicationContext());
                title.setTextColor(Color.BLACK);
                title.setGravity(Gravity.LEFT);
                title.setTypeface(null, Typeface.BOLD);
                title.setText(marker.getTitle());

                TextView snippet = new TextView(getApplicationContext());
                snippet.setTextColor(Color.GRAY);
                snippet.setText(marker.getSnippet());

                info.addView(title);
                info.addView(snippet);

                return info;
            }
        });
        // Add a marker in Sydney and move the camera
        LatLng nycLatLong = new LatLng(40.693364, -73.985715);
        mMap.addMarker(new MarkerOptions().position(nycLatLong).title("You are here"));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(nycLatLong,14));
    }

    private void parseCitiBikeData(JSONObject result) throws JSONException, ExecutionException, InterruptedException {
        citiBikeArrayList = jsonParser.ParseCitiBikeData(result);
        dropPinsonBikeLocation(citiBikeArrayList,range);
        asyncRequest.getSafetyConcerns();
    }

    private void parseSafetyData(JSONObject result) throws JSONException {
        safetyArrayList = jsonParser.ParseSafetyData(result);
        Log.d(TAG,"safety array size is = " + safetyArrayList.size());
        dropPinsonSafetyLocation(safetyArrayList,range);
    }

    private void dropPinsonSafetyLocation(ArrayList<Safety> safetyArrayList, Double range) {
        MarkerOptions markerOptions;
        LatLng currLatLong;
        Location startPoint = new Location("Your Location");
        Location endPoint;
        SimpleDateFormat sdf;
        Date date;
        Safety safety;
        String formattedDate;
        double distanceinmeters;
        double miles;

        startPoint.setLatitude(40.693364);
        startPoint.setLongitude(-73.985715);

        //Iterating through the entire arraylist of Safety class and placing maps on the provided longitude and latitude
        //Also calculating the distance selected on the range and displaying the relevant incidents
        for(int i = 0; i < safetyArrayList.size(); i++)
        {
            markerOptions = new MarkerOptions();
            safety = safetyArrayList.get(i);
            date = new java.util.Date(safety.getDate()*1000L);
            sdf = new java.text.SimpleDateFormat("MM-dd-yyyy \nh:mm a");
            sdf.setTimeZone(java.util.TimeZone.getTimeZone("UTC-4"));
            formattedDate = sdf.format(date);
            currLatLong = new LatLng(safety.getLatitude(),safety.getLongitude());
            markerOptions.position(currLatLong);
            markerOptions.title("Occurrence: " + safety.getType());
            markerOptions.snippet("\nDate/Time: " + formattedDate);
            if(safety.getType().contains("theft") || safety.getType().contains("Theft") ) markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.robbery));
            else if(safety.getType().contains("hazard") || safety.getType().contains("Hazard") ) markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.hazard));
            else if(safety.getType().contains("accident") || safety.getType().contains("Accident") ) markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.accident));
            else if(safety.getType().contains("unconfirmed") || safety.getType().contains("Unconfirmed")) markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.unknown));
            endPoint=new Location("locationA");
            endPoint.setLatitude(safety.getLatitude());
            endPoint.setLongitude(safety.getLongitude());
            distanceinmeters = startPoint.distanceTo(endPoint);
            miles = distanceinmeters * 0.00062137;

            Log.d(TAG,"safety miles is = " + miles);
            if(miles < range)
            {
                mMap.addMarker(markerOptions).showInfoWindow();
            }

        }
    }

    //Iterating through the entire arraylist of CitiBike class and placing maps on the provided longitude and latitude
    //Also calculating the distance selected on the range and displaying the relevant bikes
    private void dropPinsonBikeLocation(ArrayList<CitiBike> citiBikeArrayList, double range) {
        MarkerOptions markerOptions;
        LatLng currLatLong;
        Location startPoint = new Location("Your Location");
        Location endPoint;
        CitiBike citiBike;
        double distanceinmeters;
        double miles;

        startPoint.setLatitude(40.693364);
        startPoint.setLongitude(-73.985715);
        mMap.clear();
        LatLng nycLatLong = new LatLng(40.693364, -73.985715);
        mMap.addMarker(new MarkerOptions().position(nycLatLong).title("You are here"));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(nycLatLong,14));
        for(int i = 0; i < citiBikeArrayList.size(); i++)
        {

            markerOptions = new MarkerOptions();
            citiBike = citiBikeArrayList.get(i);
            currLatLong = new LatLng(citiBike.getLatitude(),citiBike.getLongitude());
            markerOptions.position(currLatLong);
            markerOptions.title(citiBike.getName());
            markerOptions.snippet("\nFree Slots: " + citiBike.getEmptySlots() + "\nBikes Available: " + citiBike.getFreeBikes());
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.bikeicon));
            endPoint=new Location("locationA");
            endPoint.setLatitude(citiBike.getLatitude());
            endPoint.setLongitude(citiBike.getLongitude());
            distanceinmeters = startPoint.distanceTo(endPoint);
            miles = distanceinmeters * 0.00062137;


            if(miles < range)
            {
                Log.d(TAG,"miles is = " + miles);
                mMap.addMarker(markerOptions).showInfoWindow();
            }

        }
    }


}
