package com.example.finalsproject.dashboard_user_activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.finalsproject.R;
import com.example.finalsproject.login;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class account_settings extends Fragment {
    View view;
    ToggleButton reset,show1,show2;
    EditText confirm_EditText, pass_EditText;
    Button confirmReset_btn;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String uID;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //FIREBASE INSTANCES//
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        uID = Objects.requireNonNull(fAuth.getCurrentUser()).getUid();
        FirebaseUser user = fAuth.getCurrentUser();
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_account_settings, container, false);
        reset = view.findViewById(R.id.reset_btn);
        show1 = view.findViewById(R.id.show1);
        show2 = view.findViewById(R.id.show2);
        confirmReset_btn = view.findViewById(R.id.confirmReset_btn);
        pass_EditText = view.findViewById(R.id.pass_EditText);
        confirm_EditText = view.findViewById(R.id.confirm_EditText);
        pass_EditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        confirm_EditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        confirmReset_btn.setOnClickListener(v->{
            String pass = pass_EditText.getText().toString().trim();
            String confirm_pass = confirm_EditText.getText().toString().trim();
            if (pass.isEmpty()) {
                pass_EditText.setError("Password is required.");
            } else if (pass.length() < 8) {
                pass_EditText.setError("Password should be at least 8 characters.");
            }
            else if(confirm_pass.isEmpty()){
                confirm_EditText.setError("Confirm your password.");
            }
            else if(!pass.equals(confirm_pass)){
                confirm_EditText.setError("Password does not match.");
            }
            else{
                final AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(v.getContext());
                passwordResetDialog
                        .setTitle("Are you sure you want to reset your password?")
                        .setMessage("You are resetting your password.")
                        .setNegativeButton("No",(dialog, which) -> {})
                        .setPositiveButton("Yes",(dialog, which) -> user.updatePassword(pass)
                                .addOnSuccessListener(unused -> {
                                    Toast.makeText(getActivity(), "Password has been reset.", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(getActivity(), login.class));
                                })
                                .addOnFailureListener(e -> Toast.makeText(getActivity(), "Password could not be reset.", Toast.LENGTH_SHORT).show()))
                        .show();
            }
        });
        show1.setOnClickListener(v -> {
            if (show1.isChecked()) {
                pass_EditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);//move to the end of the edittext
            }
            else {
                pass_EditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            }
        });
        show2.setOnClickListener(v -> {
            if (show2.isChecked()) {
                confirm_EditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            }
            else {
                confirm_EditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            }
        });
        reset.setOnClickListener(v->{
            if(reset.isChecked()){
                pass_EditText.setVisibility(View.VISIBLE);
                confirm_EditText.setVisibility(View.VISIBLE);
                confirmReset_btn.setVisibility(View.VISIBLE);
                show1.setVisibility(View.VISIBLE);
                show2.setVisibility(View.VISIBLE);
            }
            else{
                pass_EditText.setVisibility(View.GONE);
                confirm_EditText.setVisibility(View.GONE);
                confirmReset_btn.setVisibility(View.GONE);
                show1.setVisibility(View.GONE);
                show2.setVisibility(View.GONE);
            }
        });
        return view;
    }
}