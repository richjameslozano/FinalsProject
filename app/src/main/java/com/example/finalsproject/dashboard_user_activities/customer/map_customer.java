package com.example.finalsproject.dashboard_user_activities.customer;

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
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.example.finalsproject.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class map_customer extends FragmentActivity implements OnMapReadyCallback {
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private Marker marker;
    FirebaseFirestore db;
    FirebaseAuth fAuth;
    String uiD,coordinates;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_customer);
        fAuth = FirebaseAuth.getInstance();
        uiD = Objects.requireNonNull(fAuth.getCurrentUser()).getUid();
        db = FirebaseFirestore.getInstance();
        // Initialize Firestore

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map1);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        // Request permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(true);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
        }

        // Zoom in on current location
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        assert locationManager != null;
        Location currentLocation = locationManager.getLastKnownLocation(Objects.requireNonNull(locationManager.getBestProvider(criteria, false)));
        if (currentLocation != null) {
            LatLng myLocation = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 15));
        }

        // Add marker on map click
        googleMap.setOnMapClickListener(latLng -> {
            Geocoder geocoder = new Geocoder(getApplicationContext());
            try {
                // Get address from location
                List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                assert addresses != null;
                String addressLine = addresses.get(0).getAddressLine(0);
                coordinates = addresses.toString();
                // Remove previous marker
                if (marker != null) {
                    marker.remove();
                }
                // Add new marker
                marker = googleMap.addMarker(new MarkerOptions().position(latLng).title(addressLine));
                // Store addressLine in Firebase Firestore
                if (marker != null) {
                    DocumentReference docRef = db.collection("delivery_info").document(uiD);
                    docRef.set(new MarkerData(addressLine, latLng.latitude, latLng.longitude, coordinates));
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        // Handle marker click
        googleMap.setOnMarkerClickListener(marker -> {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setMessage("Do you want to remove this marker?");
            alertDialogBuilder.setPositiveButton("Yes", (dialog, which) -> marker.remove());
            alertDialogBuilder.setNegativeButton("No", (dialog, which) -> {
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 15));
                marker.showInfoWindow();
            });
            alertDialogBuilder.create().show();
            return true; // Return true to indicate that the click event has been consumed
        });
    }

    private static class MarkerData {
        public String address;
        public double latitude;
        public double longitude;
        public String coordinates;

        public MarkerData(String address, double latitude, double longitude, String coordinates) {
            this.address = address;
            this.latitude = latitude;
            this.longitude = longitude;
            this.coordinates = coordinates;
        }
    }
}