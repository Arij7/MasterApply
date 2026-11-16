package com.masterapply.backend.repository;

import com.masterapply.backend.entity.Inscription;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InscriptionRepository extends JpaRepository<Inscription, Long> {

    Optional<Inscription> findByCandidatId(Long candidatId);

    boolean existsByCandidatIdAndStatut(Long candidatId, String statut);

    boolean existsByCandidatureId(Long candidatureId);

    List<Inscription> findAllByOrderByDateInscriptionDesc();

    long countByStatut(String statut);
}