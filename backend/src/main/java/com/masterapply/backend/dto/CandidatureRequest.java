package com.masterapply.backend.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CandidatureRequest(
        @NotNull(message = "Le Master est obligatoire") Long masterId,
        Long campagneId,
        @Size(max = 2000, message = "La motivation ne doit pas dépasser 2000 caractères") String motivation
) {}