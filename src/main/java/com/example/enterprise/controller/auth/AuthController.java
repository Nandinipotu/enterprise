package com.example.enterprise.controller.auth;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.enterprise.dto.request.LoginRequest;
import com.example.enterprise.dto.request.SignOutRequest;
import com.example.enterprise.dto.request.SignUpRequest;
import com.example.enterprise.service.auth.AuthService;
import com.example.enterprise.utils.ApiResponse;
import com.example.enterprise.utils.ResponseMessage;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final AuthService authService;

    @PostMapping("/company-based-user-signUp")
    public ResponseEntity<ApiResponse> signUp( @RequestBody @Valid LoginRequest loginRequest){
        try {
            return authService.signUp(loginRequest);
        } catch (Exception e) {
           return ResponseEntity.badRequest().body(new ApiResponse(false, e.getMessage()));
        }
    }

    @PostMapping("/company-based-user-login")
    public ResponseEntity<ApiResponse> logIn( @RequestBody @Valid LoginRequest loginRequest,HttpServletResponse httpResponse){
        try {
            return authService.logIn(loginRequest,httpResponse);
        } catch (Exception e) {
           return ResponseEntity.badRequest().body(new ApiResponse(false, e.getMessage()));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse> logoutUser(@Valid @RequestBody SignOutRequest request) {
        try {
            return authService.signOut(request);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(new ApiResponse(false, e.getMessage()));
        }
    }
}
