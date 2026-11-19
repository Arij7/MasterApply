package com.masterapply.backend.repository;

import com.masterapply.backend.entity.Candidature;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CandidatureRepository extends JpaRepository<Candidature, Long> {

    List<Candidature> findByCandidatIdOrderByDateCreationDesc(Long candidatId);

    List<Candidature> findAllByOrderByDateCreationDesc();

    boolean existsByCandidatIdAndCampagneIdAndMasterId(Long candidatId, Long campagneId, Long masterId);

    long countByCandidatIdAndCampagneIdAndStatutNot(Long candidatId, Long campagneId, String statut);

    // ----- statistiques du dashboard -----

    long countByStatut(String statut);

    long countByStatutIn(Collection<String> statuts);

    @Query("select m.id, m.intitule, count(c) from Candidature c join c.master m "
            + "where c.statut <> 'BROUILLON' group by m.id, m.intitule order by count(c) desc")
    List<Object[]> compterParMaster();

    @Query("select c.dateSoumission from Candidature c where c.dateSoumission is not null")
    List<LocalDateTime> datesSoumission();
}