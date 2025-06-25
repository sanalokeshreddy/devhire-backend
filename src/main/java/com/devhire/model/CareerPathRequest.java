package com.devhire.model;

import lombok.Data;
import java.util.List;

/**
 * Simple DTO for the JSON / multipart fields coming from CareerPathForm.jsx.
 * (The optional PDF résumé is handled separately in the controller as a MultipartFile.)
 */
@Data
public class CareerPathRequest {

    /** e.g. “Full-Stack Developer”, “ML Engineer”, … */
    private String role;

    /** Raw comma-separated skills string that you capture in the form. */
    private String skills;

    /** "entry" | "mid" | "senior" — or empty if the user skips the dropdown   */
    private String experience;

    /** "3months" | "6months" | "1year" | "2years" — or empty */
    private String timeframe;

    /** List that the UI builds from the “Areas of Interest” chips  */
    private List<String> interests;
}
