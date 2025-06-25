package com.devhire.controller;

import com.devhire.service.SkillExtractionService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = {"http://localhost:3000", "https://devhire-frontend.vercel.app"})
@RequestMapping("/api")
public class JDController {

    @Autowired
    private SkillExtractionService skillService;

    @PostMapping("/extract-skills")
public ResponseEntity<?> extractSkills(@RequestBody Map<String, String> requestBody) {
    try {
        String jobDescription = requestBody.get("jobDescription");
        String geminiResponse = skillService.extractSkillsFromJD(jobDescription);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(geminiResponse);

        JsonNode candidates = root.path("candidates");
        if (candidates.isEmpty() || !candidates.get(0).has("content")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Invalid Gemini response structure");
        }

        String rawText = candidates.get(0)
                .path("content")
                .path("parts")
                .get(0)
                .path("text")
                .asText()
                .trim();

        // ✅ Clean unexpected markdown
        if (rawText.startsWith("```")) {
            int firstNewline = rawText.indexOf("\n");
            rawText = rawText.substring(firstNewline + 1).replace("```", "").trim();
        }

        System.out.println("Cleaned Gemini Skill JSON: " + rawText); // Debug log

        // ✅ Try parsing the skills list
        List<String> skills = mapper.readValue(rawText, new TypeReference<>() {});
        return ResponseEntity.ok(skills);

    } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Skill extraction failed: " + e.getMessage());
    }
}
}
