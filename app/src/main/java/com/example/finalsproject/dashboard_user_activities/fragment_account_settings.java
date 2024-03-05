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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.finalsproject.R;
import com.example.finalsproject.login;
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
    ToggleButton reset,show1,show2;
    EditText confirm_EditText, pass_EditText;
    Button confirmReset_btn,changePfp_btn;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    StorageReference storageReference;
    String uID;
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
        reset = view.findViewById(R.id.reset_btn);
        show1 = view.findViewById(R.id.show1);
        show2 = view.findViewById(R.id.show2);
        changePfp_btn = view.findViewById(R.id.changepfp_btn);
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
        changePfp_btn.setOnClickListener(v -> {
                Intent openGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(openGalleryIntent,1000);
        });
        return view;
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