package com.devhire.service;

import com.devhire.model.*;
import com.devhire.util.GeminiAPI;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CareerPathService {

    @Autowired
    private GeminiAPI geminiAPI;

    @Autowired
    private ObjectMapper objectMapper;

    public CareerPathResponse generateCareerPath(CareerPathRequest request) {
        String prompt = buildPrompt(request.getResumeText(), request.getTargetRole());

        String aiResponse;
        try {
            aiResponse = geminiAPI.generateJSON(prompt); // ✅ use injected instance
        } catch (Exception e) {
            throw new RuntimeException("Error generating response from Gemini API", e);
        }

        try {
            return objectMapper.readValue(aiResponse, CareerPathResponse.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse AI response", e);
        }
    }

    private String buildPrompt(String resumeText, String targetRole) {
        return "You are a career mentor AI for an aspiring " + targetRole + ".\n\n" +
               "Analyze this resume: \n" + resumeText + "\n\n" +
               "Return JSON with: {\n" +
               "  targetRole: string,\n" +
               "  missingSkills: [string],\n" +
               "  courses: [string],\n" +
               "  miniProject: string,\n" +
               "  timeline: [{ week: string, focus: string }],\n" +
               "  githubLinks: [string]\n" +
               "}. ONLY return raw JSON. No explanations.";
    }
}
