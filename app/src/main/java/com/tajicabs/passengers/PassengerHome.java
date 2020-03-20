package com.tajicabs.passengers;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.tajicabs.configuration.TajiCabs;
import com.tajicabs.database.AppDatabase;
import com.tajicabs.directions.TajiDirections;
import com.tajicabs.geolocation.LocationPool;
import com.tajicabs.services.RequestServices;

import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static com.tajicabs.configuration.TajiCabs.ACTIVITY_STATE;
import static com.tajicabs.configuration.TajiCabs.COST;
import static com.tajicabs.configuration.TajiCabs.DEFAULT_ZOOM;
import static com.tajicabs.configuration.TajiCabs.DEST_LTNG;
import static com.tajicabs.configuration.TajiCabs.DEST_NAME;
import static com.tajicabs.configuration.TajiCabs.DISTANCE;
import static com.tajicabs.configuration.TajiCabs.DR_MAKE;
import static com.tajicabs.configuration.TajiCabs.DR_NAME;
import static com.tajicabs.configuration.TajiCabs.DR_PHONE;
import static com.tajicabs.configuration.TajiCabs.DR_REG;
import static com.tajicabs.configuration.TajiCabs.DR_TOKEN;
import static com.tajicabs.configuration.TajiCabs.GOOGLE_API;
import static com.tajicabs.configuration.TajiCabs.NAMES;
import static com.tajicabs.configuration.TajiCabs.ORIG_LTNG;
import static com.tajicabs.configuration.TajiCabs.ORIG_NAME;
import static com.tajicabs.configuration.TajiCabs.REQUEST_LOCATION;

public class PassengerHome extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        OnMapReadyCallback {

    private static final String TAG = PassengerHome.class.getName();

    // Dependency classes
    TajiDirections tajiDirections = new TajiDirections();
    LocationPool locationPool;

    private FirebaseAuth mAuth;
    private FirebaseUser firebaseUser;

    protected static final int overview = 0;
    protected static final int REQUEST_CHECK_SETTINGS = 0x1;

    private AppBarConfiguration mAppBarConfiguration;
    private GoogleMap mMap;
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

    private LinearLayout requestBlock, locationBlock, driverBlock;
    private FloatingActionButton geoLocation;
    private EditText textPickUp, textDropOffs;
    private Button requestRide, cancelRide;

    private static String EDIT_TEXT_TYPE = "E";
    private View requestRidePopUp, endTripPopUp;
    private TextView fromDisp, toDisp, distanceCovered, costDisp;
    private TextView driverName, vehicleMake, vehicleReg, drivePhone;

