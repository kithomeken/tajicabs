package com.tajicabs.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface UserDetailsDao {

    @Insert
    void createNewUser(UserDetails userDetails);

    @Query("SELECT * FROM userDetails LIMIT 1")
    UserDetails getUserDetails();
}
