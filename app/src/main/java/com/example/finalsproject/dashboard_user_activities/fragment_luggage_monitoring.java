package com.example.finalsproject.dashboard_user_activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.finalsproject.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Objects;

public class fragment_luggage_monitoring extends Fragment {
    View view;
    ListView luggage_monitoring_lv;
    FirebaseAuth fAuth;
    FirebaseFirestore db;
    String uiD;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_luggage_monitoring, container, false);
        luggage_monitoring_lv = view.findViewById(R.id.luggage_monitoring_lv);

        db = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        uiD = Objects.requireNonNull(fAuth.getCurrentUser()).getUid();

        luggage_monitoring_lv = view.findViewById(R.id.luggage_monitoring_lv);
        ArrayList<String> documentList = new ArrayList<>();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_list_item_1, documentList);
        luggage_monitoring_lv.setAdapter(adapter);

        DocumentReference documentReference = db.collection("users").document(uiD);
        documentReference.addSnapshotListener(requireActivity(), (documentSnapshot, error) -> {
            if (documentSnapshot != null) {
                //ISOLATE CUSTOMER FROM ADMIN AND EMPLOYEE//
                String acc_type = documentSnapshot.getString("acc_type");
                assert acc_type != null;
                if(acc_type.equals("Customer")){
                    customerList(documentList, adapter);
                }
                else if(acc_type.equals("Admin")||acc_type.equals("Employee")){
                    db.collection("delivery_info")
                            .get()
                            .addOnSuccessListener(queryDocumentSnapshots -> {
                                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                    nonCustomerList(document,documentList,adapter);
                                }
                            });
                }
            }
        });
        return view;
    }
    //DISPLAY ADMIN AND EMPLOYEE HISTORY//
    private void nonCustomerList(QueryDocumentSnapshot document, ArrayList<String> documentList, ArrayAdapter<String> adapter) {
            String customerName = document.getString("customer_name");
            String customerContact = document.getString("customer_contact");
            String customerAddress = document.getString("customer_address");
            String deliveryStatus = document.getString("delivery_status");
            String luggageQuantity = document.getString("luggage_quantity");
            String airlines  = document.getString("luggage_airline");
            String flightDate = document.getString("flight_date");
            String luggageDescription = document.getString("luggage_description");
            String endorserName = document.getString("endorser_name");
            String subcontractorName = document.getString("subcontractor_name");
            luggage_monitoring_lv.setAdapter(adapter);
            if (deliveryStatus == null && customerName == null && customerContact == null && customerAddress == null &&
                    luggageDescription == null && luggageQuantity == null && flightDate == null && endorserName == null &&
                    subcontractorName == null) {
                documentList.add("No inquiries yet");
            }
            else {
                documentList.add(
                (deliveryStatus != null ? "Delivery Status: " + deliveryStatus : "") +
                (customerName != null ? "\nCustomer Name: " + customerName : "") +
                (customerContact != null ? "\nCustomer Contact: " + customerContact : "") +
                (customerAddress != null ? "\nCustomer Address: " + customerAddress : "") +
                (luggageDescription != null ? "\nLuggage Description: " + luggageDescription : "") +
                (luggageQuantity != null ? "\nLuggage Quantity: " + luggageQuantity : "") +
                (airlines != null ? "\nAirline Name: " + airlines : "") +
                (flightDate != null ? "\nFlight Date: " + flightDate : "") +
                (endorserName != null ? "\nEndorser Name: " + endorserName : "") +
                (subcontractorName != null ? "\nSubcontractor Name: " + subcontractorName : "")
                );
            }
            adapter.notifyDataSetChanged();
    }
    //DISPLAY ISOLATED CUSTOMER HISTORY//
    private void customerList(ArrayList<String> documentList, ArrayAdapter<String> adapter) {
        DocumentReference documentReference = db.collection("delivery_info").document(uiD);
        documentReference.addSnapshotListener(requireActivity(), (documentSnapshot, error) -> {
            if (documentSnapshot != null) {
                String customerName = documentSnapshot.getString("customer_name");
                String customerContact = documentSnapshot.getString("customer_contact");
                String customerAddress = documentSnapshot.getString("customer_address");
                String deliveryStatus = documentSnapshot.getString("delivery_status");
                String luggageQuantity = documentSnapshot.getString("luggage_quantity");
                String flightDate = documentSnapshot.getString("flight_date");
                String luggageDescription = documentSnapshot.getString("luggage_description");
                String endorserName = documentSnapshot.getString("endorser_name");
                String subcontractorName = documentSnapshot.getString("subcontractor_name");
                luggage_monitoring_lv.setAdapter(adapter);
                documentList.clear();
                // Check if all values are null
                if (deliveryStatus == null && customerName == null && customerContact == null && customerAddress == null &&
                        luggageDescription == null && luggageQuantity == null && flightDate == null && endorserName == null &&
                        subcontractorName == null) {
                    documentList.add("No inquiries yet");
                }
                else {
                    documentList.add(
                    (deliveryStatus != null ? "Delivery Status: " + deliveryStatus : "") +
                    (customerName != null ? "\nCustomer Name: " + customerName : "") +
                    (customerContact != null ? "\nCustomer Contact: " + customerContact : "") +
                    (customerAddress != null ? "\nCustomer Address: " + customerAddress : "") +
                    (luggageDescription != null ? "\nLuggage Description: " + luggageDescription : "") +
                    (luggageQuantity != null ? "\nLuggage Quantity: " + luggageQuantity : "") +
                    (flightDate != null ? "\nFlight Date: " + flightDate : "") +
                    (endorserName != null ? "\nEndorser Name: " + endorserName : "") +
                    (subcontractorName != null ? "\nSubcontractor Name: " + subcontractorName : "")
                    );
                    luggage_monitoring_lv.setOnItemClickListener((parent, view1, position, ID) -> Toast.makeText(getActivity(), "Delivery Status: " + deliveryStatus, Toast.LENGTH_SHORT).show());
                }
                adapter.notifyDataSetChanged();
            }
        });
    }
}