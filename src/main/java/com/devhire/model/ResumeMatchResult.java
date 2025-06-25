package com.devhire.model;

import java.util.ArrayList;
import java.util.List;

public class ResumeMatchResult {
    private int matchPercentage;
    private List<String> missingSkills;
    private String suggestions;
    private String targetRole;
    private List<String> courses;
    private String miniProject;
    private List<TimelineItem> timeline;
    private List<String> githubLinks;
    private String filename;
    private String summary;

    // ✅ Default constructor (needed for Jackson, etc.)
    public ResumeMatchResult() {}

    // ✅ Full constructor supporting various call types
    public ResumeMatchResult(
        String filename,
        Number matchPercentage, // allows both int and double
        List<String> missingSkills,
        String suggestions,
        String targetRole,
        String miniProject,
        List<String> courses,
        String summary,
        List<?> timeline,
        List<String> githubLinks
    ) {
        this.filename = filename;
        this.matchPercentage = matchPercentage.intValue(); // safely cast
        this.missingSkills = missingSkills;
        this.suggestions = suggestions;
        this.targetRole = targetRole;
        this.miniProject = miniProject;
        this.courses = courses;
        this.summary = summary;

        this.timeline = new ArrayList<>();
        for (Object item : timeline) {
            if (item instanceof TimelineItem ti) {
                this.timeline.add(ti);
            } else if (item instanceof TimelineEntry entry) {
                TimelineItem ti = new TimelineItem();
                ti.setWeek(entry.getWeek());
                ti.setFocus(entry.getFocus());
                this.timeline.add(ti);
            }
        }

        this.githubLinks = githubLinks;
    }

    // ✅ Getters and Setters

    public int getMatchPercentage() {
        return matchPercentage;
    }

    public void setMatchPercentage(int matchPercentage) {
        this.matchPercentage = matchPercentage;
    }

    public List<String> getMissingSkills() {
        return missingSkills;
    }

    public void setMissingSkills(List<String> missingSkills) {
        this.missingSkills = missingSkills;
    }

    public String getSuggestions() {
        return suggestions;
    }

    public void setSuggestions(String suggestions) {
        this.suggestions = suggestions;
    }

    public String getTargetRole() {
        return targetRole;
    }

    public void setTargetRole(String targetRole) {
        this.targetRole = targetRole;
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

    public List<TimelineItem> getTimeline() {
        return timeline;
    }

    public void setTimeline(List<TimelineItem> timeline) {
        this.timeline = timeline;
    }

    public List<String> getGithubLinks() {
        return githubLinks;
    }

    public void setGithubLinks(List<String> githubLinks) {
        this.githubLinks = githubLinks;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    // ✅ Nested class
    public static class TimelineItem {
        private String week;
        private String focus;

        public String getWeek() {
            return week;
        }

        public void setWeek(String week) {
            this.week = week;
        }

        public String getFocus() {
            return focus;
        }

        public void setFocus(String focus) {
            this.focus = focus;
        }
    }
}
