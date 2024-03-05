package com.example.finalsproject.dashboard_user_activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.finalsproject.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class profile extends Fragment {
    View view;
    TextView acc_type,u_name,f_name,l_name,contact,email;
    ImageView pfp;
    Button changePfp_btn;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String uID;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //FIREBASE INSTANCES//
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        uID = Objects.requireNonNull(fAuth.getCurrentUser()).getUid();
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_profile, container, false);
        pfp = view.findViewById(R.id.pfp);
        changePfp_btn = view.findViewById(R.id.changepfp_btn);
        acc_type = view.findViewById(R.id.acc_type);
        u_name = view.findViewById(R.id.u_name);
        f_name = view.findViewById(R.id.f_name);
        l_name = view.findViewById(R.id.l_name);
        contact = view.findViewById(R.id.contact);
        email = view.findViewById(R.id.email);
        //PROFILE//
        DocumentReference documentReference = fStore.collection("users").document(uID);
        documentReference.addSnapshotListener(requireActivity(), (documentSnapshot, error) -> {
            if (documentSnapshot != null) {
                acc_type.setText(documentSnapshot.getString("acc_type"));
                u_name.setText(documentSnapshot.getString("u_name"));
                f_name.setText(documentSnapshot.getString("f_name"));
                l_name.setText(documentSnapshot.getString("l_name"));
                contact.setText(documentSnapshot.getString("contact"));
                email.setText(documentSnapshot.getString("email"));
            }
        });
        changePfp_btn.setOnClickListener(v -> {

        });
        return view;
    }
}