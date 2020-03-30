package com.tajicabs.trips;

import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tajicabs.R;
import com.tajicabs.database.TripRequests;

import java.util.List;

public class TripListAdapter extends RecyclerView.Adapter<TripListAdapter.ViewHolder> {
    private LayoutInflater layoutInflater;
    private List<TripRequests> tripRequestsList;
    private Context context;

    TripListAdapter(Context context) {
        this.layoutInflater = LayoutInflater.from(context);
        this.context = context;
    }

    public void setTripRequestsList(List<TripRequests> tripRequestsList) {
        this.tripRequestsList = tripRequestsList;
        notifyDataSetChanged();
    }

    @Override
    public TripListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View itemView = layoutInflater.inflate(R.layout.item_trip_list, parent, false);
        return new TripListAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(TripListAdapter.ViewHolder holder, int position) {
        if (tripRequestsList == null) {
            return;
        }

        final TripRequests tripRequests = tripRequestsList.get(position);
        if (tripRequests != null) {

            holder.startPoint.setText(tripRequests.origin_name);
            holder.endPoint.setText(tripRequests.destination_name);
            holder.driverName.setText(tripRequests.driver_name);
            holder.distance.setText(tripRequests.trip_distance);
            holder.tripCost.setText(tripRequests.trip_cost);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if (tripRequestsList == null) {
            return 0;
        } else {
            return tripRequestsList.size();
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final View mView;
        final TextView startPoint, endPoint, driverName, distance, tripCost;

        ViewHolder(View view) {
            super(view);
            mView = view;

            startPoint = view.findViewById(R.id.startPoint);
            endPoint = view.findViewById(R.id.endPoint);
            driverName = view.findViewById(R.id.driverName);
            distance = view.findViewById(R.id.distance);
            tripCost = view.findViewById(R.id.tripCost);
        }
    }
}
