package com.example.finalsproject.dashboard_user_activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.example.finalsproject.R;
import com.example.finalsproject.login;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class fragment_account_settings extends Fragment {
    View view;
    ImageView pfp_acc;
    ToggleButton reset;

    TextInputLayout passLayout, confirmLayout;
    TextInputEditText confirm_EditText, pass_EditText;
    Button confirmReset_btn,changePfp_btn,update_btn;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    StorageReference storageReference;

    String uID;

    FragmentManager fragmentManager;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //FIREBASE INSTANCES//
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        uID = Objects.requireNonNull(fAuth.getCurrentUser()).getUid();
        storageReference = FirebaseStorage.getInstance().getReference();
        FirebaseUser user = fAuth.getCurrentUser();

        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_account_settings, container, false);
        pfp_acc = view.findViewById(R.id.pfp_acc);
        StorageReference profileRef = storageReference.child("users/"+Objects.requireNonNull(fAuth.getCurrentUser()).getUid()+"/profile.jpg");
        profileRef.getDownloadUrl().addOnSuccessListener(uri -> Picasso.get().load(uri).into(pfp_acc));
        passLayout = view.findViewById(R.id.passlayout);
        confirmLayout = view.findViewById(R.id.confirmlayout);

        reset = view.findViewById(R.id.reset_btn);

        changePfp_btn = view.findViewById(R.id.changepfp_bt);
        confirmReset_btn = view.findViewById(R.id.confirmReset_btn);
        pass_EditText = view.findViewById(R.id.pass_EditText);
        confirm_EditText = view.findViewById(R.id.confirm_EditText);
        pass_EditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        confirm_EditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        update_btn = view.findViewById(R.id.update_btn);

        update_btn.setOnClickListener(v-> editFragment(new fragment_update_credentials()));
        confirmReset_btn.setOnClickListener(v->{
            String pass = Objects.requireNonNull(pass_EditText.getText()).toString().trim();
            String confirm_pass = Objects.requireNonNull(confirm_EditText.getText()).toString().trim();
            if (pass.isEmpty()) {
                passLayout.setError("Password is required.");
                passLayout.setErrorIconDrawable(null);
            } else if (pass.length() < 6) {
                passLayout.setError("Password should be at least 6 characters.");
                passLayout.setErrorIconDrawable(null);
            }
            else if(confirm_pass.isEmpty()){
                confirmLayout.setError("Confirm your password.");
                confirmLayout.setErrorIconDrawable(null);
            }
            else if(!pass.equals(confirm_pass)){
                confirmLayout.setError("Password does not match.");
                confirmLayout.setErrorIconDrawable(null);
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

        reset.setOnClickListener(v->{
            if(reset.isChecked()){
                passLayout.setVisibility(View.VISIBLE);
                confirmLayout.setVisibility(View.VISIBLE);
                pass_EditText.setVisibility(View.VISIBLE);
                confirm_EditText.setVisibility(View.VISIBLE);
                confirmReset_btn.setVisibility(View.VISIBLE);
            }
            else{
                passLayout.setVisibility(View.GONE);
                confirmLayout.setVisibility(View.GONE);
                pass_EditText.setVisibility(View.GONE);
                confirm_EditText.setVisibility(View.GONE);
                confirmReset_btn.setVisibility(View.GONE);
            }
        });
        changePfp_btn.setOnClickListener(v -> {
                Intent openGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(openGalleryIntent,1000);
        });
        return view;
    }
    private void editFragment(fragment_update_credentials a) {
         fragmentManager = getParentFragmentManager(); // or getChildFragmentManager() if called within a Fragment
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.dashboard_layout,a); // Replace fragment_container with your actual fragment container ID
        fragmentTransaction.addToBackStack(null); // Optional, adds the transaction to the back stack
        fragmentTransaction.commit();
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1000){
            if(resultCode== Activity.RESULT_OK){
                assert data != null;
                Uri imageUri = data.getData();
                        Glide.with(this)
                        .load(imageUri)
                        .into(pfp_acc);
                        uploadImageToFirebase(imageUri);
            }
        }
    }
    private void uploadImageToFirebase(Uri imageUri) {
        StorageReference fileRef = storageReference.child("users/"+Objects.requireNonNull(fAuth.getCurrentUser()).getUid()+"/profile.jpg");
        fileRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> Toast.makeText(getActivity(), "Image uploaded.", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(getActivity(), "Image failed to upload.", Toast.LENGTH_SHORT).show());
    }
}