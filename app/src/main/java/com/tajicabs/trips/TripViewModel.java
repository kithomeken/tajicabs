package com.tajicabs.trips;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.tajicabs.database.AppDatabase;
import com.tajicabs.database.TripRequests;
import com.tajicabs.database.TripRequestsDao;

import java.util.List;

public class TripViewModel extends AndroidViewModel {
    private LiveData<List<TripRequests>> tripsLiveData;

    public TripViewModel(@NonNull Application application) {
        super(application);
        TripRequestsDao tripRequestsDao = AppDatabase.getDatabase(application).tripRequestsDao();
        tripsLiveData = tripRequestsDao.getCompletedTrips();
    }

    LiveData<List<TripRequests>> getCompletedTrips() {
        return tripsLiveData;
    }
}
