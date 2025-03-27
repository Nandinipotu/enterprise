package com.example.enterprise.dto;

import java.net.URI;
import java.nio.file.Path;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProjectsDTO {
    private String projectId;
    private String projectName;
    private String fileName;
    private String imagePath;
    private String projectURL;

}
