package com.example.enterprise.service.auth;

import org.springframework.http.ResponseEntity;

import com.example.enterprise.dto.request.LoginRequest;
import com.example.enterprise.dto.request.SignOutRequest;
import com.example.enterprise.dto.request.SignUpRequest;
import com.example.enterprise.utils.ApiResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

public interface AuthService {

    ResponseEntity<ApiResponse> signUp(LoginRequest loginRequest);

    ResponseEntity<ApiResponse> logIn(LoginRequest loginRequest, HttpServletResponse response);

    ResponseEntity<ApiResponse> signOut(SignOutRequest request);
    
    ResponseEntity<ApiResponse> enterpriseUserSignUp(LoginRequest loginRequest);

   ResponseEntity<ApiResponse> enterpriseUserLogIn(LoginRequest loginRequest, HttpServletResponse response);
}
