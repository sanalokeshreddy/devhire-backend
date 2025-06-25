package com.devhire.service;

import com.devhire.model.ResumeMatchResult;
import com.devhire.model.TimelineEntry;
import com.devhire.util.PdfExtractorUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
public class ResumeAnalysisService {

    @Autowired
    private AiResumeMatcherService aiService;

    public List<ResumeMatchResult> analyzeResumes(MultipartFile[] resumes, String jobRole, String jobDescription) {
        List<ResumeMatchResult> results = new ArrayList<>();
        for (MultipartFile resume : resumes) {
            ResumeMatchResult result = analyzeSingleResume(resume, jobDescription);
            result.setFilename(resume.getOriginalFilename());
            result.setTargetRole(jobRole); // Set target role
            results.add(result);
        }
        return results;
    }

    public ResumeMatchResult analyzeSingleResume(MultipartFile resume, String jobDescription) {
        ObjectMapper mapper = new ObjectMapper();

        try {
            String extractedText = PdfExtractorUtil.extractText(resume);

            // AI response
            String cleanJson = aiService.analyzeWithAI(extractedText, "", jobDescription);
            JsonNode jsonResult = mapper.readTree(cleanJson);

            // Skill-based strict scoring
            List<String> requiredSkills = new ArrayList<>();
            for (JsonNode skillNode : jsonResult.path("requiredSkills")) {
                requiredSkills.add(skillNode.asText().toLowerCase());
            }

            List<String> resumeSkills = new ArrayList<>();
            for (JsonNode skillNode : jsonResult.path("resumeSkills")) {
                resumeSkills.add(skillNode.asText().toLowerCase());
            }

            long matchedCount = requiredSkills.stream()
                    .filter(resumeSkills::contains)
                    .count();

            double strictScore = (requiredSkills.isEmpty() ? 0 : (double) matchedCount / requiredSkills.size() * 100);

            // Optional penalties
            List<String> missingSkills = new ArrayList<>();
            for (JsonNode skillNode : jsonResult.path("missingSkills")) {
                String skill = skillNode.asText().toLowerCase();
                missingSkills.add(skill);
                if ("java".equals(skill) || "spring boot".equals(skill)) {
                    strictScore -= 10; // apply penalty
                }
            }
            strictScore = Math.max(0, strictScore);

            // Suggestions and other fields
            String suggestions = jsonResult.path("suggestions").asText();
            String summaryPrompt = "Summarize this resume:\n" + extractedText;
            String summary = aiService.getSummary(summaryPrompt);
            String targetRole = jsonResult.path("targetRole").asText();

            List<String> courses = new ArrayList<>();
            for (JsonNode course : jsonResult.path("courses")) {
                courses.add(course.asText());
            }

            String miniProject = jsonResult.path("miniProject").asText();

            List<TimelineEntry> timeline = new ArrayList<>();
            for (JsonNode item : jsonResult.path("timeline")) {
                timeline.add(new TimelineEntry(
                        item.get("week").asText(),
                        item.get("focus").asText()
                ));
            }

            List<String> githubLinks = new ArrayList<>();
            for (JsonNode link : jsonResult.path("githubLinks")) {
                githubLinks.add(link.asText());
            }

            return new ResumeMatchResult(
                    resume.getOriginalFilename(),
                    strictScore,
                    missingSkills,
                    suggestions,
                    summary,
                    targetRole,
                    courses,
                    miniProject,
                    timeline,
                    githubLinks
            );

        } catch (Exception e) {
            e.printStackTrace();
            return new ResumeMatchResult(
                    resume.getOriginalFilename(),
                    0,
                    List.of("AI Error"),
                    "Failed to analyze resume: " + e.getMessage(),
                    "No summary available due to error.",
                    "",
                    new ArrayList<>(),
                    "",
                    new ArrayList<>(),
                    new ArrayList<>()
            );
        }
    }
}
