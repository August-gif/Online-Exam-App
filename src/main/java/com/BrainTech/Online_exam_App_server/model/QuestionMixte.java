package com.BrainTech.Online_exam_App_server.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@DiscriminatorValue("MIXTE")  // Indique que cette classe est un type spécifique de Question
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder // Permet de construire cette classe en utilisant les champs de la classe parente Question
public class QuestionMixte extends Question{
    // Le champ 'intitule' de la classe parente 'Question' contient l'énoncé complet.
    // Pas besoin de 'partieConventionelleIntitule' ici.

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "question_mixte_option", // Table de jointure spécifique pour cette relation
            joinColumns = @JoinColumn(name = "question_mixte_id"),
            inverseJoinColumns = @JoinColumn(name = "option_id")
    )
    private Set<Option> optionsPartieChoixMultiple = new HashSet<>();

    // --- NOUVELLES MÉTHODES AJOUTÉES ICI ---
    public void addOptionPartieChoixMultiple(Option option) {
        if (optionsPartieChoixMultiple == null) {
            optionsPartieChoixMultiple = new HashSet<>();
        }
        this.optionsPartieChoixMultiple.add(option);
        // Pour ManyToMany, la relation bidirectionnelle est souvent gérée implicitement par la table de jointure.
        // Si Option avait un @ManyToMany List<QuestionMixte> questionsMixtes, tu ferais aussi :
        // option.getQuestionsMixtes().add(this);
    }
    public void removeOptionPartieChoixMultiple(Option option) {
        if (optionsPartieChoixMultiple != null) {
            this.optionsPartieChoixMultiple.remove(option);
            // Si bidirectionnel :
            // option.getQuestionsMixtes().remove(this);
        }
    }

}
