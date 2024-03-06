package com.example.finalsproject.dashboard_user_activities.customer;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.finalsproject.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class inquire_lost_luggage extends Fragment {
    View view;
    EditText description, quantity, flightDate;
    TextView SelectedAddress, tv;
    Button currentBtn, customBtn;
    Button proceedBtn;
    //LOCATIONS//
    public static final int DEFAULT_UPDATE_INTERVAL = 30;
    public static final int FAST_UPDATE_INTERVAL = 3;
    private static final int PERMISSIONS_FINE_LOCATION = 0;
    Location currentLocation;
    FusedLocationProviderClient fusedLocationProviderClient;
    LocationRequest locationRequest;
    LocationCallback locationCallBack;
    String coordinates;
    FirebaseFirestore db;
    FirebaseAuth fAuth;
    String uiD;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_inquire_lost_luggage, container, false);
        fAuth = FirebaseAuth.getInstance();
        uiD = Objects.requireNonNull(fAuth.getCurrentUser()).getUid();
        db = FirebaseFirestore.getInstance();
        tv = view.findViewById(R.id.tv);
        description = view.findViewById(R.id.luggage_details_editText);
        quantity = view.findViewById(R.id.luggage_quant_editText);
        flightDate = view.findViewById(R.id.luggage_flight_date_editText);
        SelectedAddress = view.findViewById(R.id.address_tv);
        locationRequest = new LocationRequest();
        locationRequest.setInterval(1000 * DEFAULT_UPDATE_INTERVAL);
        locationRequest.setFastestInterval(1000 * FAST_UPDATE_INTERVAL);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        locationCallBack = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                currentLocation = locationResult.getLastLocation();
            }
        };
        currentBtn = view.findViewById(R.id.current_address_btn);
        customBtn = view.findViewById(R.id.custom_address_btn);
        proceedBtn = view.findViewById(R.id.proceed_btn);
        currentBtn.setOnClickListener(v -> updateGPS());
        customBtn.setOnClickListener(v -> startActivity(new Intent(getActivity(), map_customer.class)));
        proceedBtn.setOnClickListener(v -> {
        });
        // Now you can use cords as needed
        return view;
    }



    //CURRENT LOCATION FUNCTIONS//

    //LOCATION PERMISSION REQUEST//
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_FINE_LOCATION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                updateGPS();
            }
            else{
                requireActivity().finish();//APP CAN'T WORK WITHOUT LOCATION PERMISSIONS//
            }
        }
    }
    //LOCATION UPDATE//
    private void updateGPS(){
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity());
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(requireActivity(), location -> {
                updateUIValues(location);
                currentLocation = location;
            });
        }
        else{
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_FINE_LOCATION);
        }
    }
    //LOCATION UPDATE//
    //GET LOCATION PERMISSION REQUEST//


    //UPDATE VALUES IN UI//
    @SuppressLint("SetTextI18n")
    private void updateUIValues(Location location) {
        if (location == null) {
            // Handle null location
            return;
        }
        Geocoder geocoder = new Geocoder(requireContext());
        try{
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
            assert addresses != null;
            coordinates = addresses.toString();
            SelectedAddress.setText(addresses.get(0).getAddressLine(0));
            DocumentReference docRef = db.collection("delivery_info").document(uiD);
            Address address = addresses.get(0);
            String addressLine = address.getAddressLine(0);
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            Map<String, Object> locationData = new HashMap<>();
            locationData.put("address", addressLine);
            locationData.put("latitude", latitude);
            locationData.put("longitude", longitude);
            locationData.put("coordinates", coordinates);
            docRef.set(locationData);
        }
        catch (Exception e){
            SelectedAddress.setText("Unable to get street address");
        }

    }
}