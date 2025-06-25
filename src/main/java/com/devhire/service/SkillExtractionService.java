package com.devhire.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class SkillExtractionService {

    @Value("${gemini.api.key}")
    private String apiKey;

    private static final String GEMINI_API_URL =
        "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=";

    private final ObjectMapper mapper = new ObjectMapper();

    /**
     * Calls Gemini and returns raw JSON string
     */
    public String extractSkillsFromJD(String jobDescription) throws IOException {
        String prompt = """
        You are an AI assistant that extracts key technical skills from job descriptions.

        "Extract the core technical skills from this job description.\nReturn only a JSON array (no markdown, no extra text).\nExample: [\"Java\", \"Spring Boot\", \"Docker\"]"


        Do not return markdown, explanations, or any extra text.

        Job Description:
        %s
        """.formatted(jobDescription);

        OkHttpClient client = new OkHttpClient();
        MediaType mediaType = MediaType.parse("application/json");

        String requestBody = """
        {
          "contents": [
            {
              "role": "user",
              "parts": [{ "text": %s }]
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
                throw new IOException("Gemini request failed: " + response);
            }
            return response.body().string();
        }
    }

    /**
     * Parses Gemini's raw response and returns a clean List<String> of skills
     */
    public List<String> extractSkillsFromJDAsList(String jobDescription) throws IOException {
        String json = extractSkillsFromJD(jobDescription);

        JsonNode root = mapper.readTree(json);
        JsonNode candidates = root.path("candidates");

        if (candidates.isEmpty() || !candidates.get(0).has("content")) {
            throw new IOException("Invalid Gemini response structure");
        }

        String rawText = candidates.get(0)
                .path("content")
                .path("parts")
                .get(0)
                .path("text")
                .asText()
                .trim();

        // Remove markdown backticks if present
        if (rawText.startsWith("```")) {
            int firstNewline = rawText.indexOf("\n");
            rawText = rawText.substring(firstNewline + 1).replace("```", "").trim();
        }

        return mapper.readValue(rawText, new TypeReference<List<String>>() {});
    }

    private String toJson(String text) {
        return "\"" + text.replace("\\", "\\\\")
                          .replace("\"", "\\\"")
                          .replace("\n", "\\n")
                          .replace("\r", "") + "\"";
    }
}
