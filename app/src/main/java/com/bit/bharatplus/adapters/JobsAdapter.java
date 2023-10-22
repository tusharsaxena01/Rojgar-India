package com.bit.bharatplus.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bit.bharatplus.R;
import com.bit.bharatplus.models.JobModel;

import java.util.List;

public class JobsAdapter extends RecyclerView.Adapter<JobsAdapter.JobViewHolder> {
    Context context;
    private List<JobModel> jobList;

    public JobsAdapter(List<JobModel> jobList) {
        this.jobList = jobList;
    }

    @NonNull
    @Override
    public JobViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_job_layout, parent, false);
        return new JobViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull JobViewHolder holder, int position) {
        JobModel job = jobList.get(position);

        holder.jobTitle.setText(job.getJobTitle());
        holder.jobDesc.setText(job.getJobDescription());
        holder.jobTime.setText(job.getJobTimeStamp());
        holder.postedBy.setText(job.getJobPostedBy());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = job.getJobPostedBy();
                dialPhone(phone);
            }
        });

    }

    private void dialPhone(String phone) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel: "+ phone));
        if(intent.resolveActivity(context.getPackageManager()) != null){
            context.startActivity(intent);
        }
    }


    @Override
    public int getItemCount() {
        return jobList.size();
    }

    public static class JobViewHolder extends RecyclerView.ViewHolder {
        TextView jobTitle, jobDesc, postedBy, jobTime;
        public JobViewHolder(@NonNull View itemView) {
            super(itemView);
            jobTitle = itemView.findViewById(R.id.jobTitle);
            jobDesc = itemView.findViewById(R.id.jobDesc);
            postedBy = itemView.findViewById(R.id.postedBy);
            jobTime = itemView.findViewById(R.id.jobTime);

        }
    }
}