    @Override
    public void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        checkFirebaseSession(currentUser);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passenger_home);

        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();

        if (savedInstanceState != null) {
            mLastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
        }

        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), GOOGLE_API);
        }

        locationBlock = findViewById(R.id.locationBlock);
        requestBlock = findViewById(R.id.requestBlock);
        driverBlock = findViewById(R.id.driverBlock);

        driverName = findViewById(R.id.driverName);
        vehicleMake = findViewById(R.id.vehicleMake);
        vehicleReg = findViewById(R.id.vehicleReg);
        drivePhone = findViewById(R.id.driverPhone);

        if (DR_TOKEN != null) {
            // Show Driver Details
            requestBlock.setVisibility(View.GONE);
            locationBlock.setVisibility(View.GONE);
            driverBlock.setVisibility(View.VISIBLE);

            driverAcceptBlock();
        } else {
            requestBlock.setVisibility(View.GONE);
            locationBlock.setVisibility(View.VISIBLE);
            driverBlock.setVisibility(View.GONE);
        }

        Button cancelRequest = findViewById(R.id.cancelRequest);
        cancelRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestBlock.setVisibility(View.GONE);
                locationBlock.setVisibility(View.VISIBLE);
                driverBlock.setVisibility(View.GONE);
            }
        });

        // Set Pick Up Point
        placesPickUp();

        // Set Drop Off Point
        placeDropOff();

        mLocationRequest = new LocationRequest().setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_fragment);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Set display name, photo url and email address of signed in user
        View navHeaderView = navigationView.getHeaderView(0);
        TextView accountName = (TextView) navHeaderView.findViewById(R.id.accountName);
        TextView accountEmail = (TextView) navHeaderView.findViewById(R.id.accountEmail);

        assert firebaseUser != null;
        accountName.setText(NAMES);
        accountEmail.setText(firebaseUser.getEmail());

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow,
                R.id.nav_tools, R.id.nav_share, R.id.nav_send)
                .setDrawerLayout(drawer)
                .build();

        geoLocation = findViewById(R.id.geo_location);
        geoLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDeviceLocation();
            }
        });

        requestRideAction();

        if (TajiCabs.END_TRIP == "1") {
            View view = new View(getApplicationContext());
            showEndTripPopUp(view);
        }
    }

    private void driverAcceptBlock() {
        driverName.setText("Driver:" + DR_NAME);
        drivePhone.setText("Phone Number: " + DR_PHONE);
        vehicleMake.setText(DR_MAKE);
        vehicleReg.setText(DR_REG);
    }

    private void placesPickUp() {
        textPickUp = findViewById(R.id.textPickUp);
        textPickUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String status = "PickUp";
                editTextType(status);
                onSearchCalled();
            }
        });

        textPickUp.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    String status = "PickUp";
                    editTextType(status);
                    onSearchCalled();
                }
            }
        });
    }

    private void placeDropOff() {
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
        // Set the fields to specify which types of place data to return.
        List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG);
        // Start the autocomplete intent.
        Intent intent = new Autocomplete.IntentBuilder(
                AutocompleteActivityMode.FULLSCREEN, fields).setCountry("KE")
                .build(this);
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
    }

    private void drawDirections() {
        if (DEST_LTNG != null && ORIG_LTNG != null) {
            mMap.clear();
            googleMapsUISetting(mMap);

            DirectionsResult directionsResult = tajiDirections
                    .getDirectionsDetails(ORIG_LTNG, DEST_LTNG, TravelMode.DRIVING);

            if (directionsResult != null) {
                tajiDirections.addPolyline(directionsResult, mMap);
                tajiDirections.positionCamera(directionsResult.routes[overview], mMap);
                tajiDirections.addMarkersToMap(directionsResult, mMap);
            }

            DISTANCE = tajiDirections.distanceInMeters(directionsResult);

            fromDisp = findViewById(R.id.fromDisp);
            toDisp = findViewById(R.id.toDisp);
            distanceCovered = findViewById(R.id.distanceCovered);
            costDisp = findViewById(R.id.costDisp);

            fromDisp.setText("From: " + ORIG_NAME);
            toDisp.setText("To: " + DEST_NAME);
            distanceCovered.setText(DISTANCE);
            costDisp.setText(tajiDirections.costCalculator(DISTANCE));

            requestBlock.setVisibility(View.VISIBLE);
            locationBlock.setVisibility(View.GONE);

            changeMarginBottom();

            // Location Pool

        }
    }

    private void changeMarginBottom() {
        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams)
                geoLocation.getLayoutParams();
        layoutParams.setMargins(0, 0, 0,300 );
        geoLocation.setLayoutParams(layoutParams);
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
                    drawDirections();
                } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                    // TODO: Handle the error.
                    Status status = Autocomplete.getStatusFromIntent(data);
                    Log.i(TAG, status.getStatusMessage());
                } else if (resultCode == RESULT_CANCELED) {
                    // The user canceled the operation.
                }
            }
        } else {
            switch (requestCode) {
                // Check for the integer request code originally supplied to startResolutionForResult().
                case REQUEST_CHECK_SETTINGS:
                    switch (resultCode) {
                        case Activity.RESULT_OK:
                            getDeviceLocation();
                        break;

                        case Activity.RESULT_CANCELED:
                            settingsRequest();//keep asking if imp or do whatever
                        break;
                    }
                break;
            }
        }
    }

    private void checkFirebaseSession(FirebaseUser user) {
        if (user == null) {
            //Return to SignInActivity
            Intent intent = new Intent(PassengerHome.this, SignInActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }

        if (requestBlock.getVisibility() == View.VISIBLE) {
            requestBlock.setVisibility(View.GONE);
            locationBlock.setVisibility(View.VISIBLE);

            super.onBackPressed();
        } else {
            super.onBackPressed();
        }
    }

    private void requestRideAction() {
        // Request Ride Action
        requestRide = findViewById(R.id.requestRide);
        requestRide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                hideKeyboard(v);
                showRequestPopUp(v);
            }
        });
    }

    public void hideKeyboard(View view) {
        final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void showEndTripPopUp(View view){
        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.MATCH_PARENT;

        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        endTripPopUp = layoutInflater.inflate(R.layout.modal_end_trip, null);

        boolean focusable = true;
        final PopupWindow popupWindow = new PopupWindow(endTripPopUp, width, height, focusable);
        popupWindow.setElevation(8);

        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        endTripPopUp.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                popupWindow.dismiss();
                return true;
            }
        });

        TextView costText = endTripPopUp.findViewById(R.id.costDisp);
        costText.setText(COST);

        Button closePopUp = endTripPopUp.findViewById(R.id.closePopUp);
        closePopUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
    }

    private void hidePopUp(View view) {
        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.MATCH_PARENT;

        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        requestRidePopUp = layoutInflater.inflate(R.layout.modal_request_ride, null);

        boolean focusable = true;
        final PopupWindow popupWindow = new PopupWindow(requestRidePopUp, width, height, focusable);
        popupWindow.setElevation(8);
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        if (popupWindow.isShowing()) {
            popupWindow.dismiss();
        }
    }

    private void showRequestPopUp(View view){
        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.MATCH_PARENT;

        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        requestRidePopUp = layoutInflater.inflate(R.layout.modal_request_ride, null);

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
                        Toast.makeText(PassengerHome.this, "No Driver Found. Try again Laters", Toast.LENGTH_LONG).show();
                        popupWindow.dismiss();
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
            }
        });
    }

    private void requestRideNotification() {
        ACTIVITY_STATE = 702;
        String email = firebaseUser.getEmail();
        COST = costDisp.getText().toString();

        // Application Database Initialization
        AppDatabase appDatabase = AppDatabase.getDatabase(this);
        RequestServices requestServices = new RequestServices(getApplicationContext(), appDatabase);
        requestServices.requestRide(email);
    }





    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.passenger_home, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }



    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int itemId = menuItem.getItemId();
        Intent intent;

        if (itemId == R.id.profile) {
            intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
        } else if (itemId == R.id.sign_out) {
            FirebaseAuth.getInstance().signOut();

            intent = new Intent(this, SignInActivity.class);
            startActivity(intent);
            finish();
        }
        return false;
    }


    public void getAddress(double lat, double lng) {
        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            Address obj = addresses.get(0);
            String add = obj.getAddressLine(0);
//            add = add + "\n" + obj.getCountryName();
//            add = add + "\n" + obj.getCountryCode();
//            add = add + "\n" + obj.getAdminArea();
//            add = add + "\n" + obj.getPostalCode();
//            add = add + "\n" + obj.getSubAdminArea();
//            add = add + "\n" + obj.getLocality();
//            add = add + "\n" + obj.getSubThoroughfare();

            ORIG_NAME = add;

            Log.v(TAG, "Address: " + add);
            // Toast.makeText(this, "Address=>" + add,
            // Toast.LENGTH_SHORT).show();

            // TennisAppActivity.showDialog(add);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Log.e(TAG, "Location Name Not Found: " + e.getMessage());
//            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

//        boolean success = googleMap.setMapStyle(new MapStyleOptions(getResources()
//                .getString(R.string.google_maps_theme)));
//
//        if (!success) {
//            Log.e(TAG, "==========================Style parsing failed.");
//        }

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
        if (mMap != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, mMap.getCameraPosition());
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

                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLastKnownLocation.getLatitude(),
                                    mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));

                            Log.e(TAG, "====================================" + mLastKnownLocation.getLatitude());
                            Log.e(TAG, "====================================" + mLastKnownLocation.getLongitude());

                            mLocationRequest = new LocationRequest();
                            mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY );

                            // Show Location of Other Devices - Drivers
                            final String latitude = "" + mLastKnownLocation.getLatitude();
                            final String longitude = "" + mLastKnownLocation.getLongitude();

                            ORIG_LTNG = new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude());

                            double latDouble = mLastKnownLocation.getLatitude();
                            double lngDouble = mLastKnownLocation.getLongitude();
                            getAddress(latDouble, lngDouble);

                            final Handler ha = new Handler();
                            ha.postDelayed(new Runnable() {

                                @Override
                                public void run() {
                                    //call function
                                    ha.postDelayed(this, 10000);

                                    locationPool = new LocationPool(getApplicationContext(), mMap, latitude, longitude);
                                    locationPool.locationPoolRequest();
                                }
                            }, 10000);
                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());

                            mMap.animateCamera(CameraUpdateFactory
                                    .newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                            mMap.getUiSettings().setMyLocationButtonEnabled(false);

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

    /**
     * Handles the result of the request for location permissions.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
        updateLocationUI();
    }

    /**
     * Updates the map's UI settings based on whether the user has granted location permission.
     */
    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }

        try {
            if (mLocationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mLastKnownLocation = null;
                getLocationPermission();
            }
        } catch (Exception e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    protected void createLocationRequest() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                // All location settings are satisfied. The client can initialize
                // location requests here.
                // ...
            }
        });

        task.addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    // Location settings are not satisfied, but this can be fixed
                    // by showing the user a dialog.
                    try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(PassengerHome.this,
                                REQUEST_CHECK_SETTINGS);
                    } catch (IntentSender.SendIntentException sendEx) {
                        // Ignore the error.
                    }
                }
            }
        });

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
                            status.startResolutionForResult(PassengerHome.this, REQUEST_CHECK_SETTINGS);
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

    private boolean hasGPSDevice(Context context) {
        final LocationManager mgr = (LocationManager) context
                .getSystemService(Context.LOCATION_SERVICE);
        if (mgr == null)
            return false;

        final List<String> providers = mgr.getAllProviders();

        if (providers == null)
            return false;

        return providers.contains(LocationManager.GPS_PROVIDER);
    }

    private void enableLoc() {
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(PassengerHome.this)
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
                                status.startResolutionForResult(PassengerHome.this, REQUEST_LOCATION);
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

    public void changeBlock() {
        if (DEST_LTNG != null && ORIG_LTNG != null) {
            TextView origPlace, destPlace;

            origPlace = findViewById(R.id.origin_place);
            destPlace = findViewById(R.id.dest_place);

            origPlace.setText(ORIG_NAME);
            destPlace.setText(DEST_NAME);

            Button requestRide = findViewById(R.id.request_ride);

            requestRide.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ProgressDialog mProgressDialog;
                    mProgressDialog = new ProgressDialog(getApplicationContext());

                    if (mProgressDialog == null) {
                        mProgressDialog.setMessage("Requesting");
                        mProgressDialog.setIndeterminate(true);
                        mProgressDialog.setCanceledOnTouchOutside(false);
                        mProgressDialog.setCancelable(false);
                    }

                    mProgressDialog.show();
                }
            });
        }
    }
}









