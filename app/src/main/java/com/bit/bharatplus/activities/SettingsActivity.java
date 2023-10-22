package com.bit.bharatplus.activities;

import static com.bit.bharatplus.activities.CompleteProfileActivity.isValidContextForGlide;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bit.bharatplus.databinding.ActivitySettingsBinding;
import com.bit.bharatplus.models.UserModel;
import com.bit.bharatplus.utils.AndroidUtils;
import com.bit.bharatplus.utils.FirebaseUtil;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Objects;

public class SettingsActivity extends AppCompatActivity {

    public static final int SELECT_PICTURE = 200;
    ActivitySettingsBinding binding;
    SharedPreferences sp;
    FirebaseDatabase db;
    FirebaseAuth mAuth;
    StorageReference storageReference;
    boolean updatedProfileImage = false;
    String updatedProfilePictureURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance();
        sp = getSharedPreferences("data", 0);
        storageReference = FirebaseStorage.getInstance().getReference();


        getExistingUserInfo(Objects.requireNonNull(mAuth.getCurrentUser()).getUid());

        binding.btnEditProfilePicture.setOnClickListener(v -> {
            // request for permission if not already granted
            requestForPermissions();

            openImageChooser();
        });

        binding.btnDone.setOnClickListener(v -> {
            if (validate()) {
                updateUserInfo(mAuth.getCurrentUser().getUid(), binding.etName.getText().toString(), updatedProfilePictureURL);
            }
        });

        // when clicked on disabled items
        binding.etProfession.setOnClickListener(v -> showDisabledToast("Profession"));

        binding.etGender.setOnClickListener(v -> showDisabledToast("Gender"));

        binding.etPhoneNumber.setOnClickListener(v -> showDisabledToast("Phone Number"));

        // end disabled items click

