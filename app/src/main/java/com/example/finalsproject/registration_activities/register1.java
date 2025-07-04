package com.example.finalsproject.registration_activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.finalsproject.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class register1 extends Fragment {
    //VARIABLE DECLARATIONS//
    View view;
    FirebaseAuth fAuth;
    Button next_btn1, back_btn1;
    TextInputEditText first_name,last_name,contact_num;

    TextInputLayout firstnamelayout,lastnamelayout,contactlayout;
    ConstraintLayout fragment_layout,register_layout;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //VIEWS DECLARATIONS//
        view = inflater.inflate(R.layout.fragment_register1, container, false); // Inflate the layout for this fragment
        fAuth = FirebaseAuth.getInstance();
        next_btn1 = view.findViewById(R.id.next_btn1);
        back_btn1 = view.findViewById(R.id.back_btn1);
        fragment_layout = view.findViewById(R.id.fragment_layout);
        register_layout = view.findViewById(R.id.register_layout);
        //regis1 data
        first_name = view.findViewById(R.id.first_name);
        last_name = view.findViewById(R.id.last_name);
        contact_num = view.findViewById(R.id.contact_num);

        firstnamelayout = view.findViewById(R.id.firstnamelayout);
        lastnamelayout = view.findViewById(R.id.lastnamelayout);
        contactlayout = view.findViewById(R.id.contactlayout);

        //LISTENERS//
        next_btn1.setOnClickListener(view -> {
            String
                    f_name = Objects.requireNonNull(first_name.getText()).toString().trim(),
                    l_name = Objects.requireNonNull(last_name.getText()).toString().trim(),
                    contact = Objects.requireNonNull(contact_num.getText()).toString().trim();

            //TEMPORARY//
            if(f_name.isEmpty()){
                first_name.setError("First name is required.");
            }
            else if (!f_name.matches("[a-zA-Z ]+")) {
                firstnamelayout.setError("First name should contain letters only.");
                firstnamelayout.setErrorIconDrawable(null);

            }
            else if(l_name.isEmpty()){
                last_name.setError("Last name is required.");
            }
            else if (!l_name.matches("[a-zA-Z ]+")) {
                lastnamelayout.setError("Last name should contain letters only.");
                lastnamelayout.setErrorIconDrawable(null);
            }
            else if(contact.isEmpty()){
                contact_num.setError("Contact number is required.");
            }
            else if (contact.length()<=10||!contact.startsWith("09")) {
                contactlayout.setError("Invalid contact number.");
                contactlayout.setErrorIconDrawable(null);
            }
            //CONTINUE//
            else{
                register_values.first_name = f_name;
                register_values.last_name = l_name;
                register_values.contact = contact;
                replaceFragment(new register2());
                //TRANSFER DATA TO DATABASE COLLECTION//
            }
        });
        back_btn1.setOnClickListener(view -> {
            //RETURN
            startActivity(new Intent(getActivity(),register.class));
        });
        return view;
    }
    //FRAGMENT METHOD//
    private void replaceFragment(register2 register2) {
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_layout,register2);
        fragmentTransaction.commit();
    }
}