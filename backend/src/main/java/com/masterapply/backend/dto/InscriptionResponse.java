package com.masterapply.backend.dto;

import com.masterapply.backend.entity.Inscription;
import java.time.LocalDateTime;

public record InscriptionResponse(
        Long id,
        Long candidatureId,
        Long candidatId,
        String candidatNom,
        String masterIntitule,
        String statut,
        LocalDateTime dateInscription
) {
    public static InscriptionResponse from(Inscription i) {
        return new InscriptionResponse(
                i.getId(),
                i.getCandidature().getId(),
                i.getCandidat().getId(),
                i.getCandidat().getPrenom() + " " + i.getCandidat().getNom(),
                i.getCandidature().getMaster().getIntitule(),
                i.getStatut(),
                i.getDateInscription());
    }
}



