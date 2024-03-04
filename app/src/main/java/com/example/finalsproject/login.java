package com.example.finalsproject;

import static com.example.finalsproject.SplashScreen.EMAIL;
import static com.example.finalsproject.SplashScreen.PASSWORD;
import static com.example.finalsproject.SplashScreen.SHARED_PREFS;
import static com.example.finalsproject.SplashScreen.STATUS;
import static com.example.finalsproject.SplashScreen.UID;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.finalsproject.dashboard_user_activities.dashboard;
import com.example.finalsproject.registration_activities.register;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class login extends AppCompatActivity {

    TextView signup,forgot_btn;
    Button login_btn;
    ToggleButton show;
    CheckBox remember_btn;
    EditText email_login,pass_login;
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
        show = findViewById(R.id.show);
        forgot_btn = findViewById(R.id.forgot);
        remember_btn = findViewById(R.id.remember_btn);
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        signup.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(),register.class)));
        login_btn.setOnClickListener(v -> {
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
                    uID = Objects.requireNonNull(fAuth.getCurrentUser()).getUid();
                    remember_save();
                    startActivity(new Intent(getApplicationContext(), dashboard.class));
                    finish();
                }
                else{
                    Toast.makeText(login.this,Objects.requireNonNull(task.getException()).getMessage(),Toast.LENGTH_LONG).show();
                }
            });
        });
        show.setOnClickListener(v -> {
            pass_login.setTypeface(Typeface.DEFAULT);
            if (show.isChecked()) {
                pass_login.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);//move to the end of the edittext
            }
            else {
                pass_login.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
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
        .putString(EMAIL,email_login.getText().toString().trim())
        .putString(PASSWORD,pass_login.getText().toString().trim())
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