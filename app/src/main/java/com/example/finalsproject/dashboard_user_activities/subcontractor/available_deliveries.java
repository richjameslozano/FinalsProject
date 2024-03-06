package com.example.finalsproject.dashboard_user_activities.subcontractor;

import android.content.Intent;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class available_deliveries extends Fragment {
View view;
ListView available_deliveries_lv;
FirebaseAuth fAuth;
FirebaseFirestore db;
String uiD;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_available_deliveries, container, false);
        db = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        uiD = Objects.requireNonNull(fAuth.getCurrentUser()).getUid();
        available_deliveries_lv = view.findViewById(R.id.available_deliveries_lv);

        ArrayList<String> documentList = new ArrayList<>();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_list_item_1, documentList);
        available_deliveries_lv.setAdapter(adapter);

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
                        available_deliveries_lv.setOnItemClickListener((parent, view1, position, ID) -> {
                            DocumentSnapshot selectedDocument = queryDocumentSnapshots.getDocuments().get(position);
                            String stats = selectedDocument.getString("delivery_status");
                            assert stats != null;
                            if(!stats.equals("Luggage delivered")){
                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(requireContext());
                                alertDialogBuilder.setTitle("Confirming delivery.");
                                alertDialogBuilder.setMessage("Are you willing to deliver this lost luggage?");
                                alertDialogBuilder.setPositiveButton("Yes", (dialog, which) -> {
                                    Toast.makeText(getActivity(),"Luggage has been picked up by subcontractor.",Toast.LENGTH_SHORT).show();
                                    String customerId = selectedDocument.getString("customer_id");
                                    DocumentReference documentReference = db.collection("users").document(uiD);
                                    documentReference.addSnapshotListener(requireActivity(), (documentSnapshot, error) -> {
                                        if (documentSnapshot != null) {
                                            String subcontractor_name = documentSnapshot.getString("l_name")+", "+documentSnapshot.getString("f_name");
                                            assert customerId != null;
                                            DocumentReference docRef = db.collection("delivery_info").document(customerId);
                                            docRef.addSnapshotListener(requireActivity(), (documentSnapshot1, error1) -> {
                                                if (documentSnapshot1 != null) {
                                                    Map<String, Object> userData = new HashMap<>();
                                                    userData.put("subcontractor_name", subcontractor_name);
                                                    userData.put("delivery_status","Delivery in progress");
                                                    docRef.update(userData);
                                                    Intent intent = new Intent(getActivity(), map_subcontractor.class);
                                                    intent.putExtra("customerId", customerId);
                                                    intent.putExtra("address",selectedDocument.getString("customer_address"));
                                                    startActivity(intent);
                                                }
                                            });
                                        }
                                    });
                                    dialog.dismiss();
                                });
                                alertDialogBuilder.setNeutralButton("Luggage has been delivered",((dialog, which) -> {
                                    Toast.makeText(getActivity(),"Delivery successful.",Toast.LENGTH_SHORT).show();
                                    String customerId = selectedDocument.getString("customer_id");
                                    DocumentReference documentReference = db.collection("users").document(uiD);
                                    documentReference.addSnapshotListener(requireActivity(), (documentSnapshot, error) -> {
                                        if (documentSnapshot != null) {
                                            assert customerId != null;
                                            DocumentReference docRef = db.collection("delivery_info").document(customerId);
                                            docRef.addSnapshotListener(requireActivity(), (documentSnapshot1, error1) -> {
                                                if (documentSnapshot1 != null) {
                                                    Map<String, Object> userData = new HashMap<>();
                                                    userData.put("delivery_status","Luggage delivered");
                                                    docRef.update(userData);
                                                }
                                            });
                                        }
                                    });
                                    dialog.dismiss();
                                }));
                                alertDialogBuilder.setNegativeButton("No",((dialog, which) -> {
                                    Toast.makeText(getActivity(),"Delivery dismissed.",Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                }));
                                alertDialogBuilder.show();
                            }
                            else{
                                Toast.makeText(getActivity(),"Luggage is already delivered.",Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                })
                .addOnFailureListener(e -> {});

        return view;
    }
}