package com.example.enterprise.controller.projects;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.enterprise.dto.ProjectsDTO;
import com.example.enterprise.dto.request.ProjectRequest;
import com.example.enterprise.dto.request.UpdateProject;
import com.example.enterprise.dto.response.ApiResponse;
import com.example.enterprise.entity.Project;
import com.example.enterprise.service.project.ProjectService;
import com.example.enterprise.utils.ApiResponseUtil;
import com.example.enterprise.utils.ResponseMessage;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @GetMapping("/user-based-project-list")
    public ResponseEntity<ApiResponse> getProjectList() {
        try {
            List<ProjectsDTO> projects = projectService.getProjectList();
            return ApiResponseUtil.success("success", projects);
        } catch (Exception e) {
            return ApiResponseUtil.internalServerError(e.getMessage());
        }
    }

    @PostMapping(value = "create-project", consumes = "multipart/form-data")
    public ResponseEntity<ApiResponse> createProject(@Valid @ModelAttribute ProjectRequest request) {
        try {
            String contentType = request.getFile().getContentType();
            if (!("image/png".equals(contentType) || "image/svg+xml".equals(contentType))) {
                return ApiResponseUtil.badRequest("File type should be svg or png");
            }
            projectService.createProject(request);
            return ApiResponseUtil.success("Project created successfully", null);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponseUtil.internalServerError(e.getMessage());
        }
    }

    @PostMapping("generate-project-access-token")
    public ResponseEntity<ApiResponse> generateProjectAccessToken(String projectId, HttpServletRequest request) {
        try {
            Map<String, Object> response = projectService.generateProjectAccessToken(projectId, request);
            return ApiResponseUtil.created(response, ResponseMessage.TOKEN_GENERATED_SUCCESSFULLY.getMessage());
        } catch (UsernameNotFoundException | BadCredentialsException e) {
            return ApiResponseUtil.badRequest(ResponseMessage.INVALID_CREDENTIALS.getMessage());
        } catch (Exception e) {
            return ApiResponseUtil.internalServerError(e.getMessage());
        }
    }

    @GetMapping("/get-all-project-list")
    public ResponseEntity<ApiResponse> getAllProjectList() {
        try {
            List<ProjectsDTO> projects = projectService.getAllProjectList();
            return ApiResponseUtil.success("success", projects);
        } catch (Exception e) {
            return ApiResponseUtil.internalServerError(e.getMessage());
        }
    }

    @PutMapping(value = "/update-project", consumes = "multipart/form-data")
    public ResponseEntity<ApiResponse> updateProject(@Valid @ModelAttribute UpdateProject project) {
        try {

            if (project.getFile() != null && !project.getFile().isEmpty()) {
                String contentType = project.getFile().getContentType();
                if (!("image/png".equals(contentType) || "image/svg+xml".equals(contentType))) {
                    return ApiResponseUtil.badRequest("File type should be svg or png");
                }
            }
            Project projects = projectService.updateProject(project);
            return ApiResponseUtil.success("success", projects);
        } catch (Exception e) {
            return ApiResponseUtil.internalServerError(e.getMessage());
        }
    }

}
