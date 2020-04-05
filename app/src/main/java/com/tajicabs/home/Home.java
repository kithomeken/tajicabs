package com.tajicabs.home;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.google.maps.model.DirectionsResult;
import com.google.maps.model.TravelMode;

import com.tajicabs.R;
import com.tajicabs.auth.SignIn;
import com.tajicabs.database.AppDatabase;
import com.tajicabs.database.RWServices;
import com.tajicabs.directions.TajiDirections;
import com.tajicabs.geolocation.LocationPool;
import com.tajicabs.global.Variables;
import com.tajicabs.services.RequestServices;
import com.tajicabs.settings.ContactUs;
import com.tajicabs.settings.Settings;
import com.tajicabs.trips.TripsActivity;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import static com.tajicabs.global.Constants.DEFAULT_ZOOM;
import static com.tajicabs.global.Constants.GOOGLE_API;
import static com.tajicabs.global.Constants.REQUEST_LOCATION;
import static com.tajicabs.global.Variables.ACCOUNT_NAME;
import static com.tajicabs.global.Variables.DEST_LTNG;
import static com.tajicabs.global.Variables.DEST_NAME;
import static com.tajicabs.global.Variables.DISTANCE;
import static com.tajicabs.global.Variables.DR_MAKE;
import static com.tajicabs.global.Variables.DR_NAME;
import static com.tajicabs.global.Variables.DR_PHONE;
import static com.tajicabs.global.Variables.DR_REG;
import static com.tajicabs.global.Variables.END_TRIP;
import static com.tajicabs.global.Variables.ORIG_LTNG;
import static com.tajicabs.global.Variables.ORIG_NAME;

public class Home extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        OnMapReadyCallback {
    private static final String TAG = Home.class.getName();

    // Dependency classes
    TajiDirections tajiDirections = new TajiDirections();
    LocationPool locationPool;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    protected static final int overview = 0;
    protected static final int REQUEST_CHECK_SETTINGS = 0x1;
    protected static final int REQUEST_PHONE_CALL = 0x1;

    private GoogleMap googleMap;
    private LocationRequest mLocationRequest;

    private FusedLocationProviderClient mFusedLocationProviderClient;
    private final LatLng mDefaultLocation = new LatLng(-1.2833 , 36.8167);
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mLocationPermissionGranted;

    private Location mLastKnownLocation;
    private GoogleApiClient googleApiClient;

    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";
    private final int AUTOCOMPLETE_REQUEST_CODE = 1;

    private FloatingActionButton geoLocation;
    private EditText textPickUp, textDropOffs;
    private Button requestRide, cancelRide;

    private static String EDIT_TEXT_TYPE = "E";
    private View requestRidePopUp, endTripPopUp, tempView;
    private ConstraintLayout locationLayout, requestLayout, driverLayout;
    private View rootView;

    @Override
    public void onStart() {
        super.onStart();

        firebaseUser = firebaseAuth.getCurrentUser();
        checkFirebaseSession(firebaseUser);
    }

    private void checkFirebaseSession(FirebaseUser user) {
        if (user == null) {
            //Return to SignInActivity
            Intent intent = new Intent(Home.this, SignIn.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Variables.ACTIVITY_STATE = 0;
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        // Fetch Details From Room Database
        AppDatabase appDatabase = AppDatabase.getDatabase(this);
        RWServices rwServices = new RWServices(appDatabase);
        rwServices.getUserDetails();

        if (savedInstanceState != null) {
            mLastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
        }

        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), GOOGLE_API);
        }

        locationLayout = findViewById(R.id.sheet_location_details);
        requestLayout = findViewById(R.id.sheet_request_trip);
        driverLayout = findViewById(R.id.sheet_driver_details);

        locationLayout.setVisibility(View.VISIBLE);
        requestLayout.setVisibility(View.GONE);
        driverLayout.setVisibility(View.GONE);

        // Places Picker
        placesPickStartPoint();
        placesPickEndPoint();

