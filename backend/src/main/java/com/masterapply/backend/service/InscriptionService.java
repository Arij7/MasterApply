package com.masterapply.backend.service;

import com.masterapply.backend.dto.InscriptionResponse;
import com.masterapply.backend.entity.Candidature;
import com.masterapply.backend.entity.Inscription;
import com.masterapply.backend.entity.Paiement;
import com.masterapply.backend.exception.BusinessException;
import com.masterapply.backend.exception.ForbiddenException;
import com.masterapply.backend.exception.ResourceNotFoundException;
import com.masterapply.backend.repository.CandidatureRepository;
import com.masterapply.backend.repository.InscriptionRepository;
import com.masterapply.backend.repository.PaiementRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InscriptionService {

    private final CandidatureRepository candidatureRepo;
    private final PaiementRepository paiementRepo;
    private final InscriptionRepository inscriptionRepo;

    @Transactional
    public InscriptionResponse confirmer(Long candidatureId, Long candidatId) {
        Candidature c = candidatureRepo.findById(candidatureId)
                .orElseThrow(() -> new ResourceNotFoundException("Candidature introuvable : id=" + candidatureId));
        if (!c.getCandidat().getId().equals(candidatId)) {
            throw new ForbiddenException("Accès refusé : cette candidature ne vous appartient pas");
        }

        // RG10 : candidature acceptée
        if (!Candidature.ACCEPTEE.equals(c.getStatut())) {
            throw new BusinessException(
                    "Inscription impossible : la candidature doit être acceptée (statut actuel : " + c.getStatut() + ")");
        }
        // RG10 : paiement réussi
        if (!paiementRepo.existsByCandidatureIdAndStatut(candidatureId, Paiement.REUSSI)) {
            throw new BusinessException("Inscription impossible : aucun paiement réussi pour cette candidature");
        }
        // RG11, RG12 : une seule inscription définitive par candidat
        if (inscriptionRepo.existsByCandidatIdAndStatut(candidatId, Inscription.CONFIRMEE)) {
            throw new BusinessException(
                    "Inscription refusée : vous avez déjà une inscription définitive dans un autre Master");
        }
        if (inscriptionRepo.existsByCandidatureId(candidatureId)) {
            throw new BusinessException("Une inscription existe déjà pour cette candidature");
        }

        // RG13 : le candidat confirme explicitement
        Inscription inscription = new Inscription();
        inscription.setCandidature(c);
        inscription.setCandidat(c.getCandidat());
        inscription.setStatut(Inscription.CONFIRMEE);
        try {
            inscription = inscriptionRepo.saveAndFlush(inscription);
        } catch (DataIntegrityViolationException e) {
            // filet de sécurité : la contrainte UNIQUE(candidat_id) de la base
            throw new BusinessException("Inscription refusée : vous avez déjà une inscription définitive");
        }
        return InscriptionResponse.from(inscription);
    }

    @Transactional(readOnly = true)
    public List<InscriptionResponse> lister(Long utilisateurId, boolean admin) {
        List<Inscription> liste = admin
                ? inscriptionRepo.findAllByOrderByDateInscriptionDesc()
                : inscriptionRepo.findByCandidatId(utilisateurId).map(List::of).orElse(List.of());
        return liste.stream().map(InscriptionResponse::from).toList();
    }
}