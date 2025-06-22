package com.BrainTech.Online_exam_App_server.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@DiscriminatorValue("CHOIX_MULTIPLE") // La valeur dans la colonne 'type' de la DB pour ce type de question
@Getter
@Setter
@SuperBuilder // Permet d'utiliser le builder de la classe parente Question

public class QuestionChoixMultiple extends Question{
    // Liste des options possibles pour cette question à choix multiples
    // Chaque option est spécifique à cette question
    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Option> options = new ArrayList<>();

    // Un champ pour indiquer si c'est une question à réponse unique ou multiple
    // (Par exemple, un QCM où l'on coche une seule réponse ou plusieurs)
    private boolean estChoixMultiplePermis; // true si l'étudiant peut cocher plusieurs options

    // Méthodes utilitaires pour gérer la liste d'options
    public void addOption(Option option) {
        this.options.add(option);
        option.setQuestion(this); // Assurez-vous que la relation bidirectionnelle est définie
    }
    public void removeOption(Option option) {
        this.options.remove(option);
        option.setQuestion(null); // Dissocier l'option de cette question
    }




}
