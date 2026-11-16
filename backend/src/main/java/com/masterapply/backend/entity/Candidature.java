package com.masterapply.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.masterapply.backend.exception.BusinessException;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.*;

@Entity
@Table(name = "candidature")
@Getter @Setter @NoArgsConstructor
public class Candidature {

    public static final String BROUILLON = "BROUILLON";
    public static final String SOUMISE = "SOUMISE";
    public static final String EN_EXAMEN = "EN_EXAMEN";
    public static final String ACCEPTEE = "ACCEPTEE";
    public static final String REFUSEE = "REFUSEE";

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false) @JoinColumn(name = "candidat_id")
    private Candidat candidat;

    @ManyToOne(optional = false) @JoinColumn(name = "master_id")
    private Master master;

    @ManyToOne(optional = false) @JoinColumn(name = "campagne_id")
    private Campagne campagne;

    @ManyToOne @JoinColumn(name = "administrateur_id")
    private Administrateur administrateur;

    private LocalDateTime dateCreation = LocalDateTime.now();
    private LocalDateTime dateSoumission;
    private LocalDateTime dateDecision;

    /** Valeurs autorisées : BROUILLON, SOUMISE, EN_EXAMEN, ACCEPTEE, REFUSEE */
    @Column(nullable = false, length = 20)
    private String statut = BROUILLON;

    @Column(length = 2000) private String motivation;
    private Double noteDossier;
    private String commentaireAdmin;

    @OneToMany(mappedBy = "candidature", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Document> documents = new ArrayList<>();

    @OneToMany(mappedBy = "candidature")
    @JsonIgnore
    private List<Paiement> paiements = new ArrayList<>();

    // ---- Méthodes du diagramme : elles contrôlent les changements de statut ----

    public void soumettre() {
        if (!BROUILLON.equals(statut)) {
            throw new BusinessException(
                "Seul un brouillon peut être soumis (statut actuel : " + statut + ")");
        }
        this.statut = SOUMISE;
        this.dateSoumission = LocalDateTime.now();
    }

    public void accepter(Administrateur admin) {
        verifierDecisionPossible();
        this.statut = ACCEPTEE;
        this.administrateur = admin;
        this.dateDecision = LocalDateTime.now();
    }

    public void refuser(Administrateur admin) {
        verifierDecisionPossible();
        this.statut = REFUSEE;
        this.administrateur = admin;
        this.dateDecision = LocalDateTime.now();
    }

    private void verifierDecisionPossible() {
        if (!SOUMISE.equals(statut) && !EN_EXAMEN.equals(statut)) {
            throw new BusinessException(
                "Une décision n'est possible que sur une candidature soumise ou en examen");
        }
    }
}