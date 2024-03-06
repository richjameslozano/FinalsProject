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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class delivery_inquiries extends Fragment {
    View view;
    ListView inq_lv;
    FirebaseAuth fAuth;
    FirebaseFirestore db,fStore;
    String uiD;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_delivery_inquiries, container, false);
        fStore = FirebaseFirestore.getInstance();
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
                        String name = document.getString("customer_name");
                        String add = document.getString("customer_address");
                        String contact = document.getString("customer_contact");
                        String status = document.getString("delivery_status");
                        String id = document.getId();
                        documentList.add("Customer Name: " + name + "\n\nCustomer Address: " + add + "\n\nCustomer contact: " + contact + "\n\nDelivery Status: " + status + "\n\nUser ID: " + id);
                        adapter.notifyDataSetChanged();
                        inq_lv.setOnItemClickListener((parent, view1, position, ID) -> {
                                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(requireContext());
                                    alertDialogBuilder.setTitle("Confirming delivery.");
                                    alertDialogBuilder.setMessage("Do you confirm that this is a lost luggage within our system's storage?");
                                    alertDialogBuilder.setPositiveButton("Yes", (dialog, which) -> {
                                        Toast.makeText(getActivity(),"Luggage is out for delivery.",Toast.LENGTH_SHORT).show();
                                        DocumentSnapshot selectedDocument = queryDocumentSnapshots.getDocuments().get(position);
                                        String customerId = selectedDocument.getString("customer_id");
                                        DocumentReference documentReference = fStore.collection("users").document(uiD);
                                        documentReference.addSnapshotListener(requireActivity(), (documentSnapshot, error) -> {
                                            if (documentSnapshot != null) {
                                                String endorserName = documentSnapshot.getString("l_name")+", "+documentSnapshot.getString("f_name");
                                                assert customerId != null;
                                                DocumentReference docRef = db.collection("delivery_info").document(customerId);
                                                docRef.addSnapshotListener(requireActivity(), (documentSnapshot1, error1) -> {
                                                        if (documentSnapshot1 != null) {
                                                            Map<String, Object> userData = new HashMap<>();
                                                            userData.put("endorser_name", endorserName);
                                                            userData.put("delivery_status","Out for Delivery");
                                                            docRef.update(userData);
                                                        }
                                                });
                                            }
                                        });
                                    });
                                    alertDialogBuilder.setNegativeButton("No",((dialog, which) -> {
                                        Toast.makeText(getActivity(),"Delivery dismissed.",Toast.LENGTH_SHORT).show();
                                        dialog.dismiss();
                                    }));
                                    alertDialogBuilder.show();
                                });
                    }
                })
                .addOnFailureListener(e -> {});
        return view;
    }
}