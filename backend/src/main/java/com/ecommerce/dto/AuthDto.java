package com.ecommerce.dto;

import com.ecommerce.entity.Role;
import lombok.*;

public class AuthDto {

    @Getter
    @Setter
    public static class RegisterRequest {
        private String name;
        private String email;
        private String password;
        private Role role;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class LoginRequest {
        private String email;
        private String password;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AuthResponse {
        private String token;
        private String email;
        private Role role;
    }
}
