package com.codewithmosh.store.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginUserRequest(@NotBlank @Email String email, @NotBlank String password) {
}
