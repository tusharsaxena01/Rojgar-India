package com.bit.bharatplus.activities;

import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bit.bharatplus.databinding.ActivityJobPostBinding;
import com.bit.bharatplus.models.JobModel;
import com.bit.bharatplus.utils.AndroidUtils;
import com.bit.bharatplus.utils.FirebaseUtil;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class JobPostActivity extends AppCompatActivity {
    ActivityJobPostBinding binding;

    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityJobPostBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("jobs");

        binding.ivBack.setOnClickListener(v -> onBackPressed());

        // Set click listener for postJobButton
        binding.btnPost.setOnClickListener(view -> {
            // Get the input values
            String title = binding.etJobTitle.getText().toString().trim();
            String desc = binding.etJobDesc.getText().toString().trim();

            // Get the current time
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
            String currentTime = dateFormat.format(calendar.getTime());


            // Check if any of the fields are empty
            if (title.isEmpty() || desc.isEmpty()) {
                Toast.makeText(JobPostActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                AndroidUtils.showAlertDialog(JobPostActivity.this, "Warning", "Please fill in all fields");
            } else {
                try{// Generate a unique key for the job entry in the database
                    String jobId = databaseReference.push().getKey();

                    // Create a new instance of the Job class
                    JobModel job = new JobModel(jobId, title, desc, currentTime, FirebaseUtil.getCurrentUserId());

                    // Save the job entry to the database
                    assert jobId != null;
                    databaseReference.child(jobId).setValue(job).addOnCompleteListener(task -> {
                        if(!task.isSuccessful())
                            AndroidUtils.showAlertDialog(JobPostActivity.this, "Error", task.getException().getMessage());
                        else{
                            Toast.makeText(JobPostActivity.this, "Job posted successfully", Toast.LENGTH_SHORT).show();
                            AndroidUtils.showAlertDialog(JobPostActivity.this, "Success", "Job Posted Successfully");
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    AndroidUtils.dismissCurrentDialog();
                                }
                            }, 2000);
                        }
                    });


                    // Clear the input fields
                    binding.etJobTitle.setText("");
                    binding.etJobDesc.setText("");
                }catch (Exception e){
                    AndroidUtils.showAlertDialog(JobPostActivity.this, "Error",e.getLocalizedMessage());
                }
            }
        });
    }
}
