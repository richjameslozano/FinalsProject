package com.example.finalsproject;

import static com.example.finalsproject.SplashScreen.ACC_TYPE;
import static com.example.finalsproject.SplashScreen.EMAIL;
import static com.example.finalsproject.SplashScreen.PASSWORD;
import static com.example.finalsproject.SplashScreen.SHARED_PREFS;
import static com.example.finalsproject.SplashScreen.STATUS;
import static com.example.finalsproject.SplashScreen.UID;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.finalsproject.registration_activities.register;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class login extends AppCompatActivity {

    TextView signup;
    Button login_btn;
    CheckBox remember_btn;
    EditText email_login,pass_login;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String uId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email_login =findViewById(R.id.email_login);
        pass_login = findViewById(R.id.pass_login);
        signup = findViewById(R.id.signup);
        login_btn = findViewById(R.id.login_btn);
        remember_btn = findViewById(R.id.remember_btn);
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        signup.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(),register.class)));
        login_btn.setOnClickListener(view -> {

            String email = email_login.getText().toString().trim();
            String password = pass_login.getText().toString().trim();

            if (TextUtils.isEmpty(email)){
                email_login.setError("Email is Required");
                return;
            }
                if(TextUtils.isEmpty(password)){
                    pass_login.setError("Password is Required.");
                    return;
                }
                if(password.length()<6){
                    pass_login.setError("Password must be 6 Characters and above");
                  return;
            }
            //authentication
            fAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(task -> {
                if (task.isSuccessful()){
                    uId = Objects.requireNonNull(fAuth.getCurrentUser()).getUid();
                    remember_save();
                    startActivity(new Intent(getApplicationContext(), dashboard.class));
                    finish();
                }
                else{
                    Toast.makeText(login.this,Objects.requireNonNull(task.getException()).getMessage(),Toast.LENGTH_LONG).show();
                }
            });
        });
    }
    public void remember_save(){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(STATUS, remember_btn.isChecked());
        editor.putString(UID,uId);
        editor.putString(EMAIL,email_login.getText().toString().trim());
        editor.putString(PASSWORD,pass_login.getText().toString().trim());
        //ACCOUNT TYPE//
        DocumentReference documentReference = fStore.collection("users").document(uId);
        documentReference.addSnapshotListener(this, (documentSnapshot, error) -> {
            assert documentSnapshot != null;
            String field = documentSnapshot.getString("acc_type");
            editor.putString(ACC_TYPE,field);
            Toast.makeText(login.this,"Account Type: " + field,Toast.LENGTH_SHORT).show(); //DEBUG
        });
        editor.apply();
    }
}