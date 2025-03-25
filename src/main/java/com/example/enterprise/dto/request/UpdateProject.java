package com.example.enterprise.dto.request;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateProject {

    @NotBlank(message = "Project Id cannot be empty")
    private String projectId;   
    private String projectName;
    private MultipartFile file;
    @Pattern(
        regexp = "^(https?|ftp)://[^\\s/$.?#].[^\\s]*$",
        message = "Invalid URL format"
    )
    private String projectURL;
}
