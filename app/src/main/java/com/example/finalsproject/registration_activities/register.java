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

import com.example.finalsproject.Login;
import com.example.finalsproject.R;
import com.google.firebase.auth.FirebaseAuth;

public class register extends AppCompatActivity {
    //VARIABLE DECLARATIONS//
    FirebaseAuth fAuth;
    Button next_btn, back_btn;
    Spinner spinner;
    EditText user_regis;
    ConstraintLayout fragment_layout;
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
        fragment_layout = findViewById(R.id.fragment_layout);

        //NOT WORKING?
//        if (fAuth.getCurrentUser() != null){
//            startActivity(new Intent(register.this, MainActivity.class));
//            finish();
//        }

        //Spinner//
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.register_acctype, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        //LISTENERS//
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String selectedItem = parent.getItemAtPosition(position).toString();
                    // Do something with the selected item
                    switch (selectedItem) {
                        case "Customer":
                        case "Subcontractor":
                            account_type = selectedItem;
                            Toast.makeText(getApplicationContext(), "Selected: " + selectedItem, Toast.LENGTH_SHORT).show();
                            break;
                        default:
                            // Handle other cases if needed
                            break;
                    }
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
        next_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user = user_regis.getText().toString().trim();
                if(user.isEmpty()){
                    Toast.makeText(register.this, "Please enter an account username.", Toast.LENGTH_SHORT).show();
                } else if (user.length()<5) {
                    Toast.makeText(register.this, "Username should be more than at least 5 characters.", Toast.LENGTH_SHORT).show();
                }
                //CHECK IF USERNAME IN DATABASE IS TAKEN//
                else if(user.equals("taken")){
                    Toast.makeText(register.this, "Username is already taken.", Toast.LENGTH_SHORT).show();
                }
                //CONTINUE//
                else if(!user.equals("taken")){
                    register_values.account_type = account_type;
                    fragment_layout.setVisibility(View.VISIBLE);
                    replaceFragment(new register1());
                }
            }
        });
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //CONTINUE
                Intent i = new Intent(getApplicationContext(), Login.class);
                startActivity(i); //BACK TO DEFAULT ACTIVITY
            }
        });
    }
    private void replaceFragment(register1 register1) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_layout,register1);
        fragmentTransaction.commit();
    }
}