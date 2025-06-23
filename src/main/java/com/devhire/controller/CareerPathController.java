package com.devhire.controller;

import com.devhire.model.CareerPathRequest;
import com.devhire.model.CareerPathResponse;
import com.devhire.service.CareerPathService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = {
        "http://localhost:3000",
        "https://devhire-frontend.vercel.app"
})
@RestController
@RequestMapping("/api/career-path")
public class CareerPathController {

    @Autowired
    private CareerPathService careerPathService;

    @PostMapping
    public ResponseEntity<CareerPathResponse> generateCareerPath(@RequestBody CareerPathRequest request) {
        CareerPathResponse response = careerPathService.generateCareerPath(request);
        return ResponseEntity.ok(response);
    }
}
