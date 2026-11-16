package com.masterapply.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import lombok.*;

@Entity
@Table(name = "campagne")
@Getter @Setter @NoArgsConstructor
public class Campagne {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String libelle;

    private LocalDate dateDebut;
    private LocalDate dateFin;
    private boolean active = true;
    private int maxCandidatures = 3;
}