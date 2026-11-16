package com.masterapply.backend.repository;

import com.masterapply.backend.entity.Campagne;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CampagneRepository extends JpaRepository<Campagne, Long> {
    Optional<Campagne> findFirstByActiveTrueOrderByIdDesc();
}