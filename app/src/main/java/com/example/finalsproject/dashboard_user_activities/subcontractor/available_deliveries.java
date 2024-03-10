package com.example.finalsproject.dashboard_user_activities.subcontractor;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
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
    SearchView sv_ad;
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
        sv_ad = view.findViewById(R.id.sv_ad);
        ArrayList<String> documentList = new ArrayList<>();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireActivity(),R.layout.list,R.id.list_tv, documentList);
        available_deliveries_lv.setAdapter(adapter);
        db.collection("delivery_info")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String name = document.getString("customer_name");
                        String add = document.getString("customer_address");
                        String contact = document.getString("customer_contact");
                        String airline = document.getString("luggage_airline");
                        String status = document.getString("delivery_status");
                        String quantity = document.getString("luggage_quantity");
                        String sub_name = document.getString("subcontractor_name");
                        String end_name = document.getString("endorser_name");
                        if (status != null) {
                            DocumentReference documentReference = db.collection("users").document(uiD);
                            documentReference.addSnapshotListener(requireActivity(), (documentSnapshot, error) -> {
                                if (documentSnapshot != null) {
                                    String subcontractor_name = documentSnapshot.getString("l_name") + ", " + documentSnapshot.getString("f_name");
                                    String format = "Customer Name: " + name +
                                            "\nCustomer Address: " + add +
                                            "\nCustomer Contact: " + contact +
                                            "\nAirline Name: " + airline +
                                            "\nDelivery Status: " + status +
                                            "\nLuggage Quantity: " + quantity +
                                            "\nEndorser Name: " + end_name;
                                    if (sub_name != null) {
                                        if (status.equals("Delivery in Progress") && sub_name.equals(subcontractor_name)) {
                                            documentList.add(format);
                                            adapter.notifyDataSetChanged();
                                            available_deliveries_lv.setOnItemClickListener((parent, view, position, ID) -> {
                                                DocumentSnapshot selectedDocument = queryDocumentSnapshots.getDocuments().get(position);
                                                InDelivery(selectedDocument);
                                            });
                                        }
                                        else{
                                            adapter.notifyDataSetChanged();
                                            documentList.clear();
                                            documentList.add("No deliveries yet");
                                        }
                                    }
                                    else {
                                        if (status.equals("Out for Delivery") || status.equals("Attempt Failed")) {
                                            documentList.add(format);
                                            adapter.notifyDataSetChanged();
                                            available_deliveries_lv.setOnItemClickListener((parent, view, position, ID) -> {
                                                DocumentSnapshot selectedDocument = queryDocumentSnapshots.getDocuments().get(position);
                                                Delivery(selectedDocument);
                                            });
                                        }
                                        else{
                                            adapter.notifyDataSetChanged();
                                            documentList.clear();
                                            documentList.add("No deliveries yet");
                                        }
                                    }
                                }
                            });
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    adapter.notifyDataSetChanged();
                    documentList.clear();
                    documentList.add("No deliveries yet");
                });
        sv_ad.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });
        return view;
    }

    private void Delivery(DocumentSnapshot selectedDocument) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(requireContext());
        alertDialogBuilder.setTitle("Confirming delivery.");
        alertDialogBuilder.setMessage("Are you willing to deliver this lost luggage?");
        alertDialogBuilder.setNegativeButton("No", ((dialog, which) -> {
            Toast.makeText(getActivity(), "Delivery dismissed.", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        }));
        alertDialogBuilder.setPositiveButton("Yes", (dialog, which) -> {
            Toast.makeText(getActivity(), "Luggage has been picked up by subcontractor.", Toast.LENGTH_SHORT).show();
            String customerId = selectedDocument.getString("customer_id");
            DocumentReference documentReference = db.collection("users").document(uiD);
            documentReference.addSnapshotListener(requireActivity(), (documentSnapshot, error) -> {
                if (documentSnapshot != null) {
                    String subcontractor_name = documentSnapshot.getString("l_name") + ", " + documentSnapshot.getString("f_name");
                    assert customerId != null;
                    DocumentReference docRef = db.collection("delivery_info").document(customerId);
                    docRef.addSnapshotListener(requireActivity(), (documentSnapshot1, error1) -> {
                        if (documentSnapshot1 != null) {
                            Map<String, Object> userData = new HashMap<>();
                            userData.put("subcontractor_name", subcontractor_name);
                            userData.put("delivery_status", "Delivery in Progress");
                            docRef.update(userData);
                            Intent intent = new Intent(getActivity(), map_subcontractor.class);
                            intent.putExtra("customerId", customerId);
                            intent.putExtra("address", selectedDocument.getString("customer_address"));
                            startActivity(intent);
                        }
                    });
                }
            });
            dialog.dismiss();
        });
        alertDialogBuilder.show();
    }

    private void InDelivery(DocumentSnapshot selectedDocument) {
        String customerId = selectedDocument.getString("customer_id");
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(requireContext());
        alertDialogBuilder.setTitle("Delivery is in progress.");
        alertDialogBuilder.setMessage("Have you delivered the luggage?");
        alertDialogBuilder.setNeutralButton("Not yet", ((dialog, which) -> {
            AlertDialog.Builder alertDialogBuilder1 = new AlertDialog.Builder(requireContext());
            alertDialogBuilder1.setTitle("Opening application maps?");
            alertDialogBuilder1.setMessage("Do you want to review the delivery location point?");
            alertDialogBuilder1.setNegativeButton("No", ((dialog1, which1) -> dialog1.dismiss()));
            alertDialogBuilder1.setPositiveButton("Yes", ((dialog1, which1) -> {
                Intent intent = new Intent(getActivity(), map_subcontractor.class);
                intent.putExtra("customerId", customerId);
                intent.putExtra("address", selectedDocument.getString("customer_address"));
                startActivity(intent);
            }));
            alertDialogBuilder1.show();
        }));
        alertDialogBuilder.setNegativeButton("Attempt failed", ((dialog, which) -> {
            assert customerId!=null;
            DocumentReference docRef = db.collection("delivery_info").document(customerId);
            docRef.addSnapshotListener(requireActivity(), (documentSnapshot1, error1) -> {
                if (documentSnapshot1 != null) {
                    Map<String, Object> userData = new HashMap<>();
                    userData.put("delivery_status", "Attempt Failed");
                    userData.put("subcontractor_name", null);
                    docRef.update(userData);
                }
            });
        }));
        alertDialogBuilder.setPositiveButton("Yes", ((dialog, which) -> {
            Toast.makeText(getActivity(), "Delivery successful.", Toast.LENGTH_SHORT).show();
            DocumentReference documentReference = db.collection("users").document(uiD);
            documentReference.addSnapshotListener(requireActivity(), (documentSnapshot, error) -> {
                if (documentSnapshot != null) {
                    assert customerId!=null;
                    DocumentReference docRef = db.collection("delivery_info").document(customerId);
                    docRef.addSnapshotListener(requireActivity(), (documentSnapshot1, error1) -> {
                        if (documentSnapshot1 != null) {
                            Map<String, Object> userData = new HashMap<>();
                            userData.put("delivery_status", "Luggage Delivered");
                            docRef.update(userData);
                        }
                    });
                }
            });
            dialog.dismiss();
        }));
        alertDialogBuilder.show();
    }
}