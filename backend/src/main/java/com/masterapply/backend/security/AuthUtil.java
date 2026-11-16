package com.masterapply.backend.security;

import org.springframework.security.oauth2.jwt.Jwt;

public final class AuthUtil {

    private AuthUtil() {
    }

    public static Long uid(Jwt jwt) {
        return ((Number) jwt.getClaim("uid")).longValue();
    }

    public static boolean estAdmin(Jwt jwt) {
        return "ADMIN".equals(jwt.getClaimAsString("role"));
    }
}