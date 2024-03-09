package com.example.finalsproject.dashboard_user_activities.customer;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
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
import com.example.finalsproject.dashboard_user_activities.dashboard;
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
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class inquire_lost_luggage extends Fragment {
    View view;
    EditText description, quantity, airline;
    TextView SelectedAddress;
    Button flightDate, currentBtn, customBtn, proceedBtn,refreshBtn;
    //LOCATIONS//
    public static final int DEFAULT_UPDATE_INTERVAL = 30;
    public static final int FAST_UPDATE_INTERVAL = 3;
    private static final int PERMISSIONS_FINE_LOCATION = 0;
    Location currentLocation;
    FusedLocationProviderClient fusedLocationProviderClient;
    LocationRequest locationRequest;
    LocationCallback locationCallBack;
    FirebaseFirestore db;
    FirebaseAuth fAuth;
    String uiD;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_inquire_lost_luggage, container, false);
        //GET CURRENT LOCATION//
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
        //GET CURRENT LOCATION//
        fAuth = FirebaseAuth.getInstance();
        uiD = Objects.requireNonNull(fAuth.getCurrentUser()).getUid();
        db = FirebaseFirestore.getInstance();
        getAddress();
        description = view.findViewById(R.id.luggage_details_editText);
        quantity = view.findViewById(R.id.luggage_quant_editText);
        airline = view.findViewById(R.id.airline_editText);
        flightDate = view.findViewById(R.id.luggage_flight_dateBtn);
        SelectedAddress = view.findViewById(R.id.address_tv);
        currentBtn = view.findViewById(R.id.current_address_btn);
        customBtn = view.findViewById(R.id.custom_address_btn);
        proceedBtn = view.findViewById(R.id.proceed_btn);
        refreshBtn = view.findViewById(R.id.refreshBtn);
        flightDate.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext());
            // Set the maximum date to today's date
            datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
            datePickerDialog.getDatePicker().setMinDate(2016);
            // Show the DatePickerDialog
            datePickerDialog.setOnDateSetListener((view, year, monthOfYear, dayOfMonth) -> {
                // Set the selected date to the flightDate TextView
                flightDate.setText(String.format(Locale.getDefault(), "%02d/%02d/%d", dayOfMonth, monthOfYear + 1, year));
            });
            datePickerDialog.show();
        });
        currentBtn.setOnClickListener(v -> {
            updateGPS();
            getAddress();
        });
        customBtn.setOnClickListener(v -> startActivity(new Intent(getActivity(), map_customer.class)));
        refreshBtn.setOnClickListener(v-> getAddress());
        proceedBtn.setOnClickListener(v -> {
            String desc = description.getText().toString().trim(),
                   Quantity = quantity.getText().toString(),
                   flight = flightDate.getText().toString(),
                   airlines = airline.getText().toString();
            if(desc.isEmpty()){
                description.setError("Describe your lost luggage.");
            }
            else if(Quantity.isEmpty()){
                quantity.setError("How many luggage did you lose?");
            }
            else if(Integer.parseInt(Quantity)<=0){
                quantity.setError("Invalid amount of luggage?");
            }
            else if(airlines.isEmpty()){
                airline.setError("Enter the airlines that the luggage was checked in.");
            }
            else if(flight.equals("Flight Date: DD/MM/YY")){
                Toast.makeText(getActivity(), "Please select the flight date of when you lost your luggage.", Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(getActivity(), "Your luggage information is being processed.", Toast.LENGTH_SHORT).show();
                DocumentReference docRef = db.collection("delivery_info").document(uiD);
                DocumentReference documentReference = db.collection("users").document(uiD);
                documentReference.addSnapshotListener(requireActivity(), (documentSnapshot, error) -> {
                    if (documentSnapshot != null) {
                        Map<String, Object> userData = new HashMap<>();
                        userData.put("customer_name", documentSnapshot.getString("l_name") + ", " + documentSnapshot.getString("f_name"));
                        userData.put("customer_contact", documentSnapshot.getString("contact"));
                        userData.put("luggage_description", desc);
                        userData.put("luggage_quantity", Quantity);
                        userData.put("luggage_airline",airlines);
                        userData.put("flight_date", flight);
                        userData.put("delivery_status","Processing");
                        userData.put("customer_id",uiD);
                        docRef.update(userData);
                        getAddress();
                    }
                });
                startActivity(new Intent(getActivity(),dashboard.class));
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
        newInquiry();
        return view;
    }

    private void newInquiry() {
        DocumentReference documentReference = db.collection("delivery_info").document(uiD);
        documentReference.addSnapshotListener(requireActivity(), (documentSnapshot, error) -> {
            assert documentSnapshot != null;
            String status = documentSnapshot.getString("delivery_status");
                if(status!=null){
                    if(status.equals("Luggage delivered")){
                        Map<String, Object> updates = new HashMap<>();
                        updates.put("customer_address", null);
                        updates.put("customer_contact", null);
                        updates.put("customer_id", null);
                        updates.put("customer_name", null);
                        updates.put("delivery_status", null);
                        updates.put("flight_date", null);
                        updates.put("latitude", null);
                        updates.put("longitude", null);
                        updates.put("luggage_description", null);
                        updates.put("luggage_quantity", null);
                        updates.put("luggage_airline",null);
                        updates.put("endorser_name", null);
                        updates.put("subcontractor_name", null);
                        documentReference.update(updates);
                    }
                }
        });
    }

    @SuppressLint("SetTextI18n")
    private void getAddress() {
        DocumentReference documentReference = db.collection("delivery_info").document(uiD);
        documentReference.addSnapshotListener(requireActivity(), (documentSnapshot, error) -> {
            if (documentSnapshot != null) {
                SelectedAddress.setText(documentSnapshot.getString("customer_address"));
            }
            else{
                SelectedAddress.setText("No address has been registered yet.");
            }
        });
    }

    //LOCATION PERMISSION REQUEST CONDITION//
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
        Geocoder geocoder = new Geocoder(requireContext());
        try{
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
            assert addresses != null;
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
        return locationData;
    }
}