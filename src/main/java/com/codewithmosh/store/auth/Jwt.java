package com.codewithmosh.store.auth;

import java.util.Date;
import javax.crypto.SecretKey;
import com.codewithmosh.store.users.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.AllArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "JWT token with claims and verification capabilities")
@AllArgsConstructor
public class Jwt {
    @Schema(description = "JWT claims containing user information and metadata", hidden = true)
    private Claims claims;
    
    @Schema(description = "Secret key for JWT signing and verification", hidden = true)
    private SecretKey secretKey;

    @Schema(description = "Checks if the JWT token has expired")
    public boolean isExpired() {
        return claims.getExpiration().before(new Date());
    }

    @Schema(description = "Gets the user ID from the JWT claims")
    public Long getUserId() {
        return Long.valueOf(claims.getSubject());
    }

    @Schema(description = "Gets the user role from the JWT claims")
    public Role getRole() {
        return Role.valueOf(claims.get("role", String.class));
    }

    @Schema(description = "Converts the JWT object to a signed JWT string")
    @Override
    public String toString() {
        return Jwts.builder()
                .claims(claims)
                .signWith(secretKey)
                .compact();
    }
}
