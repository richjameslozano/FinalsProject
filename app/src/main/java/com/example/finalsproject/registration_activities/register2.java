package com.example.finalsproject.registration_activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.finalsproject.Login;
import com.example.finalsproject.R;
import com.google.firebase.auth.FirebaseAuth;

public class register2 extends Fragment {
    EditText email_regis,pass_regis,confirm_pass_regis;
    FirebaseAuth  fAuth;
    Button next_btn2, back_btn2;
    View view;
    ConstraintLayout fragment_layout;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //VIEWS DECLARATIONS//
        view = inflater.inflate(R.layout.fragment_register2, container, false); // Inflate the layout for this fragment
        fAuth = FirebaseAuth.getInstance();
        next_btn2 = view.findViewById(R.id.next_btn2);
        back_btn2 = view.findViewById(R.id.back_btn2);
        fragment_layout = view.findViewById(R.id.fragment_layout);
        //regis2 data
        email_regis = view.findViewById(R.id.email_regis);
        pass_regis = view.findViewById(R.id.pass_regis);
        confirm_pass_regis = view.findViewById(R.id.confirm_pass);


        //LISTENERS//
        next_btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //CONTINUE
                String
                        email = email_regis.getText().toString().trim(),
                        pass = pass_regis.getText().toString().trim(),
                        confirm_pass = confirm_pass_regis.getText().toString().trim();
                if(email.isEmpty()){
                    email_regis.setError("Email address is required.");
                }
                else if (!email.matches("[a-zA-Z].*") || !email.matches(".*[a-zA-Z].*") || !email.endsWith("@gmail.com")) { //standard format of gmail addresses
                    email_regis.setError("Invalid email address.");
                }
                else if (pass.isEmpty()) {
                    pass_regis.setError("Password is required.");
                } else if (pass.length() < 8) {
                    pass_regis.setError("Password should be at least 8 characters.");
                }
                else if(confirm_pass.isEmpty()){
                    confirm_pass_regis.setError("Confirm your password.");
                }
                else if(!pass.equals(confirm_pass)){
                    confirm_pass_regis.setError("Password does not match.");
                }
                //CONTINUE//
                else{
                    register_values.email = email;
                    register_values.password = pass;
                    startActivity(new Intent(getActivity(),Login.class));
                }
            }
        });
        back_btn2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //CONTINUE
                replaceFragment(new register1());
            }
        });
        return view;
    }
    //FRAGMENT METHOD//
    private void replaceFragment(register1 register1) {
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_layout,register1);
        fragmentTransaction.commit();
    }
}