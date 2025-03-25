package com.example.enterprise.service.project;

import java.net.UnknownHostException;
import java.util.Map;

import org.springframework.http.ResponseEntity;

import com.example.enterprise.utils.ApiResponse;

import jakarta.servlet.http.HttpServletRequest;

public interface AssetService {

    Map<String, Object> assetSignIn(String projectId, HttpServletRequest request) throws UnknownHostException;
    
} 
