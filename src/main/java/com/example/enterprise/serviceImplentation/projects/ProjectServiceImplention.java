package com.example.enterprise.serviceImplentation.projects;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.PublicKey;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.LookupOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.enterprise.dto.ProjectsDTO;
import com.example.enterprise.dto.request.ProjectRequest;
import com.example.enterprise.dto.request.UpdateProject;
import com.example.enterprise.entity.Project;
import com.example.enterprise.repository.ProjectRepository;
import com.example.enterprise.service.project.AssetService;
import com.example.enterprise.service.project.ProjectService;
import com.example.enterprise.utils.AuthUserDetails;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProjectServiceImplention implements ProjectService {

    @Value("${upload.path}")
    private String uploadsPath;

    private final MongoTemplate mongoTemplate;
    private final AssetService assetService;
    private final NeramServiceImpl neramService;
    private final ProjectRepository projectRepository;

    @Override
    public List<ProjectsDTO> getProjectList() {
        String userId = AuthUserDetails.getUserId();
        Criteria criteria = Criteria.where("userId").is(userId);
        LookupOperation projectLookup = Aggregation.lookup("projects", "projectId", "projectId", "projectDetails");

        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(criteria),
                projectLookup,
                Aggregation.unwind("projectDetails", true),
                Aggregation.project("projectDetails.projectId", "projectDetails.projectName",
                        "projectDetails.projectURL"));

        List<ProjectsDTO> results = mongoTemplate.aggregate(aggregation, "enterprise_user_projects", ProjectsDTO.class)
                .getMappedResults();

        results.forEach(dto -> {
            try {
                if (dto.getProjectURL() != null) {
                    dto.setProjectURL(new URI(dto.getProjectURL().toString()));
                }
            } catch (URISyntaxException e) {
                throw new RuntimeException("Invalid URL format: " + dto.getProjectURL(), e);
            }
        });

        return results;
    }

    @Override
    public void createProject(ProjectRequest request) throws IOException {
        String lastProjectId = getLastProjectId();
        String projectId = generateProjectId(lastProjectId);
        String fileName = projectFileUpload(request.getProjectName(), request.getFile());
        Project project = new Project();
        project.setProjectId(projectId);
        project.setProjectName(request.getProjectName());
        project.setFileName(fileName);
        project.setProjectURL(request.getProjectURL());
        project.setCreatedAt(new Date());
        project.setUpdatedAt(new Date());
        projectRepository.save(project);
    }

    public String getLastProjectId() {
        Query query = new Query();
        query.with(Sort.by(Sort.Direction.DESC, "projectId"));
        query.limit(1);

        Project lastProject = mongoTemplate.findOne(query, Project.class);

        if (lastProject == null || lastProject.getProjectId() == null) {
            return "ERP-PRO-000";
        }

        return lastProject.getProjectId();
    }

    public synchronized String generateProjectId(String lastProjectId) {
        String counter = lastProjectId.substring(8);
        Long number = Long.parseLong(counter);
        number++;
        DecimalFormat formatter = new DecimalFormat("000");
        String uniqueNumber = formatter.format(number);
        return "ERP-PRO-" + uniqueNumber;
    }

    @Override
    public Map<String, Object> generateProjectAccessToken(String projectId, HttpServletRequest request)
            throws Exception {
        Map<String, Object> response = new HashMap<>();

        switch (projectId) {
            case "ERP-PRO-001":
                return assetService.assetSignIn(projectId, request);

            case "ERP-PRO-002":
                return neramService.createNeramToken(projectId, request);

            default:
                response.put("error", "Invalid project ID");
                return response;
        }
    }

    @Override
    public List<ProjectsDTO> getAllProjectList() {
        List<Project> projectList = projectRepository.findAll();
        return projectList.stream()
                .map(project -> {
                    ProjectsDTO dto = new ProjectsDTO();
                    BeanUtils.copyProperties(project, dto);
                    if (project.getProjectURL() != null) {
                        dto.setProjectURL(URI.create(project.getProjectURL()));
                    }
                    Path filePath = Paths.get(uploadsPath + project.getFileName());
                    boolean file = Files.exists(filePath);
                    if (file) {
                        dto.setImagePath(filePath);
                    }
                    return dto;
                })
                .toList();
    }

    @Override
    public Project updateProject(UpdateProject project) throws NoSuchElementException, IOException {
        Project existingData = projectRepository.findByProjectId(project.getProjectId())
                .orElseThrow(() -> new NoSuchElementException("Project Id not found"));

        if (project.getProjectName() != null && !project.getProjectName().isEmpty()) {
            existingData.setProjectName(project.getProjectName());
        }

        if (project.getProjectURL() != null && !project.getProjectURL().isEmpty()) {
            existingData.setProjectURL(project.getProjectURL());
        }

        if (project.getFile() != null && !project.getFile().isEmpty()) {
            String fileName = projectFileUpload(project.getProjectName(), project.getFile());
            existingData.setFileName(fileName);
        }
        return projectRepository.save(existingData);
    }

    private String projectFileUpload(String fileName, MultipartFile file) throws IOException {
        Path directory = Paths.get(uploadsPath);
        if (!Files.exists(directory)) {
            try {
                Files.createDirectories(directory);
            } catch (IOException e) {
                throw new IOException("Could not create upload directory", e);
            }
        }
        String originalFilename =file.getOriginalFilename();
        String[] parts = originalFilename.split("\\.");
        String fileExtension = parts.length > 1 ? parts[parts.length - 1] : "";
        String newFileName = fileName + "." + fileExtension.toString();
        Path filePath = directory.resolve(newFileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        return fileName;
    }
}
