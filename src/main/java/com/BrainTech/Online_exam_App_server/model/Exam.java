package com.BrainTech.Online_exam_App_server.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@NoArgsConstructor // Constructeur sans arguments, requis par JPA
@AllArgsConstructor // Constructeur avec tous les arguments, utile avec Lombok
@Getter // Génère tous les getters
@Setter // Génère tous les setters
@Table(name = "examens") // Spécifie le nom de la table dans la base de données
public class Exam {

    @Id // Indique que 'id' est la clé primaire
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Stratégie d'auto-incrémentation pour la clé primaire
    private Long id;

    @Column(nullable = false, unique = true) // Le titre ne peut pas être nul et doit être unique
    private String titre; // Titre de l'examen (ex: "Examen de Java Avancé - Session 1")

    @Column(columnDefinition = "TEXT") // Permet d'avoir une colonne de texte plus longue
    private String description; // Description ou instructions de l'examen

    @Column(nullable = false) // La date de début ne peut pas être nulle
    private LocalDateTime dateDebut; // Date et heure de début de l'examen

    @Column(nullable = false) // La date de fin ne peut pas être nulle
    private LocalDateTime dateFin; // Date et heure de fin de l'examen

    @Column(nullable = false) // La durée en minutes ne peut pas être nulle
    private Integer dureeMinutes; // Durée maximale de l'examen en minutes

    private Double scoreMaximum; // Le score total maximum possible pour cet examen (somme des points des questions)

    private Boolean examenActif = true; // Indique si l'examen est actif et peut être passé

    // Relation OneToMany avec Question : Un examen peut avoir plusieurs questions.
    // 'mappedBy' indique que la relation est gérée par le champ 'examen' dans l'entité Question.
    // CASCADE.ALL signifie que si un examen est supprimé, ses questions associées le sont aussi.
    // orphanRemoval = true assure que les questions sont supprimées si elles ne sont plus liées à cet examen.
    // Utilise un Set pour l'unicité et de meilleures performances d'ajout/suppression.
    @OneToMany(mappedBy = "examen", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Question> questions = new HashSet<>(); // Contient toutes les questions, de toutes les séries, pour cet examen

    // Relation OneToMany avec StudentExamParticipation : Un examen a plusieurs participations.
    // Cette entité de jointure enrichie gère la relation entre Exam et Student, et stocke la 'serieAttribuee' à chaque étudiant pour cet examen.
    @OneToMany(mappedBy = "exam", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<StudentExamParticipation> participations = new HashSet<>(); // Les participations des étudiants à cet examen

    // --- Méthodes utilitaires pour la gestion des relations bidirectionnelles ---

    /**
     * Ajoute une question à cet examen et établit la relation bidirectionnelle.
     * @param question La question à ajouter.
     */
    public void addQuestion(Question question) {
        this.questions.add(question);
        question.setExamen(this); // Important pour maintenir la cohérence de la relation
    }

    /**
     * Retire une question de cet examen et rompt la relation bidirectionnelle.
     * @param question La question à retirer.
     */
    public void removeQuestion(Question question) {
        this.questions.remove(question);
        question.setExamen(null); // Important pour rompre la cohérence de la relation
    }

    /**
     * Ajoute une participation d'étudiant à cet examen.
     * La relation est gérée par StudentExamParticipation.
     * @param participation La participation à ajouter.
     */
    public void addParticipation(StudentExamParticipation participation) {
        this.participations.add(participation);
        participation.setExam(this); // Important pour maintenir la cohérence
    }

    /**
     * Retire une participation d'étudiant de cet examen.
     * La relation est gérée par StudentExamParticipation.
     * @param participation La participation à retirer.
     */
    public void removeParticipation(StudentExamParticipation participation) {
        this.participations.remove(participation);
        participation.setExam(null); // Important pour rompre la cohérence
    }
}
