package com.example.enterprise.service.project;

import java.util.Map;

import org.springframework.http.ResponseEntity;

import com.example.enterprise.utils.ApiResponse;

import jakarta.servlet.http.HttpServletRequest;

public interface NeramService {
    
    Map<String, Object> createNeramToken(String projectId, HttpServletRequest request);

}
