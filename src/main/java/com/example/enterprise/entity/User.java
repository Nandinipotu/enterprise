package com.example.enterprise.entity;

import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.repository.Update;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(value = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    
    @Id
    private String id;
    private String userId;
    private String userName;
    private String email;
    private String password;
    private boolean isActive;
    private boolean isUserLoggedIn;
    private List<String> roles;
    private List<String> permission;
    @CreatedDate
    private Date createdAt;
    private Date updatedAt;

}
