package com.example.finalsproject.Aabasis;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.finalsproject.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class setAddressMap extends AppCompatActivity implements OnMapReadyCallback {
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    List<Location> savedLocations;
    private Marker marker;
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
        //ZOOM IN ON CURRENT LOCATION//
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        assert locationManager != null;
        Location currentlocation = locationManager.getLastKnownLocation(Objects.requireNonNull(locationManager.getBestProvider(criteria, false)));
        if (currentlocation != null) {
            LatLng myLocation = new LatLng(currentlocation.getLatitude(), currentlocation.getLongitude());
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 15));
        }
        //MARK CURRENT LOCATION//
        for (Location location : savedLocations) {
            Geocoder geocoder = new Geocoder(getApplicationContext());
            LatLng mark = new LatLng(location.getLatitude(), location.getLongitude());
            List<Address> address;
            try {
                //CURRENT LOCATION VALUE//
                address = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
            assert address != null;
            String addressLine = address.get(0).getAddressLine(0);
            googleMap.addMarker(new MarkerOptions()
                    .position(mark)
                    .title(addressLine));
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mark, 12.0f));
        }
        //CUSTOM LOCATION//
        googleMap.setOnMapClickListener(latLng -> {
            if (marker != null) {
                marker.remove();
                marker = null;
            }
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            try {
                //CUSTOM LOCATION VALUE//
                List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                String addressLine = addresses.get(0).getAddressLine(0);
                marker = googleMap.addMarker(new MarkerOptions().position(latLng).title(addressLine));
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
            if (marker != null) {
                googleMap.setOnMarkerClickListener(marker -> {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                    alertDialogBuilder.setMessage("Are you sure you want to remove the marker?");
                    alertDialogBuilder.setPositiveButton("Yes", (dialog, which) -> marker.remove());
                    alertDialogBuilder.setNegativeButton("No", (dialog, which) -> {
                        googleMap.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
                        marker.showInfoWindow();
                        dialog.dismiss();
                    });
                    alertDialogBuilder.create().show();
                    return true; // Return true to indicate that the click event has been consumed
                });
            }
        });
    }
}