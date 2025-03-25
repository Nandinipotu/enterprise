package com.example.enterprise.dto.request;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ProjectRequest {

    @NotBlank(message = "Project name cannot be empty")
    @Size(max = 100, message = "Project name cannot exceed 100 characters")
    private String projectName;

    private MultipartFile file;

    @NotBlank(message = "Project URL cannot be empty")
    @Pattern(
        regexp = "^(https?|ftp)://[^\\s/$.?#].[^\\s]*$",
        message = "Invalid URL format"
    )
    private String projectURL;
}
