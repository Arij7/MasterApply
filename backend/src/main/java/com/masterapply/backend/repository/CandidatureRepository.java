package com.masterapply.backend.repository;

import com.masterapply.backend.entity.Candidature;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CandidatureRepository extends JpaRepository<Candidature, Long> {

    List<Candidature> findByCandidatIdOrderByDateCreationDesc(Long candidatId);

    List<Candidature> findAllByOrderByDateCreationDesc();

    boolean existsByCandidatIdAndCampagneIdAndMasterId(Long candidatId, Long campagneId, Long masterId);

    long countByCandidatIdAndCampagneIdAndStatutNot(Long candidatId, Long campagneId, String statut);
}