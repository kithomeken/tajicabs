package com.tajicabs.database;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
@Entity(tableName = "tripDetails")
public class TripRequests {
    public TripRequests(@NonNull String trip_id, @NonNull String origin_name, @NonNull String origin_lat,
                       @NonNull String origin_lng, @NonNull String destination_name, @NonNull String destination_lat,
                       @NonNull String destination_lng, @NonNull String passenger_name, @NonNull String passenger_phone,
                       @NonNull String trip_distance, @NonNull String trip_cost, @NonNull String final_destination,
                       @NonNull String trip_date, @NonNull String trip_state, String driver_name, String driver_phone) {

        this.trip_id = trip_id;
        this.origin_name = origin_name;
        this.origin_lat = origin_lat;
        this.origin_lng = origin_lng;
        this.destination_name = destination_name;
        this.destination_lat = destination_lat;
        this.destination_lng = destination_lng;
        this.passenger_name = passenger_name;
        this.passenger_phone = passenger_phone;
        this.trip_distance = trip_distance;
        this.trip_cost = trip_cost;
        this.final_destination = final_destination;
        this.trip_date = trip_date;
        this.trip_state = trip_state;

        this.driver_name = driver_name;
        this.driver_phone = driver_phone;
    }

    @PrimaryKey/*(autoGenerate = true)*/
    @NonNull
    @ColumnInfo(name = "trip_id")
    public String trip_id;

    @ColumnInfo(name = "origin_name")
    @NonNull
    public String origin_name;

    @ColumnInfo(name = "origin_lat")
    @NonNull
    String origin_lat;

    @ColumnInfo(name = "origin_lng")
    @NonNull
    public String origin_lng;

    @ColumnInfo(name = "destination_name")
    @NonNull
    public String destination_name;

    @ColumnInfo(name = "destination_lat")
    @NonNull
    private String destination_lat;

    @ColumnInfo(name = "destination_lng")
    @NonNull
    private String destination_lng;

    @ColumnInfo(name = "passenger_name")
    @NonNull
    private String passenger_name;

    @ColumnInfo(name = "passenger_phone")
    @NonNull
    private String passenger_phone;

    @ColumnInfo(name = "trip_distance")
    @NonNull
    public String trip_distance;

    @ColumnInfo(name = "trip_cost")
    @NonNull
    public String trip_cost;

    @ColumnInfo(name = "final_destination")
    @NonNull
    private String final_destination;

    @ColumnInfo(name = "trip_date")
    @NonNull
    private String trip_date;

    @ColumnInfo(name = "trip_state")
    @NonNull
    public String trip_state;

    @ColumnInfo(name = "driver_name")
    public String driver_name;

    @ColumnInfo(name = "driver_phone")
    public String driver_phone;

    @NonNull
    String getTrip_id() {
        return trip_id;
    }

    @NonNull
    public String getOrigin_name() {
        return origin_name;
    }

    @NonNull
    public String getOrigin_lat() {
        return origin_lat;
    }

    @NonNull
    public String getOrigin_lng() {
        return origin_lng;
    }

    @NonNull
    public String getDestination_name() {
        return destination_name;
    }

    @NonNull
    public String getDestination_lat() {
        return destination_lat;
    }

    @NonNull
    public String getDestination_lng() {
        return destination_lng;
    }

    @NonNull
    public String getPassenger_name() {
        return passenger_name;
    }

    @NonNull
    public String getPassenger_phone() {
        return passenger_phone;
    }

    @NonNull
    public String getTrip_distance() {
        return trip_distance;
    }

    @NonNull
    public String getTrip_cost() {
        return trip_cost;
    }

    @NonNull
    public String getFinal_destination() {
        return final_destination;
    }

    @NonNull
    public String getTrip_date() {
        return trip_date;
    }

    @NonNull
    public String getTripState() {
        return trip_state;
    }

    @NonNull
    public String getDriver_name() {
        return driver_name;
    }

    @NonNull
    public String getDriver_phone() {
        return driver_phone;
    }
}