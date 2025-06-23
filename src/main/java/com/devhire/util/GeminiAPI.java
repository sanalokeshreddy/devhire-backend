package com.devhire.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.springframework.stereotype.Component;

@Component
public class GeminiAPI {

    private static final String GEMINI_API_KEY = System.getenv("GEMINI_API_KEY"); // fallback or hardcoded for local
    private static final String GEMINI_API_URL =
            "https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent?key=" + GEMINI_API_KEY;

    private static final OkHttpClient client = new OkHttpClient();
    private static final ObjectMapper mapper = new ObjectMapper();

    public String generateJSON(String prompt) throws Exception {
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        String bodyJson = "{ \"contents\": [{\"parts\":[{\"text\":\"" + prompt.replace("\"", "\\\"") + "\"}]}] }";
        RequestBody body = RequestBody.create(bodyJson, JSON);

        Request request = new Request.Builder()
                .url(GEMINI_API_URL)
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            String res = response.body().string();
            JsonNode node = mapper.readTree(res);
            return node.get("candidates").get(0).get("content").get("parts").get(0).get("text").asText();
        }
    }
}
