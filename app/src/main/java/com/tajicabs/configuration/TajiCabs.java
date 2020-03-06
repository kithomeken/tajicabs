package com.tajicabs.configuration;


import com.google.android.gms.maps.model.LatLng;

import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;

public class TajiCabs {
    public final static String PASSENGER_DETAILS = "PASSENGER_DETAILS";
    public static String ERROR_CODE;

    public static String FIREBASE_TOKEN = null;




    public static CookieStore APP_CLIENT_COOKIE = new BasicCookieStore();
    public static HttpClient APP_HTTP_CLIENT = new DefaultHttpClient();

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

    public static String DR_NAME = null;
    public static String DR_PHONE = null;
    public static String DR_REG = null;
    public static String DR_MAKE = null;

    public static String DR_TOKEN = null;
}
