package com.example.finalsproject;
import static com.example.finalsproject.SplashScreen.ACC_TYPE;
import static com.example.finalsproject.SplashScreen.EMAIL;
import static com.example.finalsproject.SplashScreen.PASSWORD;
import static com.example.finalsproject.SplashScreen.SHARED_PREFS;
import static com.example.finalsproject.SplashScreen.STATUS;
import static com.example.finalsproject.SplashScreen.UID;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class dashboard extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String uiD;
    TextView acc_type,u_name,f_name,l_name,contact,email,pass,textView;
    Button logout_btn;
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard);

        //HOOKS//
        //toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //drawer
        drawerLayout = findViewById(R.id.drawer_layout);
        //actionbar
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        //navigationview
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


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
        textView = findViewById(R.id.title);
        DocumentReference documentReference = fStore.collection("users").document(uiD);
        documentReference.addSnapshotListener(this, (documentSnapshot, error) -> {
            if (documentSnapshot != null) {
                // HANDLE HOW EACH DASHBOARD DIFFERS PER USER POSSIBLY THROUGH FRAGMENTATION HERE (TO FOLLOW) //
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
            fAuth.signOut();
            un_remember_save();
            startActivity(new Intent(getApplicationContext(),login.class));
            finish();
        });




    }




    public void un_remember_save(){ //BACK TO DEFAULT VALUES
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(STATUS, false);
        editor.putString(UID,"uid");
        editor.putString(EMAIL,"email");
        editor.putString(PASSWORD,"password");
        editor.putString(ACC_TYPE,"acc_type");
        editor.apply();
        }
    //slide to back function
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    //selection in navigation bar
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//        switch (MenuItem.getItemId()){
//
//        }

        return true;
    }
}
