package com.example.finalsproject.dashboard_user_activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

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
                    customerLuggage();
                }
                else if(acc_type.equals("Admin")||acc_type.equals("Employee")){
                    db.collection("delivery_info")
                            .get()
                            .addOnSuccessListener(queryDocumentSnapshots -> {
                                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                    String name = document.getString("customer_name");
                                    String add = document.getString("customer_address");
                                    String contact = document.getString("customer_contact");
                                    String status = document.getString("delivery_status");
                                    String id = document.getId();
                                    documentList.add("Customer Name: " + name + "\n\nCustomer Address: " + add + "\n\nCustomer contact: " + contact + "\n\nDelivery Status: " + status + "\n\nUser ID: " + id);
                                    adapter.notifyDataSetChanged();
                                }
                            })
                            .addOnFailureListener(e -> {});
                }
            }
        });
        return view;
    }

    private void customerLuggage() {
        DocumentReference documentReference = db.collection("delivery_info").document(uiD);
        documentReference.addSnapshotListener(requireActivity(), (documentSnapshot, error) -> {
            if (documentSnapshot != null) {
                String customerName = documentSnapshot.getString("customer_name");
                String customerContact = documentSnapshot.getString("customer_contact");
                String customerAddress = documentSnapshot.getString("customer_address");
                String deliveryStatus = documentSnapshot.getString("delivery_status");
                String luggageQuantity = documentSnapshot.getString("luggage_quantity");
                String luggageDescription = documentSnapshot.getString("luggage_description");
                ArrayList<String> documentList = new ArrayList<>();
                ArrayAdapter<String> adapter = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_list_item_1, documentList);
                luggage_monitoring_lv.setAdapter(adapter);
                documentList.add("Delivery Status: " + deliveryStatus +
                        "\nCustomer Name: " + customerName +
                        "\nCustomer Contact: " + customerContact +
                        "\nCustomer Address: " + customerAddress +
                        "\nLuggage Quantity: " + luggageQuantity +
                        "\nLuggage Description: " + luggageDescription);
                adapter.notifyDataSetChanged();
            }
        });
    }
}