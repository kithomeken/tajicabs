package com.tajicabs.database;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;

import com.tajicabs.configuration.TajiCabs;

import java.util.UUID;

import static com.tajicabs.constants.Constants.ACTIVE_TRIP_STATE;

// Read Write Services for Application Database
public class RWServices {
    private final String TAG = RWServices.class.getName();

    private AppDatabase appDatabase;

    private UserDetailsDao userDetailsDao;
    private TripRequestsDao tripRequestsDao;

    public RWServices(AppDatabase appDatabase) {
        this.appDatabase = appDatabase;

        userDetailsDao = appDatabase.userDetailsDao();
        tripRequestsDao = appDatabase.tripRequestsDao();
    }

    public void createUser(@NonNull String email, @NonNull String first_name,
        @NonNull String last_name, @NonNull String phone_number, @NonNull String firebaseToken) {

        final String userId = UUID.randomUUID().toString();
        UserDetails userDetails = new UserDetails(userId, email, first_name,
                last_name, phone_number, firebaseToken);

        new createUserAsyncTask(userDetailsDao).execute(userDetails);

        getUserDetails();
    }

    public void getUserDetails() {
        UserDetails userDetails = userDetailsDao.getUserDetails();

        String emailAdd = (userDetails == null) ? "No Data Found" : userDetails.getEmail();
        String firstName = (userDetails == null) ? "No Data Found" : userDetails.getFirstName();
        String lastName = (userDetails == null) ? "No Data Found" : userDetails.getLastName();
        String phoneNumber = (userDetails == null) ? "No Data Found" : userDetails.getPhoneNumber();
        String firebaseToken = (userDetails == null) ? "No Data Found" : userDetails.getFirebaseToken();

        TajiCabs.NAMES = firstName + " " + lastName;
        TajiCabs.EMAIL = emailAdd;
        TajiCabs.PHONE = phoneNumber;
        TajiCabs.FIREBASE_TOKEN = firebaseToken;

        Log.e(TAG, "USER DETAILS: " + emailAdd + " ===== " + firebaseToken);
    }

    @SuppressLint("StaticFieldLeak")
    private class createUserAsyncTask extends AsyncTask<UserDetails, Void, Void> {
        UserDetailsDao userDetailsDao;

        private createUserAsyncTask(UserDetailsDao userDetailsDao) {
            this.userDetailsDao = userDetailsDao;
        }

        @Override
        protected Void doInBackground(UserDetails... userDetails) {
            userDetailsDao.createNewUser(userDetails[0]);
            return null;
        }
    }

    public void createTripRequest(@NonNull String tripId, @NonNull String origin, @NonNull String destination,
        @NonNull String distance, @NonNull String cost, String driverToken, String driverName,
        String driverPhone, String vehicleRegNo, String vehicleMake) {

        TripRequests tripRequests = new TripRequests(tripId, origin, destination, distance, cost,
                driverToken, driverName, driverPhone, vehicleRegNo, vehicleMake, ACTIVE_TRIP_STATE);

        new createTripRequestAsyncTask(tripRequestsDao).execute(tripRequests);
    }

    public void getActiveTripDetails() {
        TripRequests tripRequests = tripRequestsDao.getActiveTripRequest();

        String tripId = (tripRequests == null) ? "No Data Found" : tripRequests.getTripId();
        String tripState = (tripRequests == null) ? "No Data Found" : tripRequests.getRequestTripState();
    }

    public String getAnyTripRequest(String tripId) {
        TripRequests tripRequests = tripRequestsDao.getAnyTripRequest(tripId);
        return (tripRequests == null) ? "No Trip Found" : tripRequests.getRequestTripState();
    }

    @SuppressLint("StaticFieldLeak")
    private class createTripRequestAsyncTask extends AsyncTask<TripRequests, Void, Void> {
        TripRequestsDao tripRequestsDao;

        private createTripRequestAsyncTask(TripRequestsDao tripRequestsDao) {
            this.tripRequestsDao = tripRequestsDao;
        }

        @Override
        protected Void doInBackground(TripRequests... tripRequests) {
            tripRequestsDao.createTripRequest(tripRequests[0]);
            return null;
        }
    }
}
