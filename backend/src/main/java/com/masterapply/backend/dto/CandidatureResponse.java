package com.masterapply.backend.dto;

import com.masterapply.backend.entity.Candidature;
import java.time.LocalDateTime;

public record CandidatureResponse(
        Long id,
        Long candidatId,
        String candidatNom,
        Long masterId,
        String masterIntitule,
        Long campagneId,
        String campagneLibelle,
        String statut,
        String motivation,
        LocalDateTime dateCreation,
        LocalDateTime dateSoumission,
        LocalDateTime dateDecision,
        Double noteDossier,
        String commentaireAdmin,
        int nombreDocuments
) {
    public static CandidatureResponse from(Candidature c) {
        return new CandidatureResponse(
                c.getId(),
                c.getCandidat().getId(),
                c.getCandidat().getPrenom() + " " + c.getCandidat().getNom(),
                c.getMaster().getId(),
                c.getMaster().getIntitule(),
                c.getCampagne().getId(),
                c.getCampagne().getLibelle(),
                c.getStatut(),
                c.getMotivation(),
                c.getDateCreation(),
                c.getDateSoumission(),
                c.getDateDecision(),
                c.getNoteDossier(),
                c.getCommentaireAdmin(),
                c.getDocuments().size());
    }
}