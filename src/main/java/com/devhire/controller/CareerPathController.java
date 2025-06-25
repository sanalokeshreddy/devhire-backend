package com.devhire.controller;

import com.devhire.model.CareerPathRequest;
import com.devhire.model.CareerPathResponse;
import com.devhire.service.CareerPathService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * REST endpoint that returns an AI-generated career roadmap.
 */
@RestController
@RequestMapping("/api")
@CrossOrigin(
        origins = {
                "http://localhost:3000",
                "https://devhire-frontend.vercel.app"
        },
        allowCredentials = "true")
@RequiredArgsConstructor
@Slf4j
public class CareerPathController {

    private final CareerPathService careerPathService;

    /**
     * Endpoint hit by the new CareerPathForm.
     *
     * The form is posted as multipart/form-data:
     *  • all ordinary text fields are bound to {@link CareerPathRequest} via
     *    {@code @ModelAttribute}
     *  • the optional PDF résumé comes in as {@code @RequestPart MultipartFile resume}
     */
    @PostMapping(
            value = "/career-roadmap",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CareerPathResponse> generateRoadmap(
            @ModelAttribute CareerPathRequest request,
            @RequestPart(name = "resume", required = false) MultipartFile resume) {

        log.info("Career-roadmap request received for targetRole='{}'", request.getRole());

        CareerPathResponse response = careerPathService.createRoadmap(request, resume);
        return ResponseEntity.ok(response);
    }
}
