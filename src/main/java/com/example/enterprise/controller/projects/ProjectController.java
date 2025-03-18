package com.example.enterprise.controller.projects;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.enterprise.dto.response.ApiResponse;
import com.example.enterprise.utils.ApiResponseUtil;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/projects")
@RequiredArgsConstructor
public class ProjectController {
    
    @GetMapping("/")
    public ResponseEntity<ApiResponse> getProjectList(@RequestParam String param) {
        try {
            return ApiResponseUtil.success("success", null);
        } catch (Exception e) {
            return ApiResponseUtil.internalServerError(e.getMessage());
        }
    }
    
}
