package com.masterapply.backend.dto;

import com.masterapply.backend.entity.Document;
import java.time.LocalDateTime;

public record DocumentResponse(Long id, String type, String nomFichier, LocalDateTime dateUpload) {

    public static DocumentResponse from(Document d) {
        return new DocumentResponse(d.getId(), d.getType(), d.getNomFichier(), d.getDateUpload());
    }
}