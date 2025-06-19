package com.codewithmosh.store.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request body for user login")
public record LoginUserRequest(
    @Schema(description = "User's email address", example = "user@example.com")
    @NotBlank @Email String email,
    
    @Schema(description = "User's password", example = "secret123", minLength = 6)
    @NotBlank String password
) {}
