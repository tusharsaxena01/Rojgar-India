package com.bit.bharatplus.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.bit.bharatplus.R;
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

    ActivityButtonClickHomeBinding binding;
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
//            setupForWorkers();
        }

    }

    private void setupForJobs() {
        binding.tvHeader.setText("Jobs");
        List<JobModel> jobsList = new ArrayList<JobModel>();
        jobsAdapter = new JobsAdapter(jobsList);
        binding.recycler.setAdapter(jobsAdapter);

        // Fetch available jobs from the Firebase Realtime Database
        DatabaseReference jobsRef = FirebaseDatabase.getInstance().getReference("jobs");
        jobsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                jobsList.clear();

                for (DataSnapshot jobSnapshot : dataSnapshot.getChildren()) {
                    JobModel job = jobSnapshot.getValue(JobModel.class);
                    jobsList.add(job);
                }

                jobsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database errors if any
                AndroidUtils.showAlertDialog(ButtonClickHomeActivity.this, "Error", databaseError.getMessage());
            }
        });

        if(jobsList.isEmpty()){
            binding.tvMessage.setText("No Openings available currently");
            binding.tvMessage.setVisibility(View.VISIBLE);
            binding.recycler.setVisibility(View.GONE);
        }else{
            binding.tvMessage.setVisibility(View.GONE);
            binding.recycler.setVisibility(View.VISIBLE);
        }

    }
}