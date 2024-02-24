package com.example.finalsproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.finalsproject.registration_activities.register;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class Login extends AppCompatActivity {

    TextView signup;
    Button login_btn;
    EditText email_login,pass_login;
    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email_login =findViewById(R.id.email_login);
        pass_login = findViewById(R.id.pass_login);
        signup = findViewById(R.id.signup);
        login_btn = findViewById(R.id.login_btn);
        fAuth = FirebaseAuth.getInstance();
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Login.this, register.class);
                startActivity(i);
            }
        });

        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
                fAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(Login.this,"Login Successfully!",Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        }
                        else{
                            Toast.makeText(Login.this,Objects.requireNonNull(task.getException()).getMessage(),Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
    }
}