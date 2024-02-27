package com.example.finalsproject;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

@SuppressLint("CustomSplashScreen")
public class SplashScreen extends AppCompatActivity {
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String STATUS = "status";
    public static final String ACC_TYPE = "acc_type";
    public static final String UID = "uid";
    public static final String EMAIL = "email";
    public static final String PASSWORD = "password";
    FirebaseAuth fAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        fAuth = FirebaseAuth.getInstance();
        @SuppressLint({"MissingInflateId", "LocalSuppress"})
        ImageView img = findViewById(R.id.splashimg);
        Glide.with(SplashScreen.this)
                .load(R.drawable.spongebob)
                .into(img);

        new Handler().postDelayed(() -> {
            boolean login_status;
            SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
            login_status = sharedPreferences.getBoolean(STATUS, false);
            if(login_status){
                //LOGGED IN//
                fAuth.signInWithEmailAndPassword(EMAIL,PASSWORD);
                startActivity(new Intent(this, dashboard.class));
            }
            else{
                //NOT LOGGED IN//
                startActivity(new Intent(this, login.class));
            }
        },2000);
    }
    // HOW EACH DASHBOARD DIFFERS PER USER //
}