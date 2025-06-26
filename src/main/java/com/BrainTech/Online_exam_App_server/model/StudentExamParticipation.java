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
@Table(name = "student_exam_participations") // Nom de la table dans la base de données
public class StudentExamParticipation  {
    @Id // Indique que 'id' est la clé primaire
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Stratégie d'auto-incrémentation
    private Long id;

    // Relation ManyToOne vers Student : Une participation est liée à un seul étudiant.
    // 'nullable = false' signifie qu'une participation doit toujours être liée à un étudiant.
    @ManyToOne(fetch = FetchType.LAZY) // Chargement paresseux pour éviter de charger l'étudiant entier inutilement
    @JoinColumn(name = "student_id", nullable = false) // Clé étrangère vers la table des étudiants
    private Student student;

    // Relation ManyToOne vers Exam : Une participation est liée à un seul examen.
    @ManyToOne(fetch = FetchType.LAZY) // Chargement paresseux
    @JoinColumn(name = "exam_id", nullable = false) // Clé étrangère vers la table des examens
    private Exam exam;

    @Column(nullable = true) // La série attribuée ne peut pas être nulle
    private String serieAttribuee; // Le tag de la série (ex: "A", "B", "C") attribuée à cet étudiant pour cet examen

    private LocalDateTime debutParticipation; // Date et heure de début de la participation de l'étudiant à cet examen
    private LocalDateTime finParticipation; // Date et heure de soumission des réponses par l'étudiant
    private Double scoreFinalExamen; // Le score total obtenu par cet étudiant pour cet examen
    private Boolean examenTermine = false; // Indique si l'étudiant a terminé et soumis ses réponses

    // Constructeur utile pour la création initiale d'une participation
    public StudentExamParticipation(Student student, Exam exam, String serieAttribuee) {
        this.student = student;
        this.exam = exam;
        this.serieAttribuee = serieAttribuee;
        // Les autres champs (dates, score, terminé) seront initialisés à null ou leurs valeurs par défaut.
    }

}
