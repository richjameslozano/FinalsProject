package com.example.finalsproject.Aabasis;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.finalsproject.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

public class setAddressMap extends AppCompatActivity implements OnMapReadyCallback {
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    List<Location> savedLocations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_subcontractor);
        //obtain SupportFragmentManager
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.id_map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);
        savedLocations = history_list_class.getMyLocations();
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        //GET CURRENT LOCATION//
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(true);
        } else {
            // Request permission
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
        }
        //GET CURRENT LOCATION//
        Geocoder geocoder = new Geocoder(getApplicationContext());
        for (Location location : savedLocations) {

            LatLng mark = new LatLng(location.getLatitude(), location.getLongitude());
            List<Address> address;

            try {
                address = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            assert address != null;
            String addressLine = address.get(0).getAddressLine(0);
            googleMap.addMarker(new MarkerOptions()
                    .position(mark)
                    .title(addressLine));
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mark, 12.0f));
        }
    }
}