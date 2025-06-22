package com.devhire.model;

public class ResumeRequest {
    private String resumeText;
    private String jobRole;

    // ✅ Getters and Setters
    public String getResumeText() {
        return resumeText;
    }

    public void setResumeText(String resumeText) {
        this.resumeText = resumeText;
    }

    public String getJobRole() {
        return jobRole;
    }

    public void setJobRole(String jobRole) {
        this.jobRole = jobRole;
    }
}
