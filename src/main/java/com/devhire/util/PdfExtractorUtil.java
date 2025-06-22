package com.devhire.util;

import org.apache.tika.Tika;
import org.springframework.web.multipart.MultipartFile;

public class PdfExtractorUtil {

    private static final Tika tika = new Tika();

    public static String extractText(MultipartFile file) {
        try {
            return tika.parseToString(file.getInputStream());
        } catch (Exception e) {
            return "Error extracting text: " + e.getMessage();
        }
    }
}
