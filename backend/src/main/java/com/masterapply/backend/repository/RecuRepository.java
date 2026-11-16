package com.masterapply.backend.repository;

import com.masterapply.backend.entity.Recu;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecuRepository extends JpaRepository<Recu, Long> {

    List<Recu> findByPaiementCandidatureCandidatIdOrderByDateGenerationDesc(Long candidatId);

    List<Recu> findAllByOrderByDateGenerationDesc();
}