package com.tajicabs.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface TripRequestsDao {
    @Insert
    void createTripRequest(TripRequests tripRequests);

    @Query("SELECT * FROM tripRequests WHERE trip_state = 'ACTIVE'")
    TripRequests getActiveTripRequest();

    @Query("SELECT * FROM tripRequests WHERE trip_id = :tripId")
    TripRequests getAnyTripRequest(String tripId);
}
