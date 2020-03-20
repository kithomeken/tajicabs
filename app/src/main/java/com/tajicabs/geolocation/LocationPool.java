package com.tajicabs.geolocation;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import com.tajicabs.R;
import com.tajicabs.global.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.tajicabs.global.Constants.SNIPPET_KEY;

public class LocationPool {
    private static final String TAG = LocationPool.class.getName();
    private String locationLat, locationLng;
    private Context context;
    private GoogleMap googleMap;
    private ArrayList<Marker> markerArrayList = new ArrayList<>();

    public LocationPool(Context context, GoogleMap googleMap, String locationLat, String locationLng) {
        this.locationLat = locationLat;
        this.locationLng = locationLng;
        this.context = context;
        this.googleMap = googleMap;
    }

    public void locationPoolRequest() {
        /*
         * Creating a String Request
         * The request type is GET defined by first parameter
         * The URL is defined in the second parameter
         * Then we have a Response Listener and a Error Listener
         * In response listener we will get the JSON response as a String
         * */

        String stringUrl = Constants.LOCATION_POOL_REQUEST + "?latitude=" + locationLat + "&longitude=" + locationLng;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, stringUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            Log.e(TAG, "JSON ARRAY: " + jsonArray);

                            // Remove Existing Location Pool Markers
                            removeLocationPool();

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);

                                String latitude = jsonObject.getString("latitude");
                                String longitude = jsonObject.getString("longitude");
                                String token = jsonObject.getString("token");

                                showLocationPool(latitude, longitude);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e(TAG, "STACKTRACE: " + e.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });

        Volley.newRequestQueue(context).add(stringRequest);
    }


    private void showLocationPool(String latStr, String lngStr) {
        String latLngString = latStr + ", " + lngStr;
        String[] latlong = latLngString.split(",");

        double latitude = Double.parseDouble(latlong[0]);
        double longitude = Double.parseDouble(latlong[1]);

        LatLng locationPool = new LatLng(latitude, longitude);

        Marker marker = googleMap.addMarker(new MarkerOptions()
            .position(locationPool)
            .snippet(SNIPPET_KEY)
        );

        marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.icon_driver));
        markerArrayList.add(marker);
    }

    private void removeLocationPool() {
        for(Marker marker: markerArrayList) {
            marker.remove();
        }
    }
}
