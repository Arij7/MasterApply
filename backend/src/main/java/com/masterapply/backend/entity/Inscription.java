package com.masterapply.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.*;

@Entity
@Table(name = "inscription")
@Getter @Setter @NoArgsConstructor
public class Inscription {

    public static final String A_FINALISER = "A_FINALISER";
    public static final String CONFIRMEE = "CONFIRMEE";
    public static final String ANNULEE = "ANNULEE";

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false) @JoinColumn(name = "candidature_id", unique = true)
    private Candidature candidature;

    @OneToOne(optional = false) @JoinColumn(name = "candidat_id", unique = true)
    private Candidat candidat;

    private LocalDateTime dateInscription = LocalDateTime.now();

    /** Valeurs autorisées : A_FINALISER, CONFIRMEE, ANNULEE */
    @Column(nullable = false, length = 20)
    private String statut = A_FINALISER;
}