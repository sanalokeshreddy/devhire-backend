package com.devhire.controller;

import com.devhire.service.SkillExtractionService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = {"http://localhost:3000", "https://devhire-frontend.vercel.app"})
@RequestMapping("/api")
public class JDController {

    @Autowired
    private SkillExtractionService skillService;

    @PostMapping("/extract-skills")
    public List<String> extractSkills(@RequestBody Map<String, String> requestBody) throws IOException {
        String jobDescription = requestBody.get("jobDescription");

        // Get Gemini response (which might contain Markdown backticks)
        String geminiResponse = skillService.extractSkillsFromJD(jobDescription);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode skillNode = mapper.readTree(geminiResponse)
                .get("candidates").get(0)
                .get("content").get("parts").get(0)
                .get("text");

        String rawText = skillNode.asText().trim();

        // ✅ Clean Markdown-style code block from Gemini if present
        if (rawText.startsWith("```")) {
            int firstNewline = rawText.indexOf("\n");
            rawText = rawText.substring(firstNewline + 1); // Skip "```json"
            rawText = rawText.replace("```", "").trim();   // Remove closing backticks
        }

        // ✅ Now parse clean JSON to List<String>
        return mapper.readValue(rawText, new TypeReference<List<String>>() {});
    }
}
