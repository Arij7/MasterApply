package com.masterapply.backend.service;

import com.masterapply.backend.dto.DashboardResponse;
import com.masterapply.backend.dto.DashboardResponse.StatJour;
import com.masterapply.backend.dto.DashboardResponse.StatMaster;
import com.masterapply.backend.entity.Candidature;
import com.masterapply.backend.entity.Inscription;
import com.masterapply.backend.entity.Paiement;
import com.masterapply.backend.repository.CandidatureRepository;
import com.masterapply.backend.repository.InscriptionRepository;
import com.masterapply.backend.repository.PaiementRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final CandidatureRepository candidatureRepo;
    private final PaiementRepository paiementRepo;
    private final InscriptionRepository inscriptionRepo;

    @Transactional(readOnly = true)
    public DashboardResponse calculer() {
        long total = candidatureRepo.count();
        long brouillons = candidatureRepo.countByStatut(Candidature.BROUILLON);
        long enAttente = candidatureRepo.countByStatutIn(
                List.of(Candidature.SOUMISE, Candidature.EN_EXAMEN));
        long acceptees = candidatureRepo.countByStatut(Candidature.ACCEPTEE);
        long refusees = candidatureRepo.countByStatut(Candidature.REFUSEE);
        long paiementsReussis = paiementRepo.countByStatut(Paiement.REUSSI);
        long inscriptions = inscriptionRepo.countByStatut(Inscription.CONFIRMEE);

        List<StatMaster> parMaster = candidatureRepo.compterParMaster().stream()
                .map(l -> new StatMaster((String) l[1], ((Number) l[2]).longValue()))
                .toList();

        Map<LocalDate, Long> jours = new TreeMap<>();
        for (LocalDateTime d : candidatureRepo.datesSoumission()) {
            jours.merge(d.toLocalDate(), 1L, Long::sum);
        }
        List<StatJour> parJour = jours.entrySet().stream()
                .map(e -> new StatJour(e.getKey(), e.getValue()))
                .toList();

        return new DashboardResponse(total, brouillons, enAttente, acceptees, refusees,
                paiementsReussis, inscriptions, parMaster, parJour);
    }
}