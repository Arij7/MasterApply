package com.masterapply.backend.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record DecisionRequest(
        @NotBlank(message = "La décision est obligatoire")
        @Pattern(regexp = "ACCEPTEE|REFUSEE", message = "La décision doit être ACCEPTEE ou REFUSEE") String decision,
        @DecimalMin(value = "0.0", message = "La note doit être comprise entre 0 et 20")
        @DecimalMax(value = "20.0", message = "La note doit être comprise entre 0 et 20") Double noteDossier,
        @Size(max = 1000, message = "Le commentaire ne doit pas dépasser 1000 caractères") String commentaire
) {}