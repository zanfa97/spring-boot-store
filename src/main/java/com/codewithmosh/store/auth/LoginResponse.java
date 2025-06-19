package com.codewithmosh.store.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "Response containing JWT tokens after successful login")
@Data
public class LoginResponse {
    @Schema(description = "JWT access token used for authenticating requests", example = "eyJhbGciOiJIUzI1NiIs...")
    private final Jwt accessToken;
    
    @Schema(description = "JWT refresh token used to obtain new access tokens", example = "eyJhbGciOiJIUzI1NiIs...")
    private final Jwt refreshToken;
}
