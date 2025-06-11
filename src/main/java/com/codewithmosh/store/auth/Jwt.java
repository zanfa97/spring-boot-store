package com.codewithmosh.store.auth;

import java.util.Date;

import javax.crypto.SecretKey;

import com.codewithmosh.store.users.Role;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Jwt {

    private Claims claims;
    private SecretKey secretKey;

    public boolean isExpired() {
        return claims.getExpiration().before(new Date());
    }

    public Long getUserId() {
        return Long.valueOf(claims.getSubject());
    }

    public Role getRole() {
        return Role.valueOf(claims.get("role", String.class));
    }

    @Override
    public String toString() {
        return Jwts.builder()
                .claims(claims)
                .signWith(secretKey)
                .compact();
    }

}
