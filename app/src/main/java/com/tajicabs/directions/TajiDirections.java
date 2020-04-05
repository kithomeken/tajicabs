package com.tajicabs.directions;

import android.location.Location;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.android.PolyUtil;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.TravelMode;
import com.tajicabs.global.Variables;

import org.joda.time.DateTime;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import static com.tajicabs.configuration.TajiCabs.DEFAULT_ZOOM;
import static com.tajicabs.configuration.TajiCabs.DEST_LTNG;
import static com.tajicabs.configuration.TajiCabs.DEST_NAME;
import static com.tajicabs.configuration.TajiCabs.GOOGLE_API;
import static com.tajicabs.configuration.TajiCabs.ORIG_LTNG;
import static com.tajicabs.configuration.TajiCabs.ORIG_NAME;

public class TajiDirections {
    private static final String TAG = TajiDirections.class.getName();
    private static final int overview = 0;

    public DirectionsResult getDirectionsDetails(LatLng origin, LatLng destination, TravelMode mode){
        DateTime now = new DateTime();

        double orgLat = origin.latitude;
        double orgLng = origin.longitude;

        double desLat = destination.latitude;
        double desLng = destination.longitude;

        String strOrg = orgLat + "," + orgLng;
        String strDes = desLat + "," + desLng;

        float[] results = new float[1];
        Location.distanceBetween(orgLat, orgLng, desLat, desLng, results);

        try {
            return DirectionsApi.newRequest(getGeoContext())
                    .mode(mode)
                    .origin(strOrg)
                    .destination(strDes)
                    .departureTime(now)
                    .await();
        } catch (InterruptedException | ApiException | IOException e) {
            e.printStackTrace();
            Log.e(TAG, "Direction Exception ", e);
            return null;
        }
    }

    private GeoApiContext getGeoContext() {
        GeoApiContext geoApiContext = new GeoApiContext();
        return geoApiContext
                .setQueryRateLimit(3)
                .setApiKey(GOOGLE_API)
                .setConnectTimeout(1, TimeUnit.SECONDS)
                .setReadTimeout(1, TimeUnit.SECONDS)
                .setWriteTimeout(1, TimeUnit.SECONDS);
    }

    public void addPolyline(DirectionsResult results, GoogleMap mMap) {
        List<LatLng> decodedPath = PolyUtil.decode(results.routes[overview]
                .overviewPolyline.getEncodedPath());

        mMap.addPolyline(new PolylineOptions().addAll(decodedPath));
    }

    public void positionCamera(DirectionsRoute route, GoogleMap mMap) {
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(
                        route.legs[overview].startLocation.lat,
                        route.legs[overview].startLocation.lng),
                /* Zoom Level */ 12));
    }

    public void addMarkersToMap(DirectionsResult results, GoogleMap mMap) {
        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(results.routes[overview].legs[overview].startLocation.lat,
                        results.routes[overview].legs[overview].startLocation.lng))
                .title(results.routes[overview].legs[overview].startAddress));

        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(results.routes[overview].legs[overview].endLocation.lat,
                        results.routes[overview].legs[overview].endLocation.lng))
                .title(results.routes[overview].legs[overview].startAddress)
                .snippet(getEndLocationTitle(results)));
    }

    private String getEndLocationTitle(DirectionsResult results){
        return  "Time :"+ results.routes[overview].legs[overview].duration
                .humanReadable + " Distance :" +
                results.routes[overview].legs[overview].distance.humanReadable;
    }

    public String distanceInMeters(DirectionsResult results) {
        String distanceStr = "" + results.routes[overview].legs[overview].distance.inMeters;
        double distance = Double.parseDouble(distanceStr);

        distance = distance / 1000;
        distance = Math.round(distance * 100.0) / 100.0;
        distanceStr = "" + distance;

        // Distance is in KM
        return distanceStr;
    }

    public String costCalculator(String distanceStr) {
        // Calculate Cost
        double cost;
        int baseDistance = 2;
        int baseCost = 200;
        double distance = Double.parseDouble(distanceStr);
        int additionalAmount = 50;

        double extra = distance - baseDistance;

        if (extra > 0 ) {
            // Calculate extra amount
            cost = baseCost + (extra * additionalAmount);
        } else {
            // USe Base Cost
            cost = baseCost;
        }

        cost = Math.round(cost);
        cost = Math.round(cost/50.0) * 50;
        NumberFormat numberFormat = NumberFormat.getInstance(Locale.US);

        Variables.COST = String.valueOf(cost);
        return numberFormat.format(cost);
    }

    public void showOnMap(GoogleMap mMap) {
        if (DEST_LTNG != null && ORIG_LTNG != null) {
            mMap.addMarker(new MarkerOptions().position(ORIG_LTNG)
                    .title(ORIG_NAME));

            mMap.addMarker(new MarkerOptions().position(DEST_LTNG)
                    .title(DEST_NAME));

            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(ORIG_LTNG, DEFAULT_ZOOM));

            // Directions API
            DirectionsResult directionsResult = getDirectionsDetails(ORIG_LTNG, DEST_LTNG, TravelMode.DRIVING);

            if (directionsResult != null) {
                Log.d(TAG, "-----------------------------" + directionsResult);
                addPolyline(directionsResult, mMap);
                positionCamera(directionsResult.routes[overview], mMap);
//                tajiDirections.addMarkersToMap(directionsResult, mMap);
            } else {
                Log.d(TAG, "-----------------------------");
            }
        }
    }
}
