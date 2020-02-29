package com.tajicabs.configuration;


import com.google.android.gms.maps.model.LatLng;

public class TajiCabs {
    public final static String PASSENGER_DETAILS = "PASSENGER_DETAILS";

    public static String EMAIL;
    public static String NAMES;
    public static String IDNUM;
    public static String PHONE;

    public final static String GOOGLE_API = "AIzaSyAAiRrbL3TId8hGBTVwLNj1-TI43ldIDcs";

    public static LatLng ORIG_LTNG = null;
    public static String ORIG_NAME = null;
    public static LatLng DEST_LTNG = null;
    public static String DEST_NAME = null;

    public static String DISTANCE = null;
    public static String COST = null;

    public static final int DEFAULT_ZOOM = 18;
    public static final int REQUEST_LOCATION = 199;

    public static int ACTIVITY_STATE = 0;
}
