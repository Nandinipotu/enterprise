package com.example.enterprise.controller.projects;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.enterprise.dto.ProjectsDTO;
import com.example.enterprise.dto.response.ApiResponse;
import com.example.enterprise.service.project.ProjectService;
import com.example.enterprise.utils.ApiResponseUtil;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;


@RestController
@RequestMapping("/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;
    
    @GetMapping("/")
    public ResponseEntity<ApiResponse> getProjectList() {
        try {
            List<ProjectsDTO> projects = projectService.getProjectList();
            return ApiResponseUtil.success("success", projects);
        } catch (Exception e) {
            return ApiResponseUtil.internalServerError(e.getMessage());
        }
    }
    
}
