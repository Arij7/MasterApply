package com.masterapply.backend.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

public record RegisterRequest(
        @NotBlank(message = "Le nom est obligatoire") String nom,
        @NotBlank(message = "Le prénom est obligatoire") String prenom,
        @NotBlank(message = "L'email est obligatoire")
        @Email(message = "Format d'email invalide") String email,
        @NotBlank(message = "Le mot de passe est obligatoire")
        @Size(min = 8, message = "Le mot de passe doit contenir au moins 8 caractères") String motDePasse,
        LocalDate dateNaissance,
        String telephone,
        String nationalite,
        @DecimalMin(value = "0.0", message = "La moyenne doit être comprise entre 0 et 20")
        @DecimalMax(value = "20.0", message = "La moyenne doit être comprise entre 0 et 20") Double moyenneGenerale
) {}