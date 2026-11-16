package com.masterapply.backend.service;

import com.masterapply.backend.dto.PaiementResponse;
import com.masterapply.backend.entity.Candidature;
import com.masterapply.backend.entity.Inscription;
import com.masterapply.backend.entity.Paiement;
import com.masterapply.backend.entity.Recu;
import com.masterapply.backend.exception.BusinessException;
import com.masterapply.backend.exception.ForbiddenException;
import com.masterapply.backend.exception.ResourceNotFoundException;
import com.masterapply.backend.repository.CandidatureRepository;
import com.masterapply.backend.repository.InscriptionRepository;
import com.masterapply.backend.repository.PaiementRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaiementService {

    private final CandidatureRepository candidatureRepo;
    private final PaiementRepository paiementRepo;
    private final InscriptionRepository inscriptionRepo;
    private final RecuService recuService;

    @Transactional
    public PaiementResponse simuler(Long candidatureId, Long candidatId, boolean forcerEchec) {
        Candidature c = charger(candidatureId);
        if (!c.getCandidat().getId().equals(candidatId)) {
            throw new ForbiddenException("Accès refusé : cette candidature ne vous appartient pas");
        }

        // RG06 : paiement uniquement après acceptation
        if (!Candidature.ACCEPTEE.equals(c.getStatut())) {
            throw new BusinessException(
                    "Paiement interdit : la candidature doit être acceptée (statut actuel : " + c.getStatut() + ")");
        }
        if (paiementRepo.existsByCandidatureIdAndStatut(candidatureId, Paiement.REUSSI)) {
            throw new BusinessException("Cette candidature a déjà été payée");
        }
        if (inscriptionRepo.existsByCandidatIdAndStatut(candidatId, Inscription.CONFIRMEE)) {
            throw new BusinessException("Paiement inutile : vous avez déjà une inscription définitive confirmée");
        }

        // RG07 : simulation, aucune transaction réelle
        Paiement p = new Paiement();
        p.setCandidature(c);
        p.setMontant(c.getMaster().getFrais());
        p.setReferenceTransaction("SIM-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        p.setStatut(forcerEchec ? Paiement.ECHOUE : Paiement.REUSSI);
        p = paiementRepo.saveAndFlush(p);

        // RG08 : reçu uniquement si le paiement a réussi
        if (Paiement.REUSSI.equals(p.getStatut())) {
            Recu recu = recuService.creerPourPaiement(p);
            p.setRecu(recu);
        }
        return PaiementResponse.from(p);
    }

    @Transactional(readOnly = true)
    public List<PaiementResponse> listerParCandidature(Long candidatureId, Long utilisateurId, boolean admin) {
        Candidature c = charger(candidatureId);
        if (!admin && !c.getCandidat().getId().equals(utilisateurId)) {
            throw new ForbiddenException("Accès refusé : cette candidature ne vous appartient pas");
        }
        return paiementRepo.findByCandidatureIdOrderByDatePaiementDesc(candidatureId)
                .stream().map(PaiementResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public List<PaiementResponse> listerTous() {
        return paiementRepo.findAllByOrderByDatePaiementDesc()
                .stream().map(PaiementResponse::from).toList();
    }

    private Candidature charger(Long id) {
        return candidatureRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Candidature introuvable : id=" + id));
    }
}