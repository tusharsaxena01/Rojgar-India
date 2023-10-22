package com.bit.bharatplus.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.bit.bharatplus.adapters.JobsAdapter;
import com.bit.bharatplus.databinding.ActivityButtonClickHomeBinding;
import com.bit.bharatplus.models.JobModel;
import com.bit.bharatplus.utils.AndroidUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ButtonClickHomeActivity extends AppCompatActivity {

    private ActivityButtonClickHomeBinding binding;
    JobsAdapter jobsAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityButtonClickHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.ivBack.setOnClickListener(v -> onBackPressed());

        Intent oldIntent = getIntent();
        String pageName = oldIntent.getStringExtra("name");
        if(pageName.equals("jobs")){
            // setup everything for jobs
            setupForJobs();
        }
        if(pageName.equals("workers")){
            // setup everything for workers
            setupForWorkers();
        }
        if(pageName.equals("profession")){
            // setup everything for professions
            setupForProfessions(oldIntent.getStringExtra("Profession name"));
        }

        binding.dummy.setOnClickListener(v -> dialPhone(binding.postedBy.getText().toString()));

    }

    private void setupForProfessions(String profession_name) {
        binding.jobTitle.setText("Manoj");
        binding.jobDesc.setText(profession_name);
        binding.jobTime.setVisibility(View.GONE);
    }

    private void dialPhone(String phone) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel: "+ phone));
        if(intent.resolveActivity(getPackageManager()) != null){
            startActivity(intent);
        }else{
            startActivity(intent);
        }
    }

    private void setupForWorkers() {
        binding.tvHeader.setText("Workers");

        List<JobModel> jobsList = new ArrayList<>();
        jobsAdapter = new JobsAdapter(jobsList);
        binding.recycler.setAdapter(jobsAdapter);

        // Fetch available jobs from the Firebase Realtime Database
        DatabaseReference jobsRef = FirebaseDatabase.getInstance().getReference("jobs");
        jobsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                jobsList.clear();

                for (DataSnapshot jobSnapshot : dataSnapshot.getChildren()) {
                    String jobId = jobSnapshot.getKey();
                    JobModel job = jobSnapshot.getValue(JobModel.class);
                    assert job != null;
                    job.setJobId(jobId);
                    jobsList.add(job);
                    Log.d("job", job.toString());
                }

                jobsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database errors if any
                Log.e("Jobs", "Database Error: " + databaseError.getMessage());
            }
        });

        if(jobsList.isEmpty()){
            AndroidUtils.showAlertDialog(ButtonClickHomeActivity.this,"Warning", "Unable to connect to server");
        }else{
            binding.dummy.setVisibility(View.GONE);
            binding.tvMessage.setVisibility(View.GONE);
            binding.recycler.setVisibility(View.VISIBLE);
        }


        binding.jobTitle.setText("Manoj");
        binding.jobDesc.setText("Car Mechanic");
        binding.jobTime.setVisibility(View.GONE);
    }

    private void setupForJobs() {
        binding.tvHeader.setText("Jobs");
        List<JobModel> jobsList = new ArrayList<>();
        jobsAdapter = new JobsAdapter(jobsList);
        binding.recycler.setAdapter(jobsAdapter);

        // Fetch available jobs from the Firebase Realtime Database
        DatabaseReference jobsRef = FirebaseDatabase.getInstance().getReference("jobs");
        jobsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                jobsList.clear();

                for (DataSnapshot jobSnapshot : dataSnapshot.getChildren()) {
                    String jobId = jobSnapshot.getKey();
                    JobModel job = jobSnapshot.getValue(JobModel.class);
                    assert job != null;
                    job.setJobId(jobId);
                    jobsList.add(job);
                    Log.d("job", job.toString());
                }

                jobsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database errors if any
                Log.e("Jobs", "Database Error: " + databaseError.getMessage());
            }
        });

        if(jobsList.isEmpty()){
            AndroidUtils.showAlertDialog(ButtonClickHomeActivity.this, "Warning", "Unable to connect to server");
        }else{
            binding.dummy.setVisibility(View.GONE);
            binding.tvMessage.setVisibility(View.GONE);
            binding.recycler.setVisibility(View.VISIBLE);
        }

    }
}