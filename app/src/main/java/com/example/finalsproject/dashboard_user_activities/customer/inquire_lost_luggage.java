package com.example.finalsproject.dashboard_user_activities.customer;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class inquire_lost_luggage extends Fragment {
    View view;
    EditText description, quantity, flightDate;
    TextView SelectedAddress;
    Button currentBtn, customBtn, proceedBtn,refreshBtn;
    //LOCATIONS//
    public static final int DEFAULT_UPDATE_INTERVAL = 30;
    public static final int FAST_UPDATE_INTERVAL = 3;
    private static final int PERMISSIONS_FINE_LOCATION = 0;
    Location currentLocation;
    FusedLocationProviderClient fusedLocationProviderClient;
    LocationRequest locationRequest;
    LocationCallback locationCallBack;
    String coordinates;
    FirebaseFirestore db,fStore;
    FirebaseAuth fAuth;
    String uiD;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_inquire_lost_luggage, container, false);
        fAuth = FirebaseAuth.getInstance();
        uiD = Objects.requireNonNull(fAuth.getCurrentUser()).getUid();
        db = FirebaseFirestore.getInstance();
        fStore = FirebaseFirestore.getInstance();
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
        currentBtn.setOnClickListener(v -> {
            updateGPS();
            getAddress();
        });
        customBtn.setOnClickListener(v -> startActivity(new Intent(getActivity(), map_customer.class)));
        refreshBtn = view.findViewById(R.id.refreshBtn);
        getAddress();
        refreshBtn.setOnClickListener(v-> getAddress());
        proceedBtn.setOnClickListener(v -> {
            String desc = description.getText().toString().trim(),
                   Quantity = quantity.getText().toString(),
                   flight = flightDate.getText().toString();
            if(desc.isEmpty()){
                description.setError("This field is required");
            }
            else if(Quantity.isEmpty()){
                quantity.setError("This field is required");
            }
            else if(flight.isEmpty()){
                flightDate.setError("This field is required");
            }
            else{
                Toast.makeText(getActivity(), "Your luggage information is being processed.", Toast.LENGTH_SHORT).show();
                DocumentReference docRef = db.collection("delivery_info").document(uiD);
                DocumentReference documentReference = fStore.collection("users").document(uiD);
                documentReference.addSnapshotListener(requireActivity(), (documentSnapshot, error) -> {
                    if (documentSnapshot != null) {
                        Map<String, Object> userData = new HashMap<>();
                        userData.put("customer_name", documentSnapshot.getString("l_name") + ", " + documentSnapshot.getString("f_name"));
                        userData.put("customer_contact", documentSnapshot.getString("contact"));
                        userData.put("luggage_description", desc);
                        userData.put("luggage_quantity", Quantity);
                        userData.put("flight_date", flight);
                        userData.put("delivery_status","Processing");
                        userData.put("customer_id",uiD);
                        docRef.update(userData);
                        getAddress();
                    }
                });

            }
        });
        DocumentReference docRef = db.collection("delivery_info").document(uiD);
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (!document.exists()) {
                    Map<String, Object> data = new HashMap<>();
                    docRef.set(data);
                }
            } else {
                Log.d(TAG, "Failed with: ", task.getException());
            }
        });

        return view;
    }

    private void getAddress() {
        DocumentReference documentReference = db.collection("delivery_info").document(uiD);
        documentReference.addSnapshotListener(requireActivity(), (documentSnapshot, error) -> {
            if (documentSnapshot != null) {
                SelectedAddress.setText(documentSnapshot.getString("customer_address"));
            }
        });
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
    //CURRENT LOCATION UPDATE//
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
            DocumentReference docRef = db.collection("delivery_info").document(uiD);
            Map<String, Object> locationData = getStringObjectMap(location, addresses);
            docRef.update(locationData);
        }
        catch (Exception ignored){
        }
    }

    @NonNull
    private Map<String, Object> getStringObjectMap(Location location, List<Address> addresses) {
        Address address = addresses.get(0);
        String addressLine = address.getAddressLine(0);
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        Map<String, Object> locationData = new HashMap<>();
        locationData.put("customer_address", addressLine);
        locationData.put("latitude", latitude);
        locationData.put("longitude", longitude);
        locationData.put("customer_coordinates", coordinates);
        return locationData;
    }
}