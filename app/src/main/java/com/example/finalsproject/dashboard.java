package com.example.finalsproject;
import static com.example.finalsproject.SplashScreen.EMAIL;
import static com.example.finalsproject.SplashScreen.PASSWORD;
import static com.example.finalsproject.SplashScreen.SHARED_PREFS;
import static com.example.finalsproject.SplashScreen.STATUS;
import static com.example.finalsproject.SplashScreen.UID;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class dashboard extends AppCompatActivity {
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String uiD;
    TextView acc_type,u_name,f_name,l_name,contact,email,pass;
    Button logout_btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard);
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        uiD = Objects.requireNonNull(fAuth.getCurrentUser()).getUid();
        logout_btn = findViewById(R.id.logout_btn);
        acc_type = findViewById(R.id.acc_type);
        u_name = findViewById(R.id.u_name);
        f_name = findViewById(R.id.f_name);
        l_name = findViewById(R.id.l_name);
        contact = findViewById(R.id.contact);
        email = findViewById(R.id.email);
        pass = findViewById(R.id.pass);
        DocumentReference documentReference = fStore.collection("users").document(uiD);
        documentReference.addSnapshotListener(this, (documentSnapshot, error) -> {
            if (documentSnapshot != null) {
                acc_type.setText(documentSnapshot.getString("acc_type"));
                u_name.setText(documentSnapshot.getString("u_name"));
                f_name.setText(documentSnapshot.getString("f_name"));
                l_name.setText(documentSnapshot.getString("l_name"));
                contact.setText(documentSnapshot.getString("contact"));
                email.setText(documentSnapshot.getString("email"));
                pass.setText(documentSnapshot.getString("pass"));
            }
        });

        //LISTENERS
        logout_btn.setOnClickListener(view ->{
            startActivity(new Intent(getApplicationContext(),login.class));
            fAuth.signOut();
            un_remember_save();
            finish();
        });
    }
    public void un_remember_save(){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(STATUS, false);
        editor.putString(UID,"uid");
        editor.putString(EMAIL,"email");
        editor.putString(PASSWORD,"password");
        editor.apply();
    }
}