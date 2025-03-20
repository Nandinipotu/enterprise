package com.example.enterprise.controller.projects;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.enterprise.service.project.NeramService;
import com.example.enterprise.utils.ApiResponse;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/neram")
@RequiredArgsConstructor
public class NeramController {
    private final NeramService neramService;

    @PostMapping("/neram-access-token")
    public ResponseEntity<ApiResponse> assetTokenGenerator(@RequestParam String projectId, HttpServletRequest request){
        try {
            return neramService.createNeramToken(projectId,request);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(new ApiResponse(false, e.getMessage()));
        }
    }
}
