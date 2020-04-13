package com.tajicabs.services;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.tajicabs.database.AppDatabase;
import com.tajicabs.database.RWServices;
import com.tajicabs.global.Variables;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.tajicabs.global.Variables.ACCOUNT_NAME;
import static com.tajicabs.global.Variables.ACCOUNT_PHONE;
import static com.tajicabs.global.Variables.COST;
import static com.tajicabs.global.Variables.DEST_LTNG;
import static com.tajicabs.global.Variables.DEST_NAME;
import static com.tajicabs.global.Variables.DISTANCE;
import static com.tajicabs.global.Variables.ORIG_LTNG;
import static com.tajicabs.global.Variables.ORIG_NAME;

public class RequestServices {
    private static final String TAG = RequestServices.class.getName();
    private Context context;
    private AppDatabase appDatabase;

    public RequestServices (Context context, AppDatabase appDatabase) {
        this.context = context;
        this.appDatabase = appDatabase;
    }

    public void requestRide(final String email) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String tajiUrl = "https://taji.kennedykitho.me/taji/firebase/passenger/request-ride";
        final String rType = "702";

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, tajiUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "STRING REQUEST COMPLETE");
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "ERROR IN STRING REQUEST " + error.getMessage());
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", email);
                params.put("passengerPhone", Variables.ACCOUNT_PHONE);
                params.put("passengerName", Variables.ACCOUNT_NAME);
                params.put("tripCost", Variables.COST);
                params.put("tripDistance", Variables.DISTANCE);

                params.put("request_type", rType);
                params.put("originLat", String.valueOf(ORIG_LTNG.latitude));
                params.put("originLng", String.valueOf(ORIG_LTNG.longitude));
                params.put("originName", ORIG_NAME);

                params.put("destinationLat", String.valueOf(DEST_LTNG.latitude));
                params.put("destinationLng", String.valueOf(DEST_LTNG.longitude));
                params.put("destinationName", Variables.DEST_NAME);

                final String tripId = UUID.randomUUID().toString();
                params.put("tripId", tripId);

                Log.d(TAG, "=====================================" + params);
                String emptyString = " ";

                // Create Trip Request
                RWServices rwServices = new RWServices(appDatabase);
                rwServices.createTripRequest(tripId, ORIG_NAME, String.valueOf(ORIG_LTNG.latitude),
                        String.valueOf(ORIG_LTNG.longitude), DEST_NAME, String.valueOf(DEST_LTNG.latitude),
                        String.valueOf(DEST_LTNG.longitude), ACCOUNT_NAME, ACCOUNT_PHONE, DISTANCE,
                        COST, emptyString, emptyString, "R");

                return params;
            }

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                return params;
            }
        };

        queue.add(stringRequest);
    }
}
