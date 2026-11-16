package com.masterapply.backend.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "utilisateur")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter @Setter @NoArgsConstructor
public abstract class Utilisateur {

    public static final String ROLE_CANDIDAT = "CANDIDAT";
    public static final String ROLE_ADMIN = "ADMIN";

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false) private String nom;
    @Column(nullable = false) private String prenom;
    @Column(nullable = false, unique = true) private String email;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(nullable = false)
    private String motDePasse;

    /** Valeurs autorisées : CANDIDAT, ADMIN */
    @Column(nullable = false, length = 20)
    private String role;
}