package com.tajicabs.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface TripRequestsDao {
    @Insert
    void createTripRequest(TripRequests tripRequests);

    @Query("SELECT * FROM tripDetails WHERE trip_state = 'A'")
    TripRequests getActiveTripRequest();

    @Query("SELECT * FROM tripDetails WHERE trip_state = 'NP'")
    TripRequests requestTripData();

    @Update(onConflict = OnConflictStrategy.IGNORE)
    void updateTripDetails(TripRequests tripDetails);

    @Query("SELECT * FROM tripDetails WHERE trip_id = :tripId")
    TripRequests getTripDetails(String tripId);

    @Query("SELECT * FROM tripDetails WHERE trip_state = 'E'")
    LiveData<List<TripRequests>> getCompletedTrips();
}
