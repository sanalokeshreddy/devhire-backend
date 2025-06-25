package com.devhire.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class AiResumeMatcherService {

    @Value("${gemini.api.key}")
    private String apiKey;

    private static final String GEMINI_API_URL =
        "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=";

    private final ObjectMapper objectMapper = new ObjectMapper();

    public String analyzeWithAI(String resumeText, String jobRole, String jobDescription) throws IOException {
        String targetText = (jobDescription != null && !jobDescription.trim().isEmpty())
            ? "Job Description for the role '" + jobRole + "':\n" + jobDescription
            : "Target Job Role: " + jobRole;

        String prompt = String.format("""
            You are an experienced technical recruiter. Assess the following resume against a job description.
            Match score should reflect:
            - ✅ Partial matches (e.g., "Java Spring" ~ "Spring Boot")
            - ✅ Closely related experience or transferable skills
            - ✅ Projects or certifications even if exact terms aren't used
            - 🚫 Do NOT give low score just because all keywords don't match.

            Use this scoring guide:
            - 90-100: Excellent match, highly aligned with role
            - 75-89: Very good match, only minor gaps
            - 60-74: Good match, a few key gaps
            - 45-59: Moderate match, requires moderate upskilling
            - 30-44: Low match, many missing skills
            - 0-29: Minimal alignment, major gaps

            Consider:
            - Technical skills (40%%)
            - Work/Project experience (30%%)
            - Education/certifications (20%%)
            - Open source or GitHub work (10%%)

            Job Info:
            %s

            -------------------------
            Resume:
            -------------------------
            %s

            Respond in pure JSON:
            {
              "matchPercentage": 0-100,
              "missingSkills": ["skill1", "skill2"],
              "suggestions": "Short advice on improvement",
              "targetRole": "Full Stack Developer",
              "courses": ["Course A", "Course B"],
              "miniProject": "Small project idea",
              "timeline": [
                { "week": "Week 1", "focus": "Topic A" },
                { "week": "Week 2", "focus": "Topic B" }
              ],
              "githubLinks": ["https://github.com/user/project1"],
              "resumeSkills": ["Java", "Spring", "Docker"],
              "requiredSkills": ["Java", "Spring Boot", "AWS"]
            }
        """, targetText, resumeText);

        OkHttpClient client = new OkHttpClient();
        MediaType mediaType = MediaType.parse("application/json");

        String requestBody = """
        {
          "contents": [
            {
              "role": "user",
              "parts": [
                {
                  "text": %s
                }
              ]
            }
          ]
        }
        """.formatted(toJson(prompt));

        Request request = new Request.Builder()
            .url(GEMINI_API_URL + apiKey)
            .post(RequestBody.create(requestBody, mediaType))
            .addHeader("Content-Type", "application/json")
            .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Gemini API failed: " + response.code() + " " + response.message());
            }

            String rawJson = response.body().string();
            JsonNode root = objectMapper.readTree(rawJson);
            return sanitizeResponse(root
                .path("candidates")
                .get(0)
                .path("content")
                .path("parts")
                .get(0)
                .path("text")
                .asText());
        }
    }

    public String getSummary(String resumeText) {
        String prompt = """
        Summarize the resume in 3-4 bullet points:
        - Technical strengths
        - Projects or experience
        - Achievements
        - Technologies mentioned

        No markdown, no formatting.

        Resume:
        -------------------------
        %s
        -------------------------
        """.formatted(resumeText);

        OkHttpClient client = new OkHttpClient();
        MediaType mediaType = MediaType.parse("application/json");

        String requestBody = """
        {
          "contents": [
            {
              "role": "user",
              "parts": [
                {
                  "text": %s
                }
              ]
            }
          ]
        }
        """.formatted(toJson(prompt));

        Request request = new Request.Builder()
            .url(GEMINI_API_URL + apiKey)
            .post(RequestBody.create(requestBody, mediaType))
            .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                return "Summary generation failed: " + response.message();
            }

            String rawJson = response.body().string();
            JsonNode root = objectMapper.readTree(rawJson);
            return sanitizeResponse(root
                .path("candidates")
                .get(0)
                .path("content")
                .path("parts")
                .get(0)
                .path("text")
                .asText());
        } catch (Exception e) {
            return "Summary generation error: " + e.getMessage();
        }
    }

    private static String toJson(String text) {
        return "\"" + text.replace("\\", "\\\\")
                          .replace("\"", "\\\"")
                          .replace("\n", "\\n")
                          .replace("\r", "") + "\"";
    }

    private static String sanitizeResponse(String raw) {
        return raw.replaceAll("```json", "")
                  .replaceAll("```", "")
                  .trim();
    }
}
