package com.example.enterprise.dto.response;

import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AssetServiceTokenResponse {
    private String id;
    private String userId;
    private String projectId;
    private List<String> projectPermissions;
    private boolean isActive;
    private Date createdAt;
    private Date updatedAt;
    private List<RequiredFieldDTO> requiredFields;

    @Data
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RequiredFieldDTO {
        private String plant;
        private String domain;
        private String phoneNo;
        private String role;
        private String userId;
        private String userName;
        private String companyId;
        private String id;
    }
}
