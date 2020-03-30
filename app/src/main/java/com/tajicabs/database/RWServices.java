package com.tajicabs.database;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;

import com.tajicabs.configuration.TajiCabs;
import com.tajicabs.global.Variables;

import java.util.UUID;

import javax.xml.validation.Validator;

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
    }

    public void getUserDetails() {
        UserDetails userDetails = userDetailsDao.getUserDetails();

        String emailAdd = (userDetails == null) ? "No Data Found" : userDetails.getEmail();
        String firstName = (userDetails == null) ? "No Data Found" : userDetails.getFirstName();
        String lastName = (userDetails == null) ? "No Data Found" : userDetails.getLastName();
        String phoneNumber = (userDetails == null) ? "No Data Found" : userDetails.getPhoneNumber();
        String firebaseToken = (userDetails == null) ? "No Data Found" : userDetails.getFirebaseToken();

        Variables.ACCOUNT_FNAME = firstName;
        Variables.ACCOUNT_LNAME = lastName;
        Variables.ACCOUNT_EMAIL = emailAdd;
        Variables.ACCOUNT_PHONE = phoneNumber;
        Variables.ACCOUNT_NAME = firstName + " " + lastName;
        Variables.FIREBASE_TOKEN = firebaseToken;

        Log.e(TAG, "USER DETAILS: " + emailAdd + " ===== " + firebaseToken);
    }

    public void endTripUpdate() {
        TripRequests tripRequests = tripRequestsDao.getActiveTripRequest();

        if (tripRequests != null) {
            tripRequests.trip_state = "E";
            tripRequestsDao.updateTripDetails(tripRequests);
        }
    }

    public String getEmailAdd() {
        UserDetails userDetails = userDetailsDao.getUserDetails();
        return (userDetails == null) ? "No Data Found" : userDetails.getEmail();
    }

    public String getFirstName() {
        UserDetails userDetails = userDetailsDao.getUserDetails();
        return (userDetails == null) ? "No Data Found" : userDetails.getFirstName();
    }

    public String getLastName() {
        UserDetails userDetails = userDetailsDao.getUserDetails();
        return (userDetails == null) ? "No Data Found" : userDetails.getLastName();
    }

    public String getPhoneNumber() {
        UserDetails userDetails = userDetailsDao.getUserDetails();
        return (userDetails == null) ? "No Data Found" : userDetails.getPhoneNumber();
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

    public void createTripRequest(@NonNull String trip_id, @NonNull String origin_name, @NonNull String origin_lat,
          @NonNull String origin_lng, @NonNull String destination_name, @NonNull String destination_lat,
          @NonNull String destination_lng, @NonNull String passenger_name, @NonNull String passenger_phone,
          @NonNull String trip_distance, @NonNull String trip_cost, @NonNull String final_destination,
          @NonNull String trip_date, @NonNull String trip_state) {

        TripRequests tripRequests = new TripRequests(trip_id, origin_name, origin_lat, origin_lng, destination_name,
                destination_lat, destination_lng, passenger_name, passenger_phone, trip_distance,
                trip_cost, final_destination, trip_date, trip_state, "", "");

        new createTripRequestAsyncTask(tripRequestsDao).execute(tripRequests);
    }

    public String getActiveTripDetails() {
        TripRequests tripRequests = tripRequestsDao.getActiveTripRequest();
        return (tripRequests == null) ? "No Data Found" : tripRequests.getTrip_id();
    }

/*    public String getAnyTripRequest(String tripId) {
        TripRequests tripRequests = tripRequestsDao.getAnyTripRequest(tripId);
        return (tripRequests == null) ? "No Trip Found" : tripRequests.getRequestTripState();
    }*/

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

        @Override
        protected void onPostExecute(Void result) {
            getUserDetails();
        }
    }
}
