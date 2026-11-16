package com.masterapply.backend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "administrateur")
@Getter @Setter @NoArgsConstructor
public class Administrateur extends Utilisateur {

    private String matricule;
}