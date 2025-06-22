package com.BrainTech.Online_exam_App_server.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Exam {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titre;

    private String description;

    @Column(nullable = false)
    private LocalDateTime dateCreation;

    @Column(nullable = false)
    private LocalDateTime dateOuverture;

    @Column(nullable = false)
    private LocalDateTime dateFermeture;

    @Column(nullable = false)
    private Integer duréeMinutes;

    private boolean estPublie = false;

    @ManyToOne
    @JoinColumn(name = "professeur_id", nullable = false)
    private Professsor professeurCreateur;

    @ManyToMany
    @JoinTable(
            name = "examen_promotion",
            joinColumns = @JoinColumn(name = "examen_id"),
            inverseJoinColumns = @JoinColumn(name = "promotion_id")
    )
    private List<Promotion> promotionsCibles; // Le nom doit correspondre à la valeur de mappedBy dans Promotion

    @ManyToMany(mappedBy = "examens")
    private List<SerieExamen> sériesExamen;

    @OneToMany(mappedBy = "examen", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Question> questions;

    @OneToMany(mappedBy = "examen", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReponseEtudiant> reponseEtudiants;



}
