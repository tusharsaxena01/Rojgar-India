package com.bit.bharatplus.models;


public class JobModel {
    private String jobId;
    private String jobTitle;
    private String jobDescription;
    private String jobTimeStamp;
    private String jobPostedBy;
    private boolean isCompleted;

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    public JobModel() {
    }

    public JobModel(boolean isCompleted, String jobDescription, String jobId, String jobPostedBy, String jobTitle) {
        this.isCompleted = isCompleted;
        this.jobId = jobId;
        this.jobTitle = jobTitle;
        this.jobDescription = jobDescription;
        this.jobPostedBy = jobPostedBy;
    }

    @Override
    public String toString() {
        return "JobModel{" +
                "jobId='" + jobId + '\'' +
                ", jobTitle='" + jobTitle + '\'' +
                ", jobDescription='" + jobDescription + '\'' +
                ", jobTimeStamp='" + jobTimeStamp + '\'' +
                ", jobPostedBy='" + jobPostedBy + '\'' +
                ", isCompleted=" + isCompleted +
                '}';
    }

    public JobModel(String jobId, String jobTitle, String jobDescription, String jobTimeStamp, String jobPostedBy) {
        this.jobId = jobId;
        this.jobTitle = jobTitle;
        this.jobDescription = jobDescription;
        this.jobTimeStamp = jobTimeStamp;
        this.jobPostedBy = jobPostedBy;
    }

    public JobModel(String jobId, String jobTitle, String jobDescription, String jobPostedBy) {
        this.jobId = jobId;
        this.jobTitle = jobTitle;
        this.jobDescription = jobDescription;
        this.jobPostedBy = jobPostedBy;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getJobDescription() {
        return jobDescription;
    }

    public void setJobDescription(String jobDescription) {
        this.jobDescription = jobDescription;
    }

    public String getJobTimeStamp() {
        return jobTimeStamp;
    }

    public void setJobTimeStamp(String jobTimeStamp) {
        this.jobTimeStamp = jobTimeStamp;
    }

    public String getJobPostedBy() {
        return jobPostedBy;
    }

    public void setJobPostedBy(String jobPostedBy) {
        this.jobPostedBy = jobPostedBy;
    }
}
