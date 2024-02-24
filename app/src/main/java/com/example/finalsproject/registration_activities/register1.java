package com.example.finalsproject.registration_activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.finalsproject.Login;
import com.example.finalsproject.R;
import com.google.firebase.auth.FirebaseAuth;

public class register1 extends Fragment {
    //VARIABLE DECLARATIONS//
    View view;
    FirebaseAuth fAuth;
    Button next_btn1, back_btn1;
    EditText first_name,last_name,address,contact_num;
    ConstraintLayout fragment_layout;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //VIEWS DECLARATIONS//
        view = inflater.inflate(R.layout.fragment_register1, container, false); // Inflate the layout for this fragment
        fAuth = FirebaseAuth.getInstance();
        next_btn1 = view.findViewById(R.id.next_btn1);
        back_btn1 = view.findViewById(R.id.back_btn1);
        fragment_layout = view.findViewById(R.id.fragment_layout);
        //regis1 data
        first_name = view.findViewById(R.id.first_name);
        last_name = view.findViewById(R.id.last_name);
        address = view.findViewById(R.id.address);
        contact_num = view.findViewById(R.id.contact_num);
        String
        f_name = first_name.getText().toString().trim(),
        l_name = last_name.getText().toString().trim(),
        add = address.getText().toString().trim(),
        contact = contact_num.getText().toString().trim();
        //LISTENERS//
        next_btn1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //TEMPORARY//
                Toast.makeText(getActivity(), "Account Type: " + register_values.account_type, Toast.LENGTH_SHORT).show();
                register_values.first_name = f_name;
                register_values.last_name = l_name;
                register_values.address = add;
                register_values.contact = contact;
                replaceFragment(new register2());
            }
        });
        back_btn1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //RETURN
                Intent i = new Intent(getActivity(),register.class);
                startActivity(i);
            }
        });
        return view;
    }

    private void replaceFragment(register2 register2) {
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_layout,register2);
        fragmentTransaction.commit();
    }
}