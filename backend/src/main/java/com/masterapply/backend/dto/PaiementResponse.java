package com.masterapply.backend.dto;

import com.masterapply.backend.entity.Paiement;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaiementResponse(
        Long id,
        Long candidatureId,
        BigDecimal montant,
        LocalDateTime datePaiement,
        String statut,
        String referenceTransaction,
        Long recuId,
        String mention
) {
    public static final String MENTION = "Paiement simulé : aucune transaction bancaire réelle";

    public static PaiementResponse from(Paiement p) {
        Long recuId = p.getRecu() == null ? null : p.getRecu().getId();
        return new PaiementResponse(
                p.getId(),
                p.getCandidature().getId(),
                p.getMontant(),
                p.getDatePaiement(),
                p.getStatut(),
                p.getReferenceTransaction(),
                recuId,
                MENTION);
    }
}