package com.example.enterprise.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SignOutRequest {
    @NotNull(message = "Token is mandatory")
    @NotEmpty(message = "Token must not be null")
    String token;
}
