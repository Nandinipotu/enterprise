package com.example.enterprise.entity;

import java.time.Instant;
import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "users_refresh_token")
public class UserRefreshToken {
    @Id
    private String id;
    
    @DBRef
    private User users;
    @NotNull(message = "field is mandatory")
    private String token;
    private Instant expiryDate;
    private Date updatedAt;

}