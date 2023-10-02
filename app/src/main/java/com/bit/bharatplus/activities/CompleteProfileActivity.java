package com.bit.bharatplus.activities;

import static android.R.color.transparent;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bit.bharatplus.R;
import com.bit.bharatplus.classes.UserModel;
import com.bit.bharatplus.databinding.ActivityCompleteProfileBinding;
import com.bit.bharatplus.databinding.DialogConfirmBinding;
import com.bit.bharatplus.utils.AndroidUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.manager.RequestManagerRetriever;
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
    public static final int SELECT_PICTURE = 200;
    FirebaseAuth mAuth;
    FirebaseDatabase db;
    StorageReference storageReference;
    SharedPreferences sp;
    ActivityCompleteProfileBinding binding;
    List<String> professions = new ArrayList<>();
    List<String> genders = new ArrayList<>();
    String currentImageURL;
    boolean uploadedImage = false;



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

        // disable submit button if fields not completed
        binding.btnSubmit.setEnabled(false);
        // setting text color to white
        int greyColor = getApplicationContext().getResources().getColor(R.color.grey, getTheme());
        binding.btnSubmit.setTextColor(greyColor);
        // setup phone number from shared preference
        binding.etPhoneNumber.setText(sp.getString("phone", "Error"));

        // setting up spinner for gender
        genders.add("Male");
        genders.add("Female");
        ArrayAdapter<String> genderAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, genders);
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.etGender.setAdapter(genderAdapter);

        // setting up spinner for professions
//        professions = new ArrayList<>();
        professions.add("Loading...");
        ArrayAdapter<String> professionAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, professions);
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

        userUserIfAlreadyExists(mAuth.getCurrentUser().getUid());


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

                String message = "Do you want to Logout?";
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
                if(!binding.btnSubmit.isEnabled()){
                    AndroidUtils.showAlertDialog(CompleteProfileActivity.this, "Warning", "All Fields are Required");
                    return;
                }
                if(validate()) {
                    saveUser(mAuth.getUid());
                    sp.edit()
                            .putString("CurrentUserName", binding.etName.getText().toString())
                            .putString("CurrentUserPhone", binding.etPhoneNumber.getText().toString())
                            .putString("CurrentProfilePictureURL", currentImageURL)
                            .putBoolean("profileCompleted", true).apply();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            startActivity(new Intent(CompleteProfileActivity.this, NavigationActivity.class));
                            finish();
                        }
                    },2000);

                }else{
                    AndroidUtils.showToast(getApplicationContext(), "Complete all Fields");
                }
            }
        });

    }

    private void userUserIfAlreadyExists(String uid) {
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
                        AndroidUtils.showAlertDialog(CompleteProfileActivity.this, "Error", error.getMessage());
                    }
                });
    }

    private void updateUI(UserModel userModel) {
        if(userModel.getProfilePictureURL() != null){
            updateProfileImage(userModel.getProfilePictureURL());
        }
        binding.etName.setText(userModel.getName());
//        binding.etProfession.setSelection(professions.indexOf(userModel.getProfession()));
        binding.etGender.setSelection(genders.indexOf(userModel.getGender()));
        AndroidUtils.showAlertDialog(CompleteProfileActivity.this, "Warning", "Network Error, Could not Fetch your Profession");
    }

    private boolean validate() {
        if(binding.etName.getText().toString().length() == 0){
            binding.etName.setError("Name is Required");
            binding.etName.requestFocus();
            return false;
        }
        if(!uploadedImage){
            AndroidUtils.showAlertDialog(CompleteProfileActivity.this, "Warning", "Profile Picture is required");
            binding.ivProfile.requestFocus();
            return false;
        }
        return true;
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
                setProgressForImage(true);
//                binding.ivProfile.setImageURI(selectedImage);
                // Use the uri to upload the image to firebase storage
                uploadImage(selectedImage);
            }
    }

    // Define a method to upload the image to firebase storage
    void uploadImage(Uri uri) {
        // Create a reference to 'images/profile.jpg'
        String profileImageName = mAuth.getUid() +"_profile.jpg";
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
                        AndroidUtils.showAlertDialog(CompleteProfileActivity.this, "Warning", "Upload Failed");
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
            Glide.with(getApplicationContext())
                    .load(url)
                    .centerCrop()
                    .into(binding.ivProfile);
            currentImageURL = url;
            uploadedImage = true;
            binding.btnSubmit.setEnabled(true);
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//                binding.btnSubmit.setBackgroundTintBlendMode(BlendMode.MULTIPLY);
//            }
            // setting text color to white
            int whiteColor = context.getResources().getColor(R.color.white, getTheme());
            binding.btnSubmit.setTextColor(whiteColor);
        }

        setProgressForImage(false);
    }

    public static boolean isValidContextForGlide(final Context context) {
        if (context == null) {
            return false;
        }
        if (context instanceof Activity) {
            final Activity activity = (Activity) context;
            return !activity.isDestroyed() && !activity.isFinishing();
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
        UserModel userModel = new UserModel(uid, profilePictureURL, name, profession, gender, phoneNumber);
        db.getReference("Users")
                .child(uid)
                .setValue(userModel)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                            AndroidUtils.showAlertDialog(CompleteProfileActivity.this, "Success", "User Created Successfully");
                        else {
                            AndroidUtils.showAlertDialog(CompleteProfileActivity.this, "Error", "Internet Error");
                            AndroidUtils.showToast(getApplicationContext(), "Check your Internet or Try Again Later");
                        }
                    }
                });


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Glide.with(getApplicationContext()).clear(binding.ivProfile);
    }
}