package com.masterapply.backend.repository;

import com.masterapply.backend.entity.Paiement;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaiementRepository extends JpaRepository<Paiement, Long> {

    List<Paiement> findByCandidatureIdOrderByDatePaiementDesc(Long candidatureId);

    List<Paiement> findAllByOrderByDatePaiementDesc();

    boolean existsByCandidatureIdAndStatut(Long candidatureId, String statut);

    long countByStatut(String statut);
}