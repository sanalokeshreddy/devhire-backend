package com.devhire.service;

import com.devhire.model.ResumeMatchResult;
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
        ObjectMapper mapper = new ObjectMapper();

        for (MultipartFile file : resumes) {
            try {
                String extractedText = PdfExtractorUtil.extractText(file);

                // 🔍 Analyze with Gemini
                String cleanJson = aiService.analyzeWithAI(extractedText, jobRole, jobDescription);
                JsonNode jsonResult = mapper.readTree(cleanJson);

                double match = jsonResult.get("matchPercentage").asDouble();
                List<String> skills = new ArrayList<>();
               for (JsonNode skillNode : jsonResult.get("missingSkills")) {
    String cleanSkill = skillNode.asText().trim().toLowerCase();
    skills.add(cleanSkill);
}


                String suggestions = jsonResult.get("suggestions").asText();

                // 🧠 AI-generated Summary
                String summaryPrompt = "Summarize this resume with strengths, key technical skills, and potential gaps relevant to the role '" 
                        + jobRole + "':\n" + extractedText;
                String summary = aiService.getSummary(summaryPrompt);

                results.add(new ResumeMatchResult(
                        file.getOriginalFilename(),
                        match,
                        skills,
                        suggestions,
                        summary
                ));
            } catch (Exception e) {
                e.printStackTrace();
                results.add(new ResumeMatchResult(
                        file.getOriginalFilename(),
                        0,
                        List.of("AI Error"),
                        "Failed to analyze resume: " + e.getMessage(),
                        "No summary available due to an error."
                ));
            }
        }

        return results;
    }
}
