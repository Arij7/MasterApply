package com.masterapply.backend.dto;

public record AuthResponse(
        String token,
        String type,
        Long id,
        String nom,
        String prenom,
        String email,
        String role
) {}