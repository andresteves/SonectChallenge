package com.andresteves.sonectchallenge;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by andresteves on 12/10/2018.
 */

public class ATMAdapter extends RecyclerView.Adapter<ATMAdapter.ViewHolder> {

    Context context;
    ArrayList<ATMObject> atmObjectArrayList;
    LocationManager locationManager;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(ATMObject item);
    }

    public ATMAdapter(Context context, ArrayList<ATMObject> atmObjectArray, LocationManager locMan, OnItemClickListener listener) {
        this.context = context;
        atmObjectArrayList = atmObjectArray;
        locationManager = locMan;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.list_item,parent,false));
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.atmName.setText("Name: " + atmObjectArrayList.get(position).getName());
        holder.address.setText("Address: " + atmObjectArrayList.get(position).getAddress());

        String distanceToUser = "";
        if (locationManager != null) {
            if(locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER) != null) {
                double latitu = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER).getLatitude();
                double longitu = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER).getLongitude();

                Location loc1 = new Location("markerPos");
                loc1.setLatitude(atmObjectArrayList.get(position).getLatitude());
                loc1.setLongitude(atmObjectArrayList.get(position).getLongitude());

                Location userLoc = new Location("userLoc");
                userLoc.setLongitude(longitu);
                userLoc.setLatitude(latitu);

                distanceToUser = String.valueOf(loc1.distanceTo(userLoc));
            }
        }
        holder.distanceUser.setText(distanceToUser);

        final int pos = position;
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(atmObjectArrayList.get(pos));
            }
        });
    }

    @Override
    public int getItemCount() {
        return atmObjectArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView address;
        TextView distanceUser;
        TextView atmName;

        public ViewHolder(View itemView) {
            super(itemView);
            address = itemView.findViewById(R.id.address);
            atmName = itemView.findViewById(R.id.name);
            distanceUser = itemView.findViewById(R.id.distanceToUser);
        }
    }
}
