package com.BrainTech.Online_exam_App_server.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;

import java.util.Set;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "students")
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false)
    private String prenom;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private LocalDate dateNaissance;

    // Nouvelle relation OneToMany avec StudentExamParticipation.
    // Cette relation remplace l'ancienne ManyToMany directe avec Exam.
    // 'mappedBy' indique que la relation est gérée par le champ 'student' dans l'entité StudentExamParticipation.
    // CASCADE.ALL signifie que si un étudiant est supprimé, toutes ses participations à des examens le sont aussi.
    // orphanRemoval = true assure que les participations sont supprimées si elles ne sont plus liées à cet étudiant.
    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<StudentExamParticipation> participations = new HashSet<>(); // Les participations de cet étudiant à divers examens

    // --- Méthodes utilitaires pour la gestion des relations bidirectionnelles ---

    /**
     * Ajoute une participation à cet étudiant et établit la relation bidirectionnelle.
     * @param participation La participation à ajouter.
     */
    public void addParticipation(StudentExamParticipation participation) {
        this.participations.add(participation);
        participation.setStudent(this); // Important pour maintenir la cohérence
    }

    /**
     * Retire une participation de cet étudiant et rompt la relation bidirectionnelle.
     * @param participation La participation à retirer.
     */
    public void removeParticipation(StudentExamParticipation participation) {
        this.participations.remove(participation);
        participation.setStudent(null); // Important pour rompre la cohérence
    }
}

