package com.example.finalsproject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.finalsproject.dashboard_user_activities.dashboard;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

@SuppressLint("CustomSplashScreen")
public class SplashScreen extends AppCompatActivity {
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String STATUS = "status";
    public static final String UID = "uid";
    public static final String EMAIL = "email";
    public static final String PASSWORD = "password";
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        @SuppressLint({"MissingInflateId", "LocalSuppress"})
        ImageView img = findViewById(R.id.splashimg);
        Glide.with(SplashScreen.this)
                .load(R.drawable.spongebob)
                .into(img);
        new Handler().postDelayed(() -> {
            boolean login_status;
            SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
            login_status = sharedPreferences.getBoolean(STATUS, false);
            if(login_status){//IF TRUE
                //LOGGED IN//
                fAuth.signInWithEmailAndPassword(EMAIL,PASSWORD);
                startActivity(new Intent(this, dashboard.class));
                finish();
            }
            else{//ELSE FALSE
                //NOT LOGGED IN//
                startActivity(new Intent(this, login.class));
                finish();
            }
        },2000);
    }
}