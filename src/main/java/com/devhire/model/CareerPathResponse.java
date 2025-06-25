package com.devhire.model;

import java.util.List;

public class CareerPathResponse {

    private String targetRole;
    private List<String> missingSkills;
    private List<String> courses;
    private String miniProject;
    private List<TimelineEntry> timeline;
    private List<String> githubLinks;

    // ✅ UI enhancement fields
    private double matchPercentage;
    private int progress;
    private String timeframe;

    public String getTargetRole() {
        return targetRole;
    }

    public void setTargetRole(String targetRole) {
        this.targetRole = targetRole;
    }

    public List<String> getMissingSkills() {
        return missingSkills;
    }

    public void setMissingSkills(List<String> missingSkills) {
        this.missingSkills = missingSkills;
    }

    public List<String> getCourses() {
        return courses;
    }

    public void setCourses(List<String> courses) {
        this.courses = courses;
    }

    public String getMiniProject() {
        return miniProject;
    }

    public void setMiniProject(String miniProject) {
        this.miniProject = miniProject;
    }

    public List<TimelineEntry> getTimeline() {
        return timeline;
    }

    public void setTimeline(List<TimelineEntry> timeline) {
        this.timeline = timeline;
    }

    public List<String> getGithubLinks() {
        return githubLinks;
    }

    public void setGithubLinks(List<String> githubLinks) {
        this.githubLinks = githubLinks;
    }

    public double getMatchPercentage() {
        return matchPercentage;
    }

    public void setMatchPercentage(double matchPercentage) {
        this.matchPercentage = matchPercentage;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) { // ✅ REQUIRED FOR CareerPathService
        this.progress = progress;
    }

    public String getTimeframe() {
        return timeframe;
    }

    public void setTimeframe(String timeframe) {
        this.timeframe = timeframe;
    }
}
