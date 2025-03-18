package com.example.enterprise.entity;

import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Document("enterprise_users")
public class EnterPriseUser {
    @Id
    private String id;
    private String userEmail;
    private String userName;
    private String password;
    private String orgId;
    private String role;
    private boolean isUserLoggedIn;
    private boolean isActive;
    private List<String> permissions;
    private Date createdAt;
    private Date updatedAt;
}
