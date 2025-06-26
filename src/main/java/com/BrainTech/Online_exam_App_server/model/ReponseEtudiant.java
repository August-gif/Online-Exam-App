package com.BrainTech.Online_exam_App_server.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "Reponse_etudiant") //Nom de table explicite
public class ReponseEtudiant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private LocalDateTime dateReponse;
    private Double scoreObtenu; // Score obtenu pour CETTE réponse à CETTE question

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "etudiant_id", nullable = false) // Nom de colonne adapté pour Student
    private Student student;

    @Column(columnDefinition = "TEXT") // Pour les réponses texte (conventionnelles ou partie texte des mixtes)
    private String reponseTextuelle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;


    @ManyToMany(fetch = FetchType.LAZY) // Pour les questions QCM et la partie QCM des mixtes
    @JoinTable(
            name = "reponse_etudiant_options_selectionnees", // Table de jointure pour les options
            joinColumns = @JoinColumn(name = "reponse_etudiant_id"),
            inverseJoinColumns = @JoinColumn(name = "option_id")
    )
    private Set<Option> optionsSelectionnees = new HashSet<>();

    // Méthodes utilitaires pour ajouter/retirer des options (si tu as besoin de gérer la collection unitairement)
    public void addOption(Option option) {
        this.optionsSelectionnees.add(option);
    }

    public void removeOption(Option option) {
        this.optionsSelectionnees.remove(option);
    }


}