//        String email = user.getEmail();
//
//        // Get the collection reference
//        DocumentReference documentReference = db.collection("passengers").document(email);
//        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                if (task.isSuccessful()) {
//                    DocumentSnapshot document = task.getResult();
//
//                    if (document.exists()) {
//                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
//                    } else {
//                        Log.d(TAG, "No such document");
//                    }
//                } else {
//                    Log.d(TAG, "get failed with ", task.getException());
//                }
//            }
//        });


/*
    */
/**
     * Displays a form allowing the user to select a place from a list of likely places.
     *//*

    private void openPlacesDialog() {
        // Ask the user to choose the place where they are now.
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // The "which" argument contains the position of the selected item.
                LatLng markerLatLng = mLikelyPlaceLatLngs[which];
                String markerSnippet = mLikelyPlaceAddresses[which];
                if (mLikelyPlaceAttributions[which] != null) {
                    markerSnippet = markerSnippet + "\n" + mLikelyPlaceAttributions[which];
                }

                // Add a marker for the selected place, with an info window
                // showing information about that place.
                mMap.addMarker(new MarkerOptions()
                        .title(mLikelyPlaceNames[which])
                        .position(markerLatLng)
                        .snippet(markerSnippet));

                // Position the map's camera at the location of the marker.
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(markerLatLng,
                        DEFAULT_ZOOM));
            }
        };

        // Display the dialog.
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.pick_place)
                .setItems(mLikelyPlaceNames, listener)
                .show();
    }*/
