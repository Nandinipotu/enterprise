package com.example.enterprise.service.project;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.springframework.web.multipart.MultipartFile;

import com.example.enterprise.dto.ProjectsDTO;
import com.example.enterprise.dto.request.ProjectRequest;
import com.example.enterprise.dto.request.UpdateProject;
import com.example.enterprise.entity.Project;

import jakarta.servlet.http.HttpServletRequest;

public interface ProjectService {
    List<ProjectsDTO> getProjectList();
    void createProject(ProjectRequest request) throws IOException ;
    Map<String, Object> generateProjectAccessToken(String projectId, HttpServletRequest request) throws Exception;
    List<ProjectsDTO> getAllProjectList();
    Project updateProject(UpdateProject project)  throws NoSuchElementException, IOException;
}
