package com.tajicabs.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface UserDetailsDao {

    @Insert
    void createNewUser(UserDetails userDetails);

    @Query("SELECT * FROM userDetails LIMIT 1")
    UserDetails getUserDetails();

    @Update(onConflict = OnConflictStrategy.IGNORE)
    void updateUserDetails(UserDetails userDetails);
}
