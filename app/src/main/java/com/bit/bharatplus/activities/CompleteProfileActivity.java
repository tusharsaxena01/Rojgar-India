package com.bit.bharatplus.activities;

import static android.R.color.transparent;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bit.bharatplus.R;
import com.bit.bharatplus.User;
import com.bit.bharatplus.databinding.ActivityCompleteProfileBinding;
import com.bit.bharatplus.databinding.DialogConfirmBinding;
import com.bit.bharatplus.utils.AndroidUtils;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;


public class CompleteProfileActivity extends AppCompatActivity {
    FirebaseAuth mAuth;
    FirebaseDatabase db;
    StorageReference storageReference;
    SharedPreferences sp;

    ActivityCompleteProfileBinding binding;
    List<String> professions;
    String[] genders = {"Male", "Female"};
    public static final int SELECT_PICTURE = 200;
    String currentImageURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCompleteProfileBinding.inflate(getLayoutInflater());

        // get permission for read, write storage and camera
        requestForPermissions();

        // normal flow
        setContentView(binding.getRoot());

        sp = getSharedPreferences("data", 0);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        DatabaseReference professionRef = db.getReference().child("Professions");

        // setup phone number from shared preference
        binding.etPhoneNumber.setText(sp.getString("phone", "Error"));

        // setting up spinner for gender
        ArrayAdapter genderAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, genders);
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.etGender.setAdapter(genderAdapter);

        // setting up spinner for professions
        professions = new ArrayList<>();
        professions.add("Loading...");
        ArrayAdapter professionAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, professions);
        professionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.etProfession.setAdapter(professionAdapter);
        // fetching professions from the server
        professionRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                professions.clear();

                for (DataSnapshot professionSnapshot : snapshot.getChildren()) {
                    String profession = professionSnapshot.getKey();
                    professions.add(profession);
                }

                // Notify the adapter that the data has changed
                professionAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                AndroidUtils.showAlertDialog(CompleteProfileActivity.this, "Error", "Error Fetching Data from Server");
            }
        });

        // if clicked on phone
        binding.etPhoneNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AndroidUtils.showToast(getApplicationContext(), "Phone Number cannot be modified after authentication");
            }
        });

        // update profile picture
        binding.btnEditProfilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // request for permission if not already granted
                requestForPermissions();

                openImageChooser();
            }
        });

        // when clicked on close
        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(CompleteProfileActivity.this);
                DialogConfirmBinding confirmDialogBinding = DialogConfirmBinding.inflate(LayoutInflater.from(CompleteProfileActivity.this));
                builder.setView(confirmDialogBinding.getRoot());

                String message = "Do you want to Exit?";
                confirmDialogBinding.tvMessage.setText(message);
                AlertDialog dialog = builder.create();
                dialog.getWindow().setBackgroundDrawableResource(transparent);

                confirmDialogBinding.btnYes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                        mAuth.signOut();
                        finish();
                    }
                });

                confirmDialogBinding.btnNo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        });

        // when clicked on submit
        binding.btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validate()) {
                    saveUser(mAuth.getUid());
                    startActivity(new Intent(CompleteProfileActivity.this, MainActivity.class));
                    finishActivity(0);
                }else{
                    binding.etName.setError("Invalid Details");
                    binding.etName.requestFocus();
                }
            }
        });

    }


    private boolean validate() {
        return binding.etName.getText().toString().length() > 0;
    }

    private void openImageChooser() {
        // Create an intent with action as ACTION_GET_CONTENT
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        // Sets the type as image/*. This ensures only components of type image are selected
        intent.setType("image/*");
        // We pass an extra array with the accepted mime types. This will ensure only components with these MIME types as targeted.
        String[] mimeTypes = {"image/jpeg", "image/png", "image/jpg"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES,mimeTypes);
        // Launching the Intent
        startActivityForResult(intent,SELECT_PICTURE);
    }

    // Override onActivityResult method to handle the image result
    @Override
    public void onActivityResult(int requestCode,int resultCode,Intent data) {
        // Result code is RESULT_OK only if the user selects an image
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK)
            if (requestCode == SELECT_PICTURE) {// Get the Uri of the selected file
                Uri selectedImage = data.getData();
                // Use the uri to display the image
                binding.ivProfile.setImageURI(selectedImage);
                // Use the uri to upload the image to firebase storage
                uploadImage(selectedImage);
            }
    }

    // Define a method to upload the image to firebase storage
    void uploadImage(Uri uri) {
        // Create a reference to 'images/profile.jpg'
        String profileImageName = mAuth.getUid().toString()+"_profile.jpg";
        StorageReference profileRef = storageReference.child("images/"+profileImageName);
        // Upload the file to firebase storage
        profileRef.putFile(uri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get the download url of the uploaded file
                        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                // Use the url to update the profile image using glide
                                updateProfileImage(uri.toString());
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        AndroidUtils.showToast(getApplicationContext(), "Upload failed");
                    }
                });
    }
    // Define a method to update the profile image using glide framework
    void updateProfileImage(String url) {
        // Load the image from the url into the image view using glide
        setProgressForImage(true);
        final Context  context = getApplication().getApplicationContext();

        if (isValidContextForGlide(context)){
            // Load image via Glide lib using context
            Glide.with(this)
                    .load(url)
                    .centerCrop()
                    .into(binding.ivProfile);
            currentImageURL = url;
        }


        setProgressForImage(false);
    }
    public static boolean isValidContextForGlide(final Context context) {
        if (context == null) {
            return false;
        }
        if (context instanceof Activity) {
            final Activity activity = (Activity) context;
            if (activity.isDestroyed() || activity.isFinishing()) {
                return false;
            }
        }
        return true;
    }

    private void setProgressForImage(boolean isProgress) {
        if(isProgress){
            binding.pbLoading.setVisibility(View.VISIBLE);
        }else{
            binding.pbLoading.setVisibility(View.GONE);
        }
    }

    private void requestForPermissions() {
        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 1);
    }

    private void saveUser(String uid) {
        assert uid != null;
        String profilePictureURL = currentImageURL;
        String name = binding.etName.getText().toString();
        String profession = binding.etProfession.getSelectedItem().toString();
        String gender = binding.etGender.getSelectedItem().toString();
        String phoneNumber = sp.getString("phone", "9876543210");
        Log.e("check", "profession: "+profession+" gender: "+gender);
        User user = new User(uid, profilePictureURL, name, profession, gender, phoneNumber);
        db.getReference("Users")
                .setValue(user)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                            sp.edit()
                                    .putBoolean("profileCompleted", true)
                                    .apply();
                        else {
                            AndroidUtils.showAlertDialog(CompleteProfileActivity.this, "Error", "Internet Error");
                            AndroidUtils.showToast(getApplicationContext(), "Check your Internet or Try Again Later");
                        }
                    }
                });


    }
}