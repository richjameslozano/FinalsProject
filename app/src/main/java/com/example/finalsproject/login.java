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
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.finalsproject.dashboard_user_activities.dashboard;
import com.example.finalsproject.registration_activities.register;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class login extends AppCompatActivity {

    TextView signup,forgot_btn;
    Button login_btn;
    CheckBox remember_btn;

    TextInputEditText email_login,pass_login;

  TextInputLayout login_pass_layout,login_email_layout;

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String uID;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        email_login =findViewById(R.id.email_login);
        pass_login = findViewById(R.id.pass_login);
        pass_login.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        signup = findViewById(R.id.signup);
        login_btn = findViewById(R.id.login_btn);

       login_pass_layout = findViewById(R.id.login_pass_layout);
       login_email_layout = findViewById(R.id.login_email_layout);

        forgot_btn = findViewById(R.id.forgot);
        remember_btn = findViewById(R.id.remember_btn);
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        signup.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(),register.class)));
        login_btn.setOnClickListener(v -> {
            String email = Objects.requireNonNull(email_login.getText()).toString().trim();
            String password = Objects.requireNonNull(pass_login.getText()).toString().trim();


            if(email.isEmpty()){
                login_email_layout.setError("Email is Required.");
                login_email_layout.setErrorIconDrawable(null);
                return;
                }
            else if (!email.matches("[a-zA-Z].*") || !email.matches(".*[a-zA-Z].*") || !email.endsWith("@gmail.com")) { //standard format of gmail addresses
                login_email_layout.setError("Invalid email address.");
                login_email_layout.setErrorIconDrawable(null);
                return;
                }

            if (password.isEmpty()) {
                login_pass_layout.setError("Password is Required.");
                login_pass_layout.setErrorIconDrawable(null);
                }
            else if (password.length() < 6) {
                        login_pass_layout.setError("Password should be at least 6 characters.");
                        login_pass_layout.setErrorIconDrawable(null);
                    }

else {
                //authentication
                fAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        uID = Objects.requireNonNull(fAuth.getCurrentUser()).getUid();
                        remember_save();
                        startActivity(new Intent(getApplicationContext(), dashboard.class));
                        finish();
                    } else {
                        Toast.makeText(login.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
        //PASSWORD RESET//
        forgot_btn.setOnClickListener(this::showAlertDialogButtonClicked);
    }
    public void remember_save(){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(STATUS, remember_btn.isChecked())
        .putString(UID, uID)
        .putString(EMAIL, Objects.requireNonNull(email_login.getText()).toString().trim())
        .putString(PASSWORD, Objects.requireNonNull(pass_login.getText()).toString().trim())
        .apply();
    }
    public void showAlertDialogButtonClicked(View view) {
        final View customLayout = getLayoutInflater().inflate(R.layout.activity_forgot_password_dialog, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(customLayout)
        .setNegativeButton("No", (dialog, which) -> {
        })
        .setPositiveButton("Yes", (dialog, which) -> {
            EditText editText = customLayout.findViewById(R.id.emailReset);
            String email = editText.getText().toString().trim();
            if (email.isEmpty()){
                Toast.makeText(this,"Email does not exist in our system.",Toast.LENGTH_SHORT).show();
            }
            else{
                sendEmail(email);
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    private void sendEmail(String email){
        fAuth.sendPasswordResetEmail(email)
            .addOnSuccessListener(unused -> Toast.makeText(this,"Email sent successfully.",Toast.LENGTH_SHORT).show())
            .addOnFailureListener(e -> Toast.makeText(this,"Email failed to send.",Toast.LENGTH_SHORT).show());
    }
}