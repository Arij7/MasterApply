package com.masterapply.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.*;

@Entity
@Table(name = "paiement")
@Getter @Setter @NoArgsConstructor
public class Paiement {

    public static final String EN_ATTENTE = "EN_ATTENTE";
    public static final String REUSSI = "REUSSI";
    public static final String ECHOUE = "ECHOUE";

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false) @JoinColumn(name = "candidature_id")
    private Candidature candidature;

    private BigDecimal montant;
    private LocalDateTime datePaiement = LocalDateTime.now();

    /** Valeurs autorisées : EN_ATTENTE, REUSSI, ECHOUE */
    @Column(nullable = false, length = 20)
    private String statut = EN_ATTENTE;

    private String referenceTransaction;

    @OneToOne(mappedBy = "paiement")
    @JsonIgnore
    private Recu recu;
}