        // clicked on back
        binding.ivBack.setOnClickListener(v -> onBackPressed());

    }

    private void openImageChooser() {
        // Create an intent with action as ACTION_GET_CONTENT
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        // Sets the type as image/*. This ensures only components of type image are selected
        intent.setType("image/*");
        // We pass an extra array with the accepted mime types. This will ensure only components with these MIME types as targeted.
        String[] mimeTypes = {"image/jpeg", "image/png", "image/jpg"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        // Launching the Intent
        startActivityForResult(intent, SELECT_PICTURE);
    }


    // Override onActivityResult method to handle the image result
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Result code is RESULT_OK only if the user selects an image
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK)
            if (requestCode == SELECT_PICTURE) {
                // Get the Uri of the selected file
                Uri selectedImage = data.getData();
                // Use the uri to display the image
                setProgressForImage(true);
                // Use the uri to upload the image to firebase storage
                uploadImage(selectedImage);
            }
    }


    // Define a method to upload the image to firebase storage
    void uploadImage(Uri uri) {
        // Create a reference to 'images/profile.jpg'
        String profileImageName = mAuth.getUid() + "_profile.jpg";
        StorageReference profileRef = storageReference.child("images/" + profileImageName);
        // Upload the file to firebase storage
        profileRef.putFile(uri)
                .addOnSuccessListener(taskSnapshot -> {
                    // Get the download url of the uploaded file
                    profileRef.getDownloadUrl().addOnSuccessListener(uri1 -> {
                        // Use the url to update the profile image using glide
                        updateProfileImage(uri1.toString());
                    });
                }).addOnFailureListener(exception -> {
                    // Handle unsuccessful uploads
                    AndroidUtils.showAlertDialog(SettingsActivity.this, "Warning", "Upload Failed");
                    AndroidUtils.showToast(getApplicationContext(), "Upload failed");
                });
    }

    private void updateProfileImage(String url) {
        // Load the image from the url into the image view using glide
        setProgressForImage(true);

        updatedProfileImage = true;
        updatedProfilePictureURL = url;

        showCurrentImage(getApplicationContext().getApplicationContext(), url);

        setProgressForImage(false);
    }
    private void requestForPermissions() {
        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 1);
    }

    private void updateUserInfo(String uid, String name, String profilePictureURL) {
        UserModel existingUserModel = FirebaseUtil.getCurrentUserDetails(SettingsActivity.this, uid);
        if(existingUserModel == null){
            String currentUserName, currentUserPhone, currentProfilePictureURL, currentUserGender, currentUserProfession;
            currentUserName = sp.getString("CurrentUserName", "");
            currentUserPhone = sp.getString("CurrentUserPhone", "");
            currentProfilePictureURL = sp.getString("CurrentProfilePictureURL", "");
            currentUserGender = sp.getString("CurrentUserGender", "");
            currentUserProfession = sp.getString("CurrentUserProfession", "");
            existingUserModel = new UserModel(uid,currentProfilePictureURL,currentUserName,currentUserProfession,currentUserGender,currentUserPhone);
        }
        if((!existingUserModel.getName().equals(name)) && (!name.isEmpty()))
            existingUserModel.setName(name);
        if(!existingUserModel.getProfilePictureURL().equals(profilePictureURL))
            existingUserModel.setProfilePictureURL(profilePictureURL);

        UserModel finalExistingUserModel = existingUserModel;
        db.getReference("Users")
                .child(uid)
                .setValue(existingUserModel).addOnCompleteListener(task -> {
                    if(task.isSuccessful()){

                        // update Shared Preference
                        sp.edit()
                                .putString("CurrentUserName", finalExistingUserModel.getName())
                                .putString("CurrentProfilePictureURL", finalExistingUserModel.getProfilePictureURL())
                                .apply();

                        AndroidUtils.showAlertDialog(SettingsActivity.this, "Success", "Profile Updated Successfully");
                        new Handler().postDelayed(() -> {
                            AndroidUtils.dismissCurrentDialog();
                            onBackPressed();
                        }, 2000);
                    }else{
                        AndroidUtils.showAlertDialog(SettingsActivity.this, "Error", Objects.requireNonNull(task.getException()).getMessage());
                    }
            });

    }


    private boolean validate() {
        if(binding.etName.getText().toString().length() == 0){
            binding.etName.setError("Name is Required");
            binding.etName.requestFocus();
            return false;
        }
        return true;
    }

    private void showDisabledToast(String itemClickedLabel) {
        String message = itemClickedLabel + " can be changed while Setting up Account";
        AndroidUtils.showToast(getApplicationContext(), message);
        AndroidUtils.showAlertDialog(SettingsActivity.this, "Warning", message);
    }

    private void getExistingUserInfo(String uid) {
        db.getReference("Users")
                .child(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            UserModel userModel = snapshot.getValue(UserModel.class);
                            if(userModel != null)
                                updateUI(userModel);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        AndroidUtils.showAlertDialog(SettingsActivity.this, "Error", error.getMessage());
                    }
                });
    }


    private void updateUI(UserModel userModel) {
        if(userModel.getProfilePictureURL() != null){
            setProgressForImage(true);
            getExistingProfilePicture(userModel.getProfilePictureURL());
            setProgressForImage(false);
        }
        binding.etName.setText(userModel.getName());
        binding.etProfession.setText(userModel.getProfession());
        binding.etGender.setText(userModel.getGender());
        binding.etPhoneNumber.setText(userModel.getPhoneNumber());

//        AndroidUtils.showAlertDialog(SettingsActivity.this, "Warning", "Network Error, Could not Fetch your Profession");
    }


    private void getExistingProfilePicture(String profilePictureURL) {
        if(!isFinishing()) {
            final Context context = getApplication().getApplicationContext();
            showCurrentImage(context, profilePictureURL);
        }
    }

    private void showCurrentImage(Context context, String profilePictureURL) {
        if (isValidContextForGlide(context)){
            // Load image via Glide lib using context
            Glide.with(binding.getRoot())
                    .load(profilePictureURL)
                    .into(binding.ivProfile);
        }
    }

    private void setProgressForImage(boolean isProgress) {
        if(isProgress){
            binding.ivProfile.setVisibility(View.INVISIBLE);
            binding.pbLoading.setVisibility(View.VISIBLE);
        }else{
            binding.ivProfile.setVisibility(View.VISIBLE);
            binding.pbLoading.setVisibility(View.GONE);
        }
    }

}