package com.masterapply.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.*;

@Entity
@Table(name = "recu")
@Getter @Setter @NoArgsConstructor
public class Recu {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false) @JoinColumn(name = "paiement_id", unique = true)
    private Paiement paiement;

    @Column(nullable = false, unique = true)
    private String numero;

    private LocalDateTime dateGeneration = LocalDateTime.now();
    private String cheminPdf;
}