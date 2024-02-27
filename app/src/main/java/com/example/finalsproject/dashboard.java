package com.example.finalsproject;
import static com.example.finalsproject.SplashScreen.SHARED_PREFS;
import static com.example.finalsproject.SplashScreen.STATUS;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;

public class dashboard extends AppCompatActivity {
    FirebaseAuth fAuth;
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard);
        fAuth = FirebaseAuth.getInstance();
        findViewById(R.id.logout_btn).setOnClickListener(view ->{
            startActivity(new Intent(this,login.class));
            un_remember_save();
            finish();
        });
    }
    public void un_remember_save(){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(STATUS, false);
        editor.apply();
    }
}