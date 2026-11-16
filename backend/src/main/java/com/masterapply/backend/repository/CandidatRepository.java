package com.masterapply.backend.repository;

import com.masterapply.backend.entity.Candidat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CandidatRepository extends JpaRepository<Candidat, Long> {
}