package com.example.enterprise.service.project;

import org.springframework.http.ResponseEntity;

import com.example.enterprise.utils.ApiResponse;

import jakarta.servlet.http.HttpServletRequest;

public interface NeramService {
    
    ResponseEntity<ApiResponse> createNeramToken(String projectId, HttpServletRequest request);

}
