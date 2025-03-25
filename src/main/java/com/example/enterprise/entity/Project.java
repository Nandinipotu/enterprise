package com.example.enterprise.entity;

import java.util.Date;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document("projects")
public class Project {
    
    private String id;
    private String projectId;
    private String projectName;
    private String fileName;
    private String projectURL;
    private Date createdAt;
    private Date updatedAt;
}
