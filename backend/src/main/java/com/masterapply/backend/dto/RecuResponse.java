package com.masterapply.backend.dto;

import com.masterapply.backend.entity.Candidature;
import com.masterapply.backend.entity.Paiement;
import com.masterapply.backend.entity.Recu;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record RecuResponse(
        Long id,
        String numero,
        LocalDateTime dateGeneration,
        Long paiementId,
        Long candidatureId,
        String candidatNom,
        String masterIntitule,
        BigDecimal montant,
        LocalDateTime datePaiement,
        String referenceTransaction,
        String mention
) {
    public static RecuResponse from(Recu r) {
        Paiement p = r.getPaiement();
        Candidature c = p.getCandidature();
        return new RecuResponse(
                r.getId(),
                r.getNumero(),
                r.getDateGeneration(),
                p.getId(),
                c.getId(),
                c.getCandidat().getPrenom() + " " + c.getCandidat().getNom(),
                c.getMaster().getIntitule(),
                p.getMontant(),
                p.getDatePaiement(),
                p.getReferenceTransaction(),
                PaiementResponse.MENTION);
    }
}