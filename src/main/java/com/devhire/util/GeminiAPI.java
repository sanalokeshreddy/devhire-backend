package com.devhire.util;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class GeminiAPI {

    @Value("${gemini.api.key}")
    private String apiKey;

    private static final String GEMINI_API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent";

    public String generateJSON(String prompt) {
        try {
            URL url = new URL(GEMINI_API_URL + "?key=" + apiKey);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            String requestBody = """
            {
              "contents": [{
                "parts": [{
                  "text": "%s"
                }]
              }]
            }
            """.formatted(prompt.replace("\"", "\\\""));

            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = requestBody.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int responseCode = connection.getResponseCode();
            InputStream is = responseCode == 200 ? connection.getInputStream() : connection.getErrorStream();

            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonResponse = mapper.readTree(is);

            if (responseCode != 200) {
                throw new RuntimeException("Gemini API call failed: " + jsonResponse.toPrettyString());
            }

            // ✅ Extract and clean the generated text (remove Markdown code block)
            String rawText = jsonResponse
                    .get("candidates").get(0)
                    .get("content")
                    .get("parts").get(0)
                    .get("text").asText();

            String cleanedText = rawText
                    .replaceAll("(?s)```json", "")
                    .replaceAll("(?s)```", "")
                    .trim();

            return cleanedText;

        } catch (Exception e) {
            throw new RuntimeException("Gemini API call failed", e);
        }
    }
}