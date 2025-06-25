package com.devhire.controller;

import com.devhire.model.ResumeMatchResult;
import com.devhire.service.ResumeAnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@CrossOrigin(origins = {"http://localhost:3000", "https://devhire-frontend.vercel.app"})
@RequestMapping("/api")
public class ResumeController {

    @Autowired
    private ResumeAnalysisService resumeService;

    // ✅ For multiple resume analysis (used in ResumeUploader.jsx)
    @PostMapping("/analyze")
    public List<ResumeMatchResult> analyzeResumes(
            @RequestParam("resumes") MultipartFile[] resumes,
            @RequestParam("jobRole") String jobRole,
            @RequestParam(value = "jobDescription", required = false) String jobDescription) {
        return resumeService.analyzeResumes(resumes, jobRole, jobDescription);
    }

    // ✅ For analyzing a single resume (used in JDHeatmap)
    @PostMapping("/analyze-single")
    public ResumeMatchResult analyzeSingleResume(
            @RequestParam("resume") MultipartFile resume,
            @RequestParam("jobDescription") String jobDescription) {
        return resumeService.analyzeSingleResume(resume, jobDescription);
    }
}
