package com.BrainTech.Online_exam_App_server.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@DiscriminatorValue("CONVENTIONNELLE" ) // La valeur dans la colonne 'type' de la DB pour ce type de question
@SuperBuilder // Permet d'utiliser le builder de la classe parente Question
public class QuestionConventionnelle extends Question{
    // Aucun champ spécifique n'est généralement nécessaire ici,
    // car le contenu de la question est dans 'intitule' de la classe parente
    // et la réponse de l'étudiant sera un texte libre géré dans ReponseQuestionEtudiant.

    // nous pourriez ajouter des champs si nécessaire, par exemple:
    // @Column(columnDefinition = "TEXT")
    // private String instructionsSupplementaires;
}
