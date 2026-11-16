package com.masterapply.backend.repository;

import com.masterapply.backend.entity.Master;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MasterRepository extends JpaRepository<Master, Long> {
}