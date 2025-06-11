package com.codewithmosh.store.auth;

import lombok.Data;

@Data
public class LoginResponse {
    private final Jwt accessToken;
    private final Jwt refreshToken;

}
