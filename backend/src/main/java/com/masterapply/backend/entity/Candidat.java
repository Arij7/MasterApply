package com.masterapply.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.*;

@Entity
@Table(name = "candidat")
@Getter @Setter @NoArgsConstructor
public class Candidat extends Utilisateur {

    private LocalDate dateNaissance;
    private String telephone;
    private String nationalite;
    private Double moyenneGenerale;

    @OneToMany(mappedBy = "candidat")
    @JsonIgnore
    private List<Candidature> candidatures = new ArrayList<>();
}