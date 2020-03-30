package com.tajicabs.trips;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tajicabs.R;
import com.tajicabs.database.TripRequests;

import java.util.List;

public class TripFragment extends Fragment {
    private TripListAdapter tripListAdapter;
    private TripViewModel tripViewModel;
    private Context context;

    public static TripFragment newInstance() {
        return new TripFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        tripListAdapter = new TripListAdapter(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initData();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_trip, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.tripRecyclerView);
        recyclerView.setAdapter(tripListAdapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        return view;
    }

    private void initData() {
        tripViewModel = ViewModelProviders.of(this).get(TripViewModel.class);
        tripViewModel.getCompletedTrips().observe(this, new Observer<List<TripRequests>>() {
            @Override
            public void onChanged(@Nullable List<TripRequests> tripRequestsList) {
                tripListAdapter.setTripRequestsList(tripRequestsList);
            }
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
