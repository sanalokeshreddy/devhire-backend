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
            You are an expert HR.
            Analyze the candidate's resume against the following target:

            %s

            -------------------------
            Resume:
            -------------------------
            %s
            -------------------------

            Provide a valid JSON response (no markdown, no code blocks, no backticks) in this format:
            {
              "matchPercentage": 0-100,
              "missingSkills": ["skill1", "skill2"],
              "suggestions": "..."
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
                throw new IOException("Unexpected code " + response);
            }

            String rawJson = response.body().string();
            JsonNode root = objectMapper.readTree(rawJson);
            String rawText = root
                    .path("candidates")
                    .get(0)
                    .path("content")
                    .path("parts")
                    .get(0)
                    .path("text")
                    .asText();

            return sanitizeResponse(rawText);
        }
    }

    public String getSummary(String resumeText) {
        String prompt = """
        You are an expert HR manager. Summarize the following resume in 3–4 bullet points that highlight:
        - Key technical skills
        - Project/experience highlights
        - Any achievements or unique strengths

        Resume:
        -------------------------
        %s
        -------------------------

        Format your response as plain bullet points (no markdown or backticks).
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
                .addHeader("Content-Type", "application/json")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                return "Summary generation failed: " + response.message();
            }

            String rawJson = response.body().string();
            JsonNode root = objectMapper.readTree(rawJson);
            String rawText = root
                    .path("candidates")
                    .get(0)
                    .path("content")
                    .path("parts")
                    .get(0)
                    .path("text")
                    .asText();

            return sanitizeResponse(rawText);
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
        if (raw.startsWith("```")) {
            raw = raw.replaceFirst("```json", "").replaceFirst("```", "").trim();
        }
        return raw.replaceAll("```", "").trim();
    }
}
