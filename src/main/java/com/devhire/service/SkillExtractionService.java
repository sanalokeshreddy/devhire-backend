package com.devhire.service;

import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class SkillExtractionService {

    @Value("${gemini.api.key}")
    private String apiKey;

    private static final String GEMINI_API_URL =
        "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=";

    public String extractSkillsFromJD(String jobDescription) throws IOException {
        String prompt = """
        Extract the key skills required from the following job description.
        Return only a JSON array of strings with no markdown or explanation.
        Example: ["Java", "Spring Boot", "Docker"]

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

    private String toJson(String text) {
        return "\"" + text.replace("\\", "\\\\")
                          .replace("\"", "\\\"")
                          .replace("\n", "\\n")
                          .replace("\r", "") + "\"";
    }
}
