package com.BrainTech.Online_exam_App_server.model;

import com.BrainTech.Online_exam_App_server.enums.QuestionType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@NoArgsConstructor
@AllArgsConstructor
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type",discriminatorType = DiscriminatorType.STRING)
@Getter
@Setter
public abstract class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String intitule;
    @Column(precision = 5)
    private Double points;  //ponderation de chaque question
    @ManyToOne
    @JoinColumn(name = "examen_id", nullable = false)
    private Exam examen;
    @Enumerated(EnumType.STRING) // Indique à JPA de stocker le nom de l'enum (e.g., "MIXTE") en DB
    @Column(insertable = false, updatable = false) // Géré automatiquement par le @DiscriminatorColumn
    private QuestionType type; // Le champ est maintenant de type QuestionType enum

    @Column(length = 512) // Longueur suffisante pour une URL ou un chemin de fichier
    private String attachmentUrl; // URL du fichier annexé (PDF, image, etc.)
    // Sera null si aucune annexe n'est présente

    // Nouvelle propriété pour identifier la série de la question
    @Column(nullable = true)
    private String serieExamenTag; // Ex: "A", "B", "C", ou "Serie_1", "Serie_2"

}


