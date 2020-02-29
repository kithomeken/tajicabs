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

import java.util.HashMap;
import java.util.Map;

import static com.tajicabs.configuration.TajiCabs.COST;
import static com.tajicabs.configuration.TajiCabs.DEST_LTNG;
import static com.tajicabs.configuration.TajiCabs.NAMES;
import static com.tajicabs.configuration.TajiCabs.ORIG_LTNG;
import static com.tajicabs.configuration.TajiCabs.PHONE;

public class RequestServices {
    private static final String TAG = RequestServices.class.getName();
    private Context context;

    public RequestServices (Context context) {
        this.context = context;
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
                        Log.d(TAG, "===================================== COMPLETE");
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "===================================== ERROR " + error.getMessage());
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", email);
                params.put("phone", PHONE);
                params.put("name", NAMES);
                params.put("request_type", rType);
                params.put("cost", COST);

                double orgLat = ORIG_LTNG.latitude;
                double orgLng = ORIG_LTNG.longitude;

                double desLat = DEST_LTNG.latitude;
                double desLng = DEST_LTNG.longitude;

                String strOrg = orgLat + "," + orgLng;
                String strDes = desLat + "," + desLng;

                params.put("origin", strOrg);
                params.put("destination", strDes);

                Log.d(TAG, "=====================================" + params);

                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                return params;
            }
        };

        queue.add(stringRequest);
    }
}