        mLocationRequest = new LocationRequest().setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_fragment);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Set display name, photo url and email address of signed in user
        View navHeaderView = navigationView.getHeaderView(0);
        TextView accountName = navHeaderView.findViewById(R.id.accountName);
        TextView accountEmail = navHeaderView.findViewById(R.id.accountEmail);

        assert firebaseUser != null;
        accountName.setText(ACCOUNT_NAME);
        accountEmail.setText(firebaseUser.getEmail());

        geoLocation = findViewById(R.id.geo_location);
        geoLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDeviceLocation();
            }
        });

        requestRide = findViewById(R.id.requestRide);
        requestRide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRequestPopUp(v);
            }
        });

        if (END_TRIP.equalsIgnoreCase("Y")) {
            rwServices.endTripUpdate();
            rootView = getWindow().getDecorView().getRootView();

            END_TRIP = "N";
            showEndTripPopUp(rootView);
        }

        if (Variables.DR_NAME != null) {
            showDriverDetails();
        }
    }

    private void showDriverDetails() {
        locationLayout.setVisibility(View.GONE);
        requestLayout.setVisibility(View.GONE);
        driverLayout.setVisibility(View.VISIBLE);

        // Populate Text Views
        TextView driverName, driverPhone, driverVehicle;
        driverName = findViewById(R.id.driverName);
        driverPhone = findViewById(R.id.driverPhone);
        driverVehicle = findViewById(R.id.driverVehicle);

        driverName.setText(DR_NAME);
        driverPhone.setText(DR_PHONE);
        driverVehicle.setText(DR_REG + " " + DR_MAKE);

        ImageView phoneCall = findViewById(R.id.phoneCall);
        phoneCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String telephone = "tel:" + DR_PHONE;

                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse(telephone));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    Activity#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for Activity#requestPermissions for more details.
                        ActivityCompat.requestPermissions(Home.this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_PHONE_CALL);
                    }
                }
                startActivity(callIntent);
            }
        });
    }

    private void placesPickStartPoint() {
        textPickUp = findViewById(R.id.textPickUp);
        textPickUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String status = "PickUp";
                tempView = view;

                editTextType(status);
                onSearchCalled();
            }
        });

        textPickUp.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    String status = "PickUp";
                    tempView = view;

                    editTextType(status);
                    onSearchCalled();
                }
            }
        });
    }

    private void placesPickEndPoint() {
        textDropOffs = findViewById(R.id.textDropOff);
        textDropOffs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String status = "Dropoff";
                editTextType(status);
                onSearchCalled();
            }
        });

        textDropOffs.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    String status = "Dropoff";
                    editTextType(status);
                    onSearchCalled();
                }
            }
        });
    }

    private void editTextType(String status) {
        EDIT_TEXT_TYPE = status;
    }

    private void onSearchCalled() {
        List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG);

        Intent intent = new Autocomplete.IntentBuilder(
                AutocompleteActivityMode.FULLSCREEN, fields).setCountry("KE")
                .build(this);
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (EDIT_TEXT_TYPE.equalsIgnoreCase("Pickup") || EDIT_TEXT_TYPE.equalsIgnoreCase("Dropoff")) {
            if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
                if (resultCode == RESULT_OK) {
                    Place place = Autocomplete.getPlaceFromIntent(data);
                    Log.i(TAG, "Place: " + place.getName() + ", " + place.getId());

                    if (EDIT_TEXT_TYPE.equalsIgnoreCase("Pickup")) {
                        // Pick Up Latlng
                        ORIG_LTNG = place.getLatLng();
                        ORIG_NAME = place.getName();

                        textPickUp.setText(ORIG_NAME);
                    }

                    if (EDIT_TEXT_TYPE.equalsIgnoreCase("Dropoff")) {
                        // Drop Off Latlng
                        DEST_LTNG = place.getLatLng();
                        DEST_NAME = place.getName();

                        textDropOffs.setText(DEST_NAME);
                    }

                    // Draw Directions
                    drawRouteDirections();
                } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                    // TODO: Handle the error.
                    Status status = Autocomplete.getStatusFromIntent(data);
                    Log.i(TAG, Objects.requireNonNull(status.getStatusMessage()));
                } else if (resultCode == RESULT_CANCELED) {
                    // The user canceled the operation.
                    ORIG_LTNG = null;
                    ORIG_NAME = null;
                    DEST_LTNG = null;
                    DEST_NAME = null;
                }
            }
        } else {
            // Check for the integer request code originally supplied to startResolutionForResult().
            if (requestCode == REQUEST_CHECK_SETTINGS) {
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        getDeviceLocation();
                        break;

                    case Activity.RESULT_CANCELED:
                        settingsRequest();//keep asking if imp or do whatever
                        break;
                }
            }
        }
    }

    private void drawRouteDirections() {
        if (DEST_LTNG != null && ORIG_LTNG != null) {
            googleMap.clear();
            googleMapsUISetting(googleMap);

            DirectionsResult directionsResult = tajiDirections
                    .getDirectionsDetails(ORIG_LTNG, DEST_LTNG, TravelMode.DRIVING);

            Log.e(TAG, "Origin LatLng -- " + ORIG_LTNG);
            Log.e(TAG, "Destination LatLng -- " + DEST_LTNG);
            Log.e(TAG, "Directions Results -- " + directionsResult);

            if (directionsResult != null) {
                tajiDirections.addPolyline(directionsResult, googleMap);
                tajiDirections.positionCamera(directionsResult.routes[overview], googleMap);
                tajiDirections.addMarkersToMap(directionsResult, googleMap);

                DISTANCE = tajiDirections.distanceInMeters(directionsResult);
            }

            TextView fromDisp, toDisp, distanceCovered, costDisp;

            fromDisp = findViewById(R.id.fromDisp);
            toDisp = findViewById(R.id.toDisp);
            distanceCovered = findViewById(R.id.distanceCovered);
            costDisp = findViewById(R.id.costDisp);

            fromDisp.setText("From: " + ORIG_NAME);
            toDisp.setText("To: " + DEST_NAME);
            distanceCovered.setText(DISTANCE);
            costDisp.setText(tajiDirections.costCalculator(DISTANCE));

            locationLayout.setVisibility(View.GONE);
            requestLayout.setVisibility(View.VISIBLE);
            driverLayout.setVisibility(View.GONE);

            /*requestBlock.setVisibility(View.VISIBLE);
            locationBlock.setVisibility(View.GONE);

            changeMarginBottom();*/
        }
    }

    private void showRequestPopUp(View view){
        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.MATCH_PARENT;

        locationLayout.setVisibility(View.GONE);
        requestLayout.setVisibility(View.GONE);
        driverLayout.setVisibility(View.GONE);
        geoLocation.setVisibility(View.GONE);

        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        requestRidePopUp = Objects.requireNonNull(layoutInflater).inflate(R.layout.modal_request_ride, null);

        boolean focusable = true;
        final PopupWindow popupWindow = new PopupWindow(requestRidePopUp, width, height, focusable);
        popupWindow.setElevation(8);

        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        requestRidePopUp.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                popupWindow.isShowing();
                return true;
            }
        });

        // Dismiss Window After 1 Minute
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(60000);
                } catch (InterruptedException e) {
                    Log.e(TAG, "EXCEPTION: " + e.getMessage());
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Do some stuff
                        if (DR_NAME == null) {
                            Toast.makeText(Home.this, "We could not find any driver near you. Try again Later", Toast.LENGTH_LONG).show();
                            popupWindow.dismiss();

                            locationLayout.setVisibility(View.GONE);
                            requestLayout.setVisibility(View.VISIBLE);
                            driverLayout.setVisibility(View.GONE);
                            geoLocation.setVisibility(View.VISIBLE);
                        } else {
                            showDriverDetails();
                            popupWindow.dismiss();
                            Thread.interrupted();
                        }
                    }
                });
            }
        };
        thread.start();

        // Request Ride
        requestRideNotification();

        cancelRide = requestRidePopUp.findViewById(R.id.cancelRequest);
        cancelRide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();

                locationLayout.setVisibility(View.GONE);
                requestLayout.setVisibility(View.VISIBLE);
                driverLayout.setVisibility(View.GONE);
                geoLocation.setVisibility(View.VISIBLE);
            }
        });
    }

    private void showEndTripPopUp(View view){
        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.MATCH_PARENT;

        locationLayout.setVisibility(View.GONE);
        requestLayout.setVisibility(View.GONE);
        driverLayout.setVisibility(View.GONE);
        geoLocation.setVisibility(View.GONE);

        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        endTripPopUp = Objects.requireNonNull(layoutInflater).inflate(R.layout.modal_end_trip, null);

        boolean focusable = true;
        final PopupWindow popupWindow = new PopupWindow(endTripPopUp, width, height, focusable);
        popupWindow.setElevation(8);

        TextView tripAmount = endTripPopUp.findViewById(R.id.tripAmount);
        tripAmount.setText(Variables.COST);

        findViewById(R.id.app_bar_home).post(new Runnable() {
            @Override
            public void run() {
                popupWindow.showAtLocation(findViewById(R.id.app_bar_home), Gravity.CENTER, 0, 0);
            }
        });

        endTripPopUp.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                popupWindow.isShowing();
                return true;
            }
        });

        Button closePopUp = endTripPopUp.findViewById(R.id.closePopUp);
        closePopUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();

                locationLayout.setVisibility(View.VISIBLE);
                requestLayout.setVisibility(View.GONE);
                driverLayout.setVisibility(View.GONE);
                geoLocation.setVisibility(View.VISIBLE);
            }
        });
    }

    private void requestRideNotification() {
        String email = firebaseUser.getEmail();

        // Application Database Initialization
        AppDatabase appDatabase = AppDatabase.getDatabase(this);
        RequestServices requestServices = new RequestServices(getApplicationContext(), appDatabase);
        requestServices.requestRide(email);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    public void hideKeyboard(View view) {
        final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.passenger_home, menu);
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int itemId = menuItem.getItemId();
        Intent intent;

        if (itemId == R.id.nav_settings) {
            if (Variables.ACTIVITY_STATE == 0) {
                Variables.ACTIVITY_STATE = 1;

                intent = new Intent(this, Settings.class);
                startActivity(intent);
            }
        } else if (itemId == R.id.nav_contacts) {
            if (Variables.ACTIVITY_STATE == 0) {
                Variables.ACTIVITY_STATE = 1;

                intent = new Intent(this, ContactUs.class);
                startActivity(intent);
            }
        } else if (itemId == R.id.nav_trips) {
            if (Variables.ACTIVITY_STATE == 0) {
                Variables.ACTIVITY_STATE = 1;

                intent = new Intent(this, TripsActivity.class);
                startActivity(intent);
            }
        } else if (itemId == R.id.sign_out) {
            if (Variables.ACTIVITY_STATE == 0) {
                Variables.ACTIVITY_STATE = 1;
                FirebaseAuth.getInstance().signOut();

                intent = new Intent(this, SignIn.class);
                startActivity(intent);
                finish();
            }
        }

        return false;
    }

    private void googleMapsUISetting(GoogleMap mMap) {
        mMap.setBuildingsEnabled(true);
        mMap.setIndoorEnabled(true);
        mMap.setTrafficEnabled(false);

        UiSettings mUiSettings = mMap.getUiSettings();

        mUiSettings.setZoomControlsEnabled(true);
        mUiSettings.setCompassEnabled(true);
        mUiSettings.setMyLocationButtonEnabled(false);
        mUiSettings.setScrollGesturesEnabled(true);

        mUiSettings.setZoomGesturesEnabled(true);
        mUiSettings.setTiltGesturesEnabled(true);
        mUiSettings.setRotateGesturesEnabled(true);
    }

    @Override
    public void onMapReady(GoogleMap mMap) {
        googleMap = mMap;

        // Prompt the user for permission.
        getLocationPermission();

        // Turn on the My Location layer and the related control on the map.
        updateLocationUI();

        // Get Current Location of Device
        getDeviceLocation();
    }

    /**
     * Saves the state of the map when the activity is paused.
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (googleMap != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, googleMap.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, mLastKnownLocation);
            super.onSaveInstanceState(outState);
        }
    }

    /**
     * Gets the current location of the device, and positions the map's camera.
     */
    public void getDeviceLocation() {
        try {
            if (mLocationPermissionGranted) {
                final Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();

                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            mLastKnownLocation = task.getResult();

                            if (Variables.DR_TOKEN == null) {
                                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLastKnownLocation.getLatitude(),
                                        mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));

                                mLocationRequest = new LocationRequest();
                                mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY );
                            }

                            // Show Location of Other Devices - Drivers
                            final String latitude = "" + mLastKnownLocation.getLatitude();
                            final String longitude = "" + mLastKnownLocation.getLongitude();

                            ORIG_LTNG = new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude());

                            double latDouble = mLastKnownLocation.getLatitude();
                            double lngDouble = mLastKnownLocation.getLongitude();
                            getLocationAddress(latDouble, lngDouble);

                            final Handler ha = new Handler();
                            ha.postDelayed(new Runnable() {

                                @Override
                                public void run() {
                                    //call function
                                    ha.postDelayed(this, 10000);

                                    locationPool = new LocationPool(getApplicationContext(), googleMap, latitude, longitude);
                                    locationPool.locationPoolRequest();
                                }
                            }, 10000);
                        } else {
                            Log.d(TAG, "Current location is null. Using last know location.");
                            Log.e(TAG, "Exception: %s", task.getException());

                            googleMap.animateCamera(CameraUpdateFactory
                                    .newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                            googleMap.getUiSettings().setMyLocationButtonEnabled(false);

                            enableLoc();
                        }
                    }
                });
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    /**
     * Prompts the user for permission to use the device location.
     */
    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    public void getLocationAddress(double lat, double lng) {
        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            Address obj = addresses.get(0);

            ORIG_NAME = obj.getAddressLine(0);
            textPickUp.setText(ORIG_NAME);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Log.e(TAG, "Location Name Not Found: " + e.getMessage());
        }
    }

    /**
     * Handles the result of the request for location permissions.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
        @NonNull String[] permissions, @NonNull int[] grantResults) {

        mLocationPermissionGranted = false;
        if (requestCode == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionGranted = true;
            }
        }
        updateLocationUI();
    }

    /**
     * Updates the map's UI settings based on whether the user has granted location permission.
     */
    private void updateLocationUI() {
        if (googleMap == null) {
            return;
        }

        try {
            if (mLocationPermissionGranted) {
                googleMap.setMyLocationEnabled(true);
                googleMap.getUiSettings().setMyLocationButtonEnabled(false);
            } else {
                googleMap.setMyLocationEnabled(false);
                googleMap.getUiSettings().setMyLocationButtonEnabled(false);

                mLastKnownLocation = null;
                getLocationPermission();
            }
        } catch (Exception e)  {
            Log.e("Exception: %s", Objects.requireNonNull(e.getMessage()));
        }
    }

    public void settingsRequest() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        locationRequest.setInterval(15 * 1000);
        locationRequest.setFastestInterval(5 * 1000);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true); //this is the key ingredient

        GoogleApiClient mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .build();


        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                final LocationSettingsStates state = result.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can initialize location
                        // requests here.
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied. But could be fixed by showing the user
                        // a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(Home.this, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.
                        break;
                }
            }
        });
    }

    private void enableLoc() {
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(Home.this)
            .addApi(LocationServices.API)
            .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                @Override
                public void onConnected(Bundle bundle) {

                }

                @Override
                public void onConnectionSuspended(int i) {
                    googleApiClient.connect();
                }
            })
            .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                @Override
                public void onConnectionFailed(ConnectionResult connectionResult) {

                    Log.d("Location error","Location error " + connectionResult.getErrorCode());
                }
            }).build();

            googleApiClient.connect();

            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
            locationRequest.setInterval(30 * 1000);
            locationRequest.setFastestInterval(5 * 1000);
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(locationRequest);

            builder.setAlwaysShow(true);

            PendingResult<LocationSettingsResult> result =
                    LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
            result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                @Override
                public void onResult(LocationSettingsResult result) {
                    final Status status = result.getStatus();
                    switch (status.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            try {
                                // Show the dialog by calling startResolutionForResult(),
                                // and check the result in onActivityResult().
                                status.startResolutionForResult(Home.this, REQUEST_LOCATION);
                                getDeviceLocation();
                            } catch (IntentSender.SendIntentException e) {
                                // Ignore the error.
                            }
                            break;
                    }
                }
            });
        }
    }

}
