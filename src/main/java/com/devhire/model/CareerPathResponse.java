package com.devhire.model;

import lombok.Data;
import java.util.List;

@Data
public class CareerPathResponse {
    private String targetRole;
    private List<String> missingSkills;
    private List<String> courses;
    private String miniProject;
    private List<TimelineStep> timeline;
    private List<String> githubLinks;
}