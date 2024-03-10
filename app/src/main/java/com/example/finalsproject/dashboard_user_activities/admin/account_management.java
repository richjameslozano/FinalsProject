package com.example.finalsproject.dashboard_user_activities.admin;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.finalsproject.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class account_management extends Fragment {
    View view;
    SearchView sv_am;
    private ArrayAdapter<String> adapter;
    private List<String> documentEmails;
    private List<DocumentSnapshot> documentSnapshots;
    private FirebaseFirestore fStore;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        view = inflater.inflate(R.layout.fragment_account_management, container, false);
        ListView listView = view.findViewById(R.id.usersListView);
        sv_am = view.findViewById(R.id.sv_am);
        documentEmails = new ArrayList<>();
        documentSnapshots = new ArrayList<>();
        adapter = new ArrayAdapter<>(requireActivity(), R.layout.list,R.id.list_tv, documentEmails);
        listView.setAdapter(adapter);
        fStore = FirebaseFirestore.getInstance();
        fStore.collection("users")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot document : task.getResult()) {
                            // Retrieve document email field
                            String email = document.getString("email");
                            // Add document email to the list
                            documentEmails.add(email);
                            documentSnapshots.add(document);
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(getActivity(), "Error getting documents: " + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Create the AlertDialog
                AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
                builder.setTitle("Delete Account");
                builder.setMessage("Are you sure you want to delete the account?");
                // Set up the buttons
                builder.setPositiveButton("Yes", (dialog, which) -> {
                    // Handle account deletion here
                    deleteAccount(position);
                });
                builder.setNegativeButton("No", (dialog, which) -> {
                    dialog.cancel(); // Dismiss the dialog
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }

            private void deleteAccount(int position) {
                DocumentSnapshot documentSnapshot = documentSnapshots.get(position);
                fStore.collection("users").document(documentSnapshot.getId())
                        .delete()
                        .addOnSuccessListener(aVoid -> {
                            // Successfully deleted the document
                            Toast.makeText(getActivity(), "Account deleted successfully", Toast.LENGTH_SHORT).show();
                            // Update the lists and notify the adapter
                            documentEmails.remove(position);
                            documentSnapshots.remove(position);
                            adapter.notifyDataSetChanged();
                        })
                        .addOnFailureListener(e -> {
                            // Error occurred
                            Toast.makeText(getActivity(), "Error deleting account", Toast.LENGTH_SHORT).show();
                });
            }
        });
        sv_am.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
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

}

