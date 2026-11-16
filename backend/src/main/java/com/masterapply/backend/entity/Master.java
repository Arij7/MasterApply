package com.masterapply.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import lombok.*;

@Entity
@Table(name = "master")
@Getter @Setter @NoArgsConstructor
public class Master {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "L'intitulé est obligatoire")
    @Column(nullable = false)
    private String intitule;

    @Column(length = 2000) private String description;
    @Column(length = 2000) private String conditions;

    @Positive(message = "La capacité doit être positive")
    private int capacite;

    @NotNull(message = "Les frais sont obligatoires")
    @PositiveOrZero(message = "Les frais ne peuvent pas être négatifs")
    private BigDecimal frais;
}