package com.example.finalsproject.dashboard_user_activities;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.finalsproject.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;


public class fragment_update_credentials extends Fragment {

View view;
TextInputEditText edit_uname, edit_LName, edit_FName,edit_email,edit_contact;

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
        edit_LName = view.findViewById(R.id.edit_LName);
        edit_FName = view.findViewById(R.id.edit_FName);
        edit_email = view.findViewById(R.id.edit_email);
        edit_contact = view.findViewById(R.id.edit_contact);
        save_btn = view.findViewById(R.id.save_btn);


        save_btn.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity()); // Use getActivity() if you are in a Fragment
            builder.setTitle("Confirm Save");
            builder.setMessage("Are you sure you want to save changes?");

            builder.setPositiveButton("Yes", (dialog, which) -> {
                DocumentReference documentReference = fStore.collection("users").document(uID);

                String
                        username = Objects.requireNonNull(edit_uname.getText()).toString().trim(),
                        lastname = Objects.requireNonNull(edit_LName.getText()).toString().trim(),
                        firstname = Objects.requireNonNull(edit_FName.getText()).toString().trim(),
                        email = Objects.requireNonNull(edit_email.getText()).toString().trim(),
                        contact = Objects.requireNonNull(edit_contact.getText()).toString().trim();

                // For each field, check if the user has entered data and only then update it
              if(!username.isEmpty()&& username.length()<6){
                  edit_uname.setError("Username should contain at least 6 characters");
                  return;
                    }
                else if (!username.isEmpty()) {
                    documentReference.update("u_name", username);
                }
                // No else needed - if empty, the field retains its previous value
                if(!firstname.isEmpty()&&!firstname.matches("[a-zA-Z ]+")){
                    edit_FName.setError("First name should contain letters only.");
                    return;
                }
                else if (!firstname.isEmpty()&&firstname.matches("[a-zA-Z ]+")) {
                    documentReference.update("f_name", firstname);
                }

                if (!lastname.isEmpty() && !lastname.matches("[a-zA-Z ]+")) {
                    edit_LName.setError("Last Name should contain letters only.");
                    return;
                }
                else if (!lastname.isEmpty()) {
                    documentReference.update("l_name", lastname);
                }
                if(!email.isEmpty()&&!email.matches(".+@gmail\\.com")){
                    edit_email.setError("Invalid email address.");
                    return;
                }
                else if (!email.isEmpty() && email.matches(".+@gmail\\.com")) {
                    documentReference.update("email", email);
                }

                if (!contact.isEmpty()&&contact.length() <=10 && !contact.startsWith("09")) {
                    edit_contact.setError("Invalid contact number.");
                    return;
                }
                else if(contact.length() == 11 && contact.startsWith("09")){
                    documentReference.update("contact", contact);
                }
                setAccountSettingsFragment(new fragment_account_settings());
            });

            builder.setNegativeButton("No", (dialog, which) -> {
                setAccountSettingsFragment(new fragment_account_settings());
                dialog.dismiss();
            });

            AlertDialog dialog = builder.create();
            dialog.show();
        });

        DocumentReference documentReference = fStore.collection("users").document(uID);
        documentReference.addSnapshotListener(requireActivity(), (documentSnapshot, error) -> {
            if (documentSnapshot != null) {
                edit_uname.setHint(documentSnapshot.getString("u_name"));
                edit_FName.setHint(documentSnapshot.getString("f_name"));
                edit_LName.setHint(documentSnapshot.getString("l_name"));
                edit_contact.setHint(documentSnapshot.getString("contact"));
                edit_email.setHint(documentSnapshot.getString("email"));
            }
        });
        return view;
        // Inflate the layout for this fragment

    }

    //used but not acknowledge
    private void setAccountSettingsFragment(fragment_account_settings a) {
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.dashboard_layout,a);
        fragmentTransaction.commit();
    }
}