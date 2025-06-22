package com.devhire.model;

import java.util.List;

public class ResumeResult {
    private String fileName;
    private String extractedText;

    public ResumeResult(String fileName, String extractedText) {
        this.fileName = fileName;
        this.extractedText = extractedText;
    }

    public String getFileName() {
        return fileName;
    }

    public String getExtractedText() {
        return extractedText;
    }
}
