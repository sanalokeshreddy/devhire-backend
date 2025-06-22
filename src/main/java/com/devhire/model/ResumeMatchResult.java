package com.devhire.model;

import java.util.List;

public class ResumeMatchResult {

    private String fileName;
    private double matchPercentage;
    private List<String> missingSkills;
    private String suggestions;
    private String summary; // ✅ New field

    public ResumeMatchResult() {}

    public ResumeMatchResult(String fileName, double matchPercentage, List<String> missingSkills, String suggestions, String summary) {
        this.fileName = fileName;
        this.matchPercentage = matchPercentage;
        this.missingSkills = missingSkills;
        this.suggestions = suggestions;
        this.summary = summary;
    }

    // Getters
    public String getFileName() {
        return fileName;
    }

    public double getMatchPercentage() {
        return matchPercentage;
    }

    public List<String> getMissingSkills() {
        return missingSkills;
    }

    public String getSuggestions() {
        return suggestions;
    }

    public String getSummary() {
        return summary;
    }
    
    // Setters
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setMatchPercentage(double matchPercentage) {
        this.matchPercentage = matchPercentage;
    }

    public void setMissingSkills(List<String> missingSkills) {
        this.missingSkills = missingSkills;
    }

    public void setSuggestions(String suggestions) {
        this.suggestions = suggestions;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }
}
