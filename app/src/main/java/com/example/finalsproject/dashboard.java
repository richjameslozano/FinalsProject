package com.example.finalsproject;

import static com.example.finalsproject.SplashScreen.EMAIL;
import static com.example.finalsproject.SplashScreen.PASSWORD;
import static com.example.finalsproject.SplashScreen.SHARED_PREFS;
import static com.example.finalsproject.SplashScreen.STATUS;
import static com.example.finalsproject.SplashScreen.UID;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

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
        //FIREBASE INSTANCES//
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        uiD = Objects.requireNonNull(fAuth.getCurrentUser()).getUid();

        setNavigationView();

        //VIEWS
        logout_btn = findViewById(R.id.logout_btn);
        acc_type = findViewById(R.id.acc_type);
        u_name = findViewById(R.id.u_name);
        f_name = findViewById(R.id.f_name);
        l_name = findViewById(R.id.l_name);
        contact = findViewById(R.id.contact);
        email = findViewById(R.id.email);
        pass = findViewById(R.id.pass);
        textView = findViewById(R.id.title);
        //PROFILE//
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
            fAuth.signOut();
            un_remember_save();
            startActivity(new Intent(this,login.class));
            finish();
        });
    }
    private void setNavigationView(){
        //NAVIGATION//
        drawerLayout = findViewById(R.id.drawer_layout);
        toolbar = findViewById(R.id.toolbar);
        navigationView = findViewById(R.id.nav_view);
        setSupportActionBar(toolbar);
        //actionbar

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        //navigation
        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(this);
        // HANDLE HOW EACH DASHBOARD DIFFERS PER USER THROUGH LAYOUTS //
        DocumentReference documentReference = fStore.collection("users").document(uiD);
        documentReference.addSnapshotListener(this, (documentSnapshot, error) -> {
            if (documentSnapshot != null) {
                Menu menu = navigationView.getMenu();
                menu.clear();
                switch (Objects.requireNonNull(documentSnapshot.getString("acc_type")).toLowerCase()){
                    case "admin":
                        navigationView.inflateMenu(R.menu.admin_menu);
                        break;
                    case "customer":
                        navigationView.inflateMenu(R.menu.customer_menu);
                        break;
                    case "employee":
                        navigationView.inflateMenu(R.menu.employee_menu);
                        break;
                    case "subcontractor":
                        navigationView.inflateMenu(R.menu.subcontractor_menu);
                        break;
                }
            }
        });
    }
    public void un_remember_save(){ //SHARED PREFERENCES SET BACK TO DEFAULT VALUES
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(STATUS, false);
        editor.putString(UID,"uid");
        editor.putString(EMAIL,"email");
        editor.putString(PASSWORD,"password");
        editor.apply();
    }

    @Override   //FRAGMENTS UTILIZATION POTENTIAL//
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        DocumentReference documentReference = fStore.collection("users").document(uiD);
        documentReference.addSnapshotListener(this, (documentSnapshot, error) -> {
            if (documentSnapshot != null) {
                switch (Objects.requireNonNull(documentSnapshot.getString("acc_type")).toLowerCase()){
                    case "admin":
                        switch (Objects.requireNonNull(item.getTitle()).toString()){
                            case "Home":
                                //
                                break;
                            case "Account Management":
                                //
                                break;
                            case "Transaction History":
                                //
                                break;
                            case "Profile":
                                //
                                break;
                            case "Logout":
                                startActivity(new Intent(this, login.class));
                                finish();
                                break;
                        }
                        break;
                    case "employee":
                        switch (Objects.requireNonNull(item.getTitle()).toString()){
                            case "Home":
                                //
                                break;
                            case "Delivery Inquiries":
                                //
                                break;
                            case "Pending Deliveries":
                                //
                                break;
                            case "Deliveries History":
                                //
                                break;
                            case "Profile":
                                //
                                break;
                            case "Account Settings":
                                //
                                break;
                            case "Logout":
                                startActivity(new Intent(this, login.class));
                                finish();
                                break;
                        }
                        break;
                    case "customer":
                        switch (Objects.requireNonNull(item.getTitle()).toString()){
                            case "Home":
                                //
                                break;
                            case "Inquire Lost Luggage":
                                //
                                break;
                            case "Luggage Monitoring":
                                //
                                break;
                            case "Profile":
                                //
                                break;
                            case "Account Settings":
                                //
                                break;
                            case "Logout":
                                startActivity(new Intent(this, login.class));
                                finish();
                                break;
                        }
                        break;
                    case "subcontractor":
                        switch (Objects.requireNonNull(item.getTitle()).toString()){
                            case "Home":
                                //
                                break;
                            case "Available Deliveries":
                                //
                                break;
                            case "Delivery History":
                                //
                                break;
                            case "Profile":
                                //
                                break;
                            case "Account Settings":
                                //
                                break;
                            case "Logout":
                                startActivity(new Intent(this, login.class));
                                finish();
                                break;
                        }
                        break;
                }
            }
        });
        return true;
    }
    @Override
    public void onBackPressed() {//slide to back function
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else if (!drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.openDrawer(GravityCompat.START);
        }
        else {
            super.onBackPressed();
        }
    }
}
