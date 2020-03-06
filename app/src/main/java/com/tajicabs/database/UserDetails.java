package com.tajicabs.database;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "userDetails",
        indices = {@Index(value = "email", unique = true)})
public class UserDetails {

    public UserDetails(@NonNull String userId, @NonNull String email, @NonNull String first_name,
        @NonNull String last_name, @NonNull String phone_number, @NonNull String firebaseToken) {

        this.userId = userId;
        this.email = email;
        this.first_name = first_name;
        this.last_name = last_name;
        this.phone_number = phone_number;
        this.firebaseToken = firebaseToken;
    }

    @PrimaryKey/*(autoGenerate = true)*/
    @NonNull
    @ColumnInfo(name = "user_id")
    public String userId;

    @ColumnInfo(name = "email")
    @NonNull
    public String email;

    @ColumnInfo(name = "first_name")
    @NonNull
    public String first_name;

    @ColumnInfo(name = "last_name")
    @NonNull
    public String last_name;

    @ColumnInfo(name = "phone_number")
    @NonNull
    public String phone_number;

    @ColumnInfo(name = "firebase_token")
    @NonNull
    public String firebaseToken;

    @NonNull
    public String getUserId() {
        return userId;
    }

    @NonNull
    public String getEmail() {
        return email;
    }

    @NonNull
    public String getFirstName() {
        return first_name;
    }

    @NonNull
    public String getLastName() {
        return last_name;
    }

    @NonNull
    public String getPhoneNumber() {
        return phone_number;
    }

    @NonNull
    public String getFirebaseToken() {
        return firebaseToken;
    }
}
