package com.masterapply.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.*;

@Entity
@Table(name = "document")
@Getter @Setter @NoArgsConstructor
public class Document {

    public static final String CV = "CV";
    public static final String RELEVE_NOTES = "RELEVE_NOTES";
    public static final String DIPLOME = "DIPLOME";
    public static final String PIECE_IDENTITE = "PIECE_IDENTITE";
    public static final String LETTRE_MOTIVATION = "LETTRE_MOTIVATION";

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false) @JoinColumn(name = "candidature_id")
    private Candidature candidature;

    /** Valeurs autorisées : CV, RELEVE_NOTES, DIPLOME, PIECE_IDENTITE, LETTRE_MOTIVATION */
    @Column(nullable = false, length = 30)
    private String type;

    private String nomFichier;
    private String chemin;
    private LocalDateTime dateUpload = LocalDateTime.now();
}