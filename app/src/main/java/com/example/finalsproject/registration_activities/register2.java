package com.example.finalsproject.registration_activities;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.finalsproject.R;
import com.example.finalsproject.login;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class register2 extends Fragment {
    EditText email_regis,pass_regis,confirm_pass_regis;

    TextInputLayout confirmlayout,pass2layout,emaillayout;
    Button next_btn2, back_btn2;

    View view;
    ConstraintLayout fragment_layout;

    FirebaseAuth fAuth;
    FirebaseFirestore db;
    String userID;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //VIEWS DECLARATIONS//
        view = inflater.inflate(R.layout.fragment_register2, container, false); // Inflate the layout for this fragment
        next_btn2 = view.findViewById(R.id.next_btn2);
        back_btn2 = view.findViewById(R.id.back_btn2);
        fragment_layout = view.findViewById(R.id.fragment_layout);
        //regis2 data
        email_regis = view.findViewById(R.id.email_regis);
        pass_regis = view.findViewById(R.id.pass_regis);
        confirm_pass_regis = view.findViewById(R.id.confirm_pass_regis);
        confirmlayout = view.findViewById(R.id.confirmlayout);
        pass2layout = view.findViewById(R.id.pass2layout);
        emaillayout = view.findViewById(R.id.emaillayout);

        pass_regis.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        confirm_pass_regis.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        //FIREBASE INSTANCES
        fAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        //LISTENERS//
        next_btn2.setOnClickListener(view -> {
            //CONTINUE
            String  email = email_regis.getText().toString().trim(),
            pass = pass_regis.getText().toString().trim(),
            confirm_pass = confirm_pass_regis.getText().toString().trim();

            if(email.isEmpty()){
                emaillayout.setError("Email address is required.");
            }
            else if (!email.matches("[a-zA-Z].*") || !email.matches(".*[a-zA-Z].*") || !email.endsWith("@gmail.com")) { //standard format of gmail addresses
                emaillayout.setError("Invalid Email Address.");
                emaillayout.setErrorIconDrawable(null);
            }
            else if (pass.isEmpty()) {
                pass2layout.setError("Password is required.");
            } else if (pass.length() < 6) {
                pass2layout.setError("Password should be at least 6 characters.");
                pass2layout.setErrorIconDrawable(null);
            }
            else if(confirm_pass.isEmpty()){
                confirmlayout.setError("Confirm your password.");
                confirmlayout.setErrorIconDrawable(null);
            }
            else if(!pass.equals(confirm_pass)){
                confirmlayout.setError("Password does not match.");
                confirmlayout.setErrorIconDrawable(null);
            }
            //CONTINUE//
            else{
                register_values.email = email;
                register_values.password = pass;
                //REGISTER ALL CREDENTIALS//
                fAuth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        userID = Objects.requireNonNull(fAuth.getCurrentUser()).getUid(); //GET CURRENT USER ID
                        startActivity(new Intent(getActivity(), login.class));
                        Toast.makeText(getActivity(), "Account created.",Toast.LENGTH_SHORT).show();
                        DocumentReference documentReference = db.collection("users").document(userID);
                        Map<String, Object> user = getStringObjectMap();
                        documentReference.set(user).addOnSuccessListener(unused -> Log.d(TAG,"User profile created for "+ userID));
                    }
                    else{
                        Toast.makeText(getActivity(), "Account creation error!" + Objects.requireNonNull(task.getException()).getMessage(),Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
        back_btn2.setOnClickListener(view -> {
            //CONTINUE
            replaceFragment(new register1());
        });
        return view;
    }

    @NonNull
    private static Map<String, Object> getStringObjectMap() {
        Map<String,Object> user = new HashMap<>();
        user.put("acc_type",register_values.account_type);
        user.put("u_name",register_values.username);
        user.put("f_name",register_values.first_name);
        user.put("l_name",register_values.last_name);
        user.put("contact",register_values.contact);
        user.put("email",register_values.email);
        user.put("first_pass",register_values.password);
        return user;
    }

    //FRAGMENT METHOD//
    private void replaceFragment(register1 register1) {
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_layout,register1);
        fragmentTransaction.commit();
    }
}