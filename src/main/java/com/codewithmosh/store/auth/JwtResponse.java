package com.codewithmosh.store.auth;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response containing a JWT token")
public record JwtResponse(
    @Schema(description = "JWT token string", example = "eyJhbGciOiJIUzI1NiIs...")
    String token
) {}
