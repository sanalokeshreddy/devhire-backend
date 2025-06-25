package com.devhire.service;

import com.devhire.model.*;
import com.devhire.util.GeminiAPI;
import com.devhire.util.PdfExtractorUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CareerPathService {

    private final GeminiAPI geminiAPI;
    private final ObjectMapper mapper = new ObjectMapper();

    public CareerPathResponse createRoadmap(CareerPathRequest req, MultipartFile resumePdf) {
        String resumeText = extractResumeText(resumePdf);
        String prompt = buildPrompt(req, resumeText);

        String rawJson;
        try {
            rawJson = geminiAPI.generateJSON(prompt);
        } catch (Exception ex) {
            log.error("Gemini call failed", ex);
            throw new RuntimeException("Error generating career roadmap", ex);
        }

        try {
            JsonNode geminiNode = mapper.readTree(rawJson);
            CareerPathResponse resp = convertToResponse(geminiNode);

            // ✅ Fix: Set missing UI values
            resp.setTimeframe(safe(req.getTimeframe()));       // Used for display in Insights
            resp.setProgress(0);                               // You can change this dynamically later
            return resp;

        } catch (Exception e) {
            log.error("Failed to parse Gemini JSON", e);
            throw new RuntimeException("Gemini response parse error", e);
        }
    }

    private String extractResumeText(MultipartFile resumePdf) {
        if (resumePdf == null || resumePdf.isEmpty()) return "";
        try {
            return PdfExtractorUtil.extractText(resumePdf);
        } catch (Exception e) {
            log.warn("Could not extract résumé text – continuing without it", e);
            return "";
        }
    }

    private String buildPrompt(CareerPathRequest r, String resumeText) {
        String tfLabel = switch (safe(r.getTimeframe())) {
            case "3months" -> "3-month intensive";
            case "6months" -> "6-month balanced";
            case "1year"   -> "1-year comprehensive";
            case "2years"  -> "2-year mastery";
            default        -> "flexible";
        };

        return """
            You are a professional AI career mentor.

            ===== Candidate profile =====
            • Target role: %s
            • Current skills: %s
            • Experience level: %s
            • Preferred timeframe: %s
            • Areas of interest: %s

            ===== Résumé (may be empty) =====
            %s
            ===== End résumé =====

            Return ONLY valid JSON (no markdown), structure:
            {
              "targetRole": "Full Stack Developer",
              "matchPercentage": 0-100,
              "missingSkills": ["skill1", "skill2"],
              "courses": ["Course A", "Course B"],
              "miniProject": "Build XYZ app using ABC stack",
              "timeline": [
                { "week": "Week 1", "focus": "Learn Spring Boot" },
                { "week": "Week 2", "focus": "Build REST API" }
              ],
              "githubLinks": ["https://github.com/user/project1"]
            }
            """.formatted(
                safe(r.getRole()),
                safe(r.getSkills()),
                safe(r.getExperience()),
                tfLabel,
                safe(String.join(", ", r.getInterests() == null ? List.of() : r.getInterests())),
                resumeText == null ? "" : resumeText
            );
    }

    private CareerPathResponse convertToResponse(JsonNode n) {
        CareerPathResponse resp = new CareerPathResponse();

        resp.setTargetRole(n.path("targetRole").asText());
        resp.setMatchPercentage(n.path("matchPercentage").asDouble(0)); // ✅ Dynamic match %

        List<String> missing = new ArrayList<>();
        n.path("missingSkills").forEach(ms -> missing.add(ms.asText()));
        resp.setMissingSkills(missing);

        List<String> courses = new ArrayList<>();
        n.path("courses").forEach(c -> courses.add(c.asText()));
        resp.setCourses(courses);

        resp.setMiniProject(n.path("miniProject").asText());

        List<TimelineEntry> steps = new ArrayList<>();
        n.path("timeline").forEach(t -> steps.add(
                new TimelineEntry(t.path("week").asText(), t.path("focus").asText())));
        resp.setTimeline(steps);

        List<String> links = new ArrayList<>();
        n.path("githubLinks").forEach(g -> links.add(g.asText()));
        resp.setGithubLinks(links);

        return resp;
    }

    private static String safe(String s) {
        return s == null ? "" : s.trim();
    }
}
