package com.BrainTech.Online_exam_App_server.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false,unique = true)
    private String matricule;
    @Column(nullable = false)
    private String nom;
    @Column(nullable = false)
    private String prenom;

    @Column(nullable = false, unique = true)
    private String email;
    @Column(nullable = false)
    private String motDePasse;

    @Column(nullable = false)
    private LocalDateTime dateInscription;

    @ManyToOne
    @JoinColumn(name = "promotion_id", nullable = false)
    private Promotion promotion;

    @OneToMany(mappedBy = "etudiant", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReponseEtudiant> reponseEtudiants;

    @ManyToMany
    @JoinTable(
            name = "etudiant_serie_examen",
            joinColumns = @JoinColumn(name = "etudiant_id"),
            inverseJoinColumns = @JoinColumn(name = "serie_examen_id")
    )
    private List<SerieExamen> serieExamen;  // nouvelle relation

}
