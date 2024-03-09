package com.example.finalsproject.dashboard_user_activities.employee;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.finalsproject.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class delivery_inquiries extends Fragment {
    View view;
    ListView inq_lv;
    FirebaseAuth fAuth;
    FirebaseFirestore db;
    String uiD;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_delivery_inquiries, container, false);
        db = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        uiD = Objects.requireNonNull(fAuth.getCurrentUser()).getUid();
        inq_lv = view.findViewById(R.id.inq_lv);
        ArrayList<String> documentList = new ArrayList<>();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_list_item_1, documentList);
        inq_lv.setAdapter(adapter);
        db.collection("delivery_info")
            .get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    //DISPLAY LIST//
                    String status = document.getString("delivery_status");
                    if(status!=null){
                        if(status.equals("Processing")||status.equals("Attempt Failed")){
                            displayData(document,documentList, adapter);
                            inq_lv.setOnItemClickListener((parent, view1, position, ID) -> onClickListView(queryDocumentSnapshots, position));
                        }
                        else{
                            adapter.notifyDataSetChanged();
                            documentList.clear();
                            documentList.add("No inquiries yet");
                        }
                    }
                    else{
                        adapter.notifyDataSetChanged();
                        documentList.clear();
                        documentList.add("No inquiries yet");
                    }
                }
            })
            .addOnFailureListener(e -> {
                adapter.notifyDataSetChanged();
                documentList.clear();
                documentList.add("No deliveries yet");
            });
        return view;
    }
    private static void displayData(QueryDocumentSnapshot document, ArrayList<String> documentList, ArrayAdapter<String> adapter) {
        String customerName = document.getString("customer_name");
        String customerContact = document.getString("customer_contact");
        String customerAddress = document.getString("customer_address");
        String deliveryStatus = document.getString("delivery_status");
        String luggageQuantity = document.getString("luggage_quantity");
        String airline  = document.getString("luggage_airline");
        String flightDate = document.getString("flight_date");
        String luggageDescription = document.getString("luggage_description");
        String id = document.getId();
        documentList.add("Customer Name: " + customerName +
                "\nCustomer Contact: " + customerContact +
                "\nCustomer Address: " + customerAddress +
                "\nDelivery Status: " + deliveryStatus +
                "\nLuggage Quantity: " + luggageQuantity +
                "\nAirline Name: " + airline +
                "\nFlight Date: " + flightDate +
                "\nLuggage Description: " + luggageDescription +
                "\nUser ID: " + id);
        adapter.notifyDataSetChanged();
    }
    private void onClickListView(QuerySnapshot queryDocumentSnapshots, int position) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(requireContext());
        alertDialogBuilder.setTitle("Confirming delivery.");
        alertDialogBuilder.setMessage("Do you confirm that this is a lost luggage within our system's storage?");
        alertDialogBuilder.setPositiveButton("Yes", (dialog, which) -> {
            getCustomerData(queryDocumentSnapshots, position);
        });
        alertDialogBuilder.setNegativeButton("No",((dialog, which) -> {
            Toast.makeText(getActivity(),"Delivery dismissed.",Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        }));
        alertDialogBuilder.show();
    }
    private void getCustomerData(QuerySnapshot queryDocumentSnapshots, int position) {
        Toast.makeText(getActivity(),"Luggage is out for delivery.",Toast.LENGTH_SHORT).show();
        DocumentSnapshot selectedDocument = queryDocumentSnapshots.getDocuments().get(position);
        String customerId = selectedDocument.getString("customer_id");
        DocumentReference documentReference = db.collection("users").document(uiD);
        documentReference.addSnapshotListener(requireActivity(), (documentSnapshot, error) -> {
            if (documentSnapshot != null) {
                String endorserName = documentSnapshot.getString("l_name")+", "+documentSnapshot.getString("f_name");
                assert customerId != null;
                getDeliveryData(customerId, endorserName);
            }
        });
    }
    private void getDeliveryData(String customerId, String endorserName) {
        DocumentReference docRef = db.collection("delivery_info").document(customerId);
        docRef.addSnapshotListener(requireActivity(), (documentSnapshot, error1) -> {
            if (documentSnapshot != null) {
                Map<String, Object> userData = new HashMap<>();
                userData.put("endorser_name", endorserName);
                userData.put("delivery_status","Out for Delivery");
                docRef.update(userData);
            }
        });
    }
}