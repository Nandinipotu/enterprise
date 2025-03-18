package com.example.enterprise.controller.projects;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


import com.example.enterprise.service.project.AssetService;
import com.example.enterprise.utils.ApiResponse;
import com.example.enterprise.utils.ApiResponseUtil;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/asset")
@RequiredArgsConstructor
public class AssetController {
    private final AssetService assetService;

    @PostMapping("/asset-token")
    public ResponseEntity<ApiResponse> assetTokenGenerator(@RequestParam String projectId, HttpServletRequest request){
        try {
            return assetService.assetSignIn(projectId,request);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(new ApiResponse(false, e.getMessage()));
        }
    }
}
