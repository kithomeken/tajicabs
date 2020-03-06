package com.tajicabs.database;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "tripRequests")
public class TripRequests {
    public TripRequests(@NonNull String tripId, @NonNull String origin, @NonNull String destination,
        @NonNull String distance, @NonNull String cost, String driverToken, String driverName,
        String driverPhone, String vehicleRegNo, String vehicleMake, @NonNull String requestTripState) {

        this.tripId = tripId;
        this.origin = origin;
        this.destination = destination;
        this.distance = distance;
        this.cost = cost;
        this.driverToken = driverToken;
        this.driverName = driverName;
        this.driverPhone = driverPhone;
        this.vehicleRegNo = vehicleRegNo;
        this.vehicleMake = vehicleMake;
        this.requestTripState = requestTripState;
    }

    @PrimaryKey/*(autoGenerate = true)*/
    @NonNull
    @ColumnInfo(name = "trip_id")
    public String tripId;

    @ColumnInfo(name = "origin")
    @NonNull
    public String origin;

    @ColumnInfo(name = "destination")
    @NonNull
    public String destination;

    @ColumnInfo(name = "distance")
    @NonNull
    public String distance;

    @ColumnInfo(name = "cost")
    @NonNull
    public String cost;

    @ColumnInfo(name = "driver_token")
    public String driverToken;

    @ColumnInfo(name = "driver_name")
    public String driverName;

    @ColumnInfo(name = "driver_phone")
    public String driverPhone;

    @ColumnInfo(name = "reg_no")
    public String vehicleRegNo;

    @ColumnInfo(name = "vehicle_make")
    public String vehicleMake;

    @ColumnInfo(name = "trip_state")
    @NonNull
    public String requestTripState;

    @NonNull
    public String getTripId() {
        return tripId;
    }

    @NonNull
    public String getOrigin() {
        return origin;
    }

    @NonNull
    public String getDestination() {
        return destination;
    }

    @NonNull
    public String getDistance() {
        return distance;
    }

    @NonNull
    public String getCost() {
        return cost;
    }

    public String getDriverToken() {
        return driverToken;
    }

    public String getDriverName() {
        return driverName;
    }

    public String getDriverPhone() {
        return driverPhone;
    }

    public String getVehicleRegNo() {
        return vehicleRegNo;
    }

    public String getVehicleMake() {
        return vehicleMake;
    }

    @NonNull
    public String getRequestTripState() {
        return requestTripState;
    }
}
