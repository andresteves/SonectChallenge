package com.andresteves.sonectchallenge;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private final String API_URL = "http://207.154.210.145:8080/data/ATM_20181005_DEV.json";
    private String[] permissions = { Manifest.permission.ACCESS_FINE_LOCATION };
    private LocationManager locationManager;
    private RecyclerView listAtms;
    private ArrayList<ATMObject> atmObjectArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_maps);

        atmObjectArrayList = new ArrayList<>();

        listAtms = findViewById(R.id.atmList);
        listAtms.setLayoutManager(new LinearLayoutManager(this));
        listAtms.setHasFixedSize(true);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, permissions, 29854);
        }else{
            locationCaller();
        }

        apiCall();
    }

    @SuppressLint("MissingPermission")
    private void locationCaller()
    {
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        // Define a listener that responds to location updates
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {}

            public void onProviderEnabled(String provider) {}

            public void onProviderDisabled(String provider) {}
        };

        // Register the listener with the Location Manager to receive location updates
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 200, 0, locationListener);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.animateCamera(CameraUpdateFactory.zoomTo(3.0f));

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                mMap.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
                marker.showInfoWindow();
                return true;
            }
        });
    }

    private void apiCall()
    {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, API_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);

                    for (int i=0; i < jsonArray.length(); i++)
                    {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                        ATMObject atmObject = new ATMObject();
                        atmObject.setName(jsonObject.getString("name"));
                        atmObject.setAddress(jsonObject.getJSONObject("address").getString("formatted"));
                        atmObject.setLatitude(jsonObject.getDouble("latitude"));
                        atmObject.setLongitude(jsonObject.getDouble("longitude"));

                        atmObjectArrayList.add(atmObject);

                        LatLng markerLocation = new LatLng(jsonObject.getDouble("latitude"), jsonObject.getDouble("longitude"));

                        String listingName = "Welcome to " + jsonObject.getString("name");
                        mMap.addMarker(new MarkerOptions().position(markerLocation).title(listingName));
                    }

                    listAtms.setAdapter(new ATMAdapter(MapsActivity.this, atmObjectArrayList, locationManager, new ATMAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(ATMObject item) {
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(item.getLatitude(),item.getLongitude()),16.0f));
                        }
                    }));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        requestQueue.add(stringRequest);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == 29854 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
        {
            locationCaller();
        }
    }
}
