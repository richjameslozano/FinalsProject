package com.example.finalsproject.registration_activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.finalsproject.login;
import com.example.finalsproject.R;
import com.google.firebase.auth.FirebaseAuth;

public class register extends AppCompatActivity {
    //VARIABLE DECLARATIONS//
    FirebaseAuth fAuth;
    Button next_btn, back_btn;
    Spinner spinner;
    EditText user_regis;
    ConstraintLayout register_layout, fragment_layout;
    String account_type;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        //VIEW DECLARATIONS//
        fAuth = FirebaseAuth.getInstance(); //firebase instance
        next_btn = findViewById(R.id.next_btn);
        back_btn = findViewById(R.id.back_btn);
        spinner = findViewById(R.id.acctype_spinner);
        user_regis = findViewById(R.id.user_regis);
        register_layout = findViewById(R.id.register_layout);
        fragment_layout = findViewById(R.id.fragment_layout);

        //SPINNER//
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.register_acc_type, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);


        //LISTENERS//
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String selectedItem = parent.getItemAtPosition(position).toString();
                    // Do something with the selected item
                    account_type = selectedItem;
                    Toast.makeText(getApplicationContext(), "Selected: " + selectedItem, Toast.LENGTH_SHORT).show();
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
        next_btn.setOnClickListener(view -> {

            String user = user_regis.getText().toString().trim();
            if(user.isEmpty()){
                user_regis.setError("Please enter an account username.");
            }
            else if (user.length()<5) {
                user_regis.setError("Username should be more than at least 5 characters.");
            }
            else if (!user.matches(".*[a-zA-Z].*")) {
                user_regis.setError("Username should contain at least one letter.");
            }
            //CHECK IF USERNAME IN DATABASE IS TAKEN//
            else if(user.equals("Taken")){
                user_regis.setError("Username is already taken.");
            }
            //CONTINUE//
            else if(!user.equals("taken")){
                register_values.account_type = account_type;
                register_values.username = user;
                replaceFragment(new register1());
                fragment_layout.setVisibility(View.VISIBLE);
                register_layout.setVisibility(View.GONE);
            }
        });
        back_btn.setOnClickListener(view -> {
            //CONTINUE
            startActivity(new Intent(getApplicationContext(), login.class));
        });
    }
    //FRAGMENT METHOD//
    private void replaceFragment(register1 register1) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_layout,register1);
        fragmentTransaction.commit();
    }
}