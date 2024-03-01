package com.example.finalsproject.dashboard_user_activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.finalsproject.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.util.List;

public class subcontractor extends AppCompatActivity {
    public static final int DEFAULT_UPDATE_INTERVAL = 30;
    public static final int FAST_UPDATE_INTERVAL = 3;
    private static final int PERMISSIONS_FINE_LOCATION = 0;
    TextView tv_lat, tv_lon, tv_altitude,tv_accuracy,tv_speed,tv_sensor,tv_updates,tv_address, tv_MarkerCount;
    Button btn_newWayPoint, btn_showWayPoint, btn_showMap;
    @SuppressLint("UseSwitchCompatOrMaterialCode")

    //WAYPOINT VARIABLES//
    Switch sw_location_updates,sw_gps;
    Boolean updateOn = false;
    Location currentLocation;
    List<Location> savedLocations;

    //GOOGLE API LOCATION SERVICES//
    FusedLocationProviderClient fusedLocationProviderClient;
    LocationRequest locationRequest;
    LocationCallback locationCallBack;


    //START OF CLASS//
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subcontractor);

        tv_lat = findViewById(R.id.tv_lat);
        tv_lon = findViewById(R.id.tv_lon);
        tv_altitude = findViewById(R.id.tv_altitude);
        tv_accuracy = findViewById(R.id.tv_accuracy);
        tv_speed = findViewById(R.id.tv_speed);
        tv_sensor = findViewById(R.id.tv_sensor);
        tv_updates = findViewById(R.id.tv_updates);
        tv_address = findViewById(R.id.tv_address);
        tv_MarkerCount = findViewById(R.id.tv_MarkerCount);
        sw_gps = findViewById(R.id.sw_gps);
        sw_location_updates = findViewById(R.id.sw_locationsupdates);
        btn_newWayPoint = findViewById(R.id.btn_newWayPoint);
        btn_showWayPoint = findViewById(R.id.btn_showWayPoint);
        btn_showMap = findViewById(R.id.btn_showMap);
        locationRequest = new LocationRequest();
        locationRequest.setInterval(1000*DEFAULT_UPDATE_INTERVAL);
        locationRequest.setFastestInterval(1000*FAST_UPDATE_INTERVAL);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        locationCallBack = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);

                Location location = locationResult.getLastLocation();
                updateUIValues(location);
                currentLocation = location;
            }
        };

        //LISTENERS//
        btn_newWayPoint.setOnClickListener(v->{
            class_history_list subcontractorHistoryList = (class_history_list)getApplicationContext();
            savedLocations = subcontractorHistoryList.getMyLocations();
            savedLocations.add(currentLocation);
        });

        btn_showWayPoint.setOnClickListener(v->{
            startActivity(new Intent(getApplicationContext(), class_history.class));
        });

        btn_showMap.setOnClickListener(v->{
            startActivity(new Intent(getApplicationContext(),map_subcontractor.class));
        });

        sw_gps.setOnClickListener(v->{
            if(sw_gps.isChecked()){
                locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                tv_sensor.setText("Using high power mode");
            }
            else {
                locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
                tv_sensor.setText("Using battery mode for location");
            }
        });
        sw_location_updates.setOnClickListener(v->{
            if(sw_location_updates.isChecked()){
                startLocationUpdates();
            }
            else{
                stopLocationUpdates();
            }
        });
        updateGPS();
    }
    //END OF CLASS//

    //METHODS//

    //LOCATION UPDATES//
    @SuppressLint("MissingPermission")
    private void startLocationUpdates() {
        tv_updates.setText("Location is being tracked");
        fusedLocationProviderClient.requestLocationUpdates(locationRequest,locationCallBack,null); // location tracking start line //
        updateGPS();
    }
    private void stopLocationUpdates() {
        tv_updates.setText("Location is not being tracked");
        tv_lat.setText("Not tracking location");
        tv_lon.setText("Not tracking location");
        tv_speed.setText("Not tracking location");
        tv_address.setText("Not tracking location");
        tv_accuracy.setText("Not tracking location");
        tv_altitude.setText("Not tracking location");
        tv_sensor.setText("Not tracking location");
        fusedLocationProviderClient.removeLocationUpdates(locationCallBack); // location tracking stop line //
    }
    //LOCATION UPDATES//

    //LOCATION PERMISSION REQUEST//
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_FINE_LOCATION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                updateGPS();
            }
            else{
                finish();//APP CAN'T WORK WITHOUT LOCATION PERMISSIONS//
            }
        }
    }
    //LOCATION UPDATE//
    private void updateGPS(){
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED){
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, location -> {
                updateUIValues(location);
                currentLocation = location;
            });
        }
        else{
            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},PERMISSIONS_FINE_LOCATION);
            }
        }
    }
    //LOCATION UPDATE//
    //GET LOCATION PERMISSION REQUEST//

    
    //UPDATE VALUES IN UI//
    private void updateUIValues(Location location) {
        if (location == null) {
            // Handle null location
            return;
        }
        String
                lat = String.valueOf(location.getLatitude()),
                lon = String.valueOf(location.getLongitude()),
                accuracy = String.valueOf(location.getAccuracy());
        tv_lat.setText(lat);
        tv_lon.setText(lon);
        tv_accuracy.setText(accuracy);
        if(location.hasAltitude()){
            tv_altitude.setText(lat);
        }
        else{
            tv_altitude.setText("Unavailable");
        }
        if(location.hasSpeed()){
            tv_speed.setText(String.valueOf(location.getAltitude()));
        }
        else{
            tv_speed.setText("Unavailable");
        }

        Geocoder geocoder = new Geocoder(getApplicationContext());
        try{
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
            tv_address.setText(addresses.get(0).getAddressLine(0));
        }
        catch (Exception e){
            tv_address.setText("Unable to get street address");
        }

        class_history_list history = (class_history_list)getApplicationContext();
        savedLocations = history.getMyLocations();
        tv_MarkerCount.setText(Integer.toString(savedLocations.size()));
    }
    //UPDATE VALUES IN UI//
}