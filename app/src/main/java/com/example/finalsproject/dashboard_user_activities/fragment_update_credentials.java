package com.example.finalsproject.dashboard_user_activities;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.finalsproject.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;


public class fragment_update_credentials extends Fragment {

View view;
EditText edit_uname,edit_lname,edit_fname,edit_email,edit_contact;

Button save_btn;
FirebaseAuth fAuth;
FirebaseFirestore fStore;
String uID;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        uID = Objects.requireNonNull(fAuth.getCurrentUser()).getUid();
        view = inflater.inflate(R.layout.fragment_update_credentials,container,false);
        edit_uname = view.findViewById(R.id.edit_uname);
        edit_lname = view.findViewById(R.id.edit_lname);
        edit_fname = view.findViewById(R.id.edit_fname);
        edit_email = view.findViewById(R.id.edit_email);
        edit_contact = view.findViewById(R.id.edit_contact);
        save_btn = view.findViewById(R.id.save_btn);

        save_btn.setOnClickListener(v -> {
            setAccountSettingsFragment(new fragment_account_settings());
            DocumentReference documentReference = fStore.collection("users").document(uID);
            String
                username =  edit_uname.getText().toString().trim(),
                lastname =  edit_lname.getText().toString().trim(),
                firstname =  edit_fname.getText().toString().trim(),
                email =  edit_email.getText().toString().trim(),
                contact =  edit_contact.getText().toString().trim();

            if(username.length()<5) {
                edit_uname.setError("Username should be more than at least 5 characters.");

            }
                else if(!username.isEmpty()){
                documentReference.update("u_name",username);
            }

            if (!lastname.matches("[a-zA-Z ]+")) {
                edit_lname.setError("Last Name should contain letters only.");

                //no numbers
            }
           else if (!lastname.isEmpty()){
                documentReference.update("l_name",lastname);

            }
            if(!firstname.matches("[a-zA-Z ]+")) {
                edit_fname.setError("First Name should contain letters only.");

                //no numbers
            }
            else if(!firstname.isEmpty()){
                documentReference.update("f_name",firstname);

            }
            if(!email.isEmpty()){
                documentReference.update("email",email);
            }
            if (contact.length()<=10||!contact.startsWith("09")) {
                edit_contact.setError("Invalid contact number.");

            }
            else if (!contact.isEmpty()){
                documentReference.update("contact",contact);

            }
        });

        DocumentReference documentReference = fStore.collection("users").document(uID);
        documentReference.addSnapshotListener(requireActivity(), (documentSnapshot, error) -> {
            if (documentSnapshot != null) {
                edit_uname.setHint(documentSnapshot.getString("u_name"));
                edit_fname.setHint(documentSnapshot.getString("f_name"));
                edit_lname.setHint(documentSnapshot.getString("l_name"));
                edit_contact.setHint(documentSnapshot.getString("contact"));
                edit_email.setHint(documentSnapshot.getString("email"));
            }
        });
        return view;
        // Inflate the layout for this fragment

    }

    private void setAccountSettingsFragment(fragment_account_settings a) {
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.dashboard_layout,a);
        fragmentTransaction.commit();
    }
}