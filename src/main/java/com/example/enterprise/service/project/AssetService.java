package com.example.enterprise.service.project;

import org.springframework.http.ResponseEntity;

import com.example.enterprise.utils.ApiResponse;

public interface AssetService {

    ResponseEntity<ApiResponse> assetSignIn();
    
} 
