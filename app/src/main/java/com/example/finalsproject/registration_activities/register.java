package com.example.finalsproject.registration_activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.finalsproject.MainActivity;
import com.example.finalsproject.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class register extends AppCompatActivity {
    FirebaseAuth fAuth;
    EditText email_regis,pass_regis;
    EditText first_name,last_name,address,contact_num;
    Button nextbtn;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //regis1 data
        first_name = findViewById(R.id.first_name);
        last_name = findViewById(R.id.last_name);
        address = findViewById(R.id.address);
        contact_num = findViewById(R.id.contact_num);

        //regis2 data
        nextbtn = findViewById(R.id.next_btn2);
        email_regis = findViewById(R.id.email_regis);
        pass_regis = findViewById(R.id.pass_regis);

        //firebase instance
        fAuth = FirebaseAuth.getInstance();


        if (fAuth.getCurrentUser() != null){
            startActivity(new Intent(register.this, MainActivity.class));
            finish();
        }

        nextbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replaceFragment(new register1());
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