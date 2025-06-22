package com.BrainTech.Online_exam_App_server.service;

import com.BrainTech.Online_exam_App_server.model.ReponseQuestionEtudiant;

import java.util.List;
import java.util.Optional;

public interface ReponseQuestionEtudiantService {
    /**
     * Crée une nouvelle réponse à une question spécifique par un étudiant.
     * Cette méthode est généralement appelée de manière interne lors de la soumission globale de l'examen.
     *
     * @param reponseQuestionEtudiant L'objet ReponseQuestionEtudiant à créer.
     * @return La ReponseQuestionEtudiant persistante.
     */
    ReponseQuestionEtudiant createReponseQuestionEtudiant(ReponseQuestionEtudiant reponseQuestionEtudiant);

    /**
     * Récupère une réponse à une question spécifique par son ID.
     *
     * @param id L'ID de la ReponseQuestionEtudiant.
     * @return Un Optional contenant la ReponseQuestionEtudiant si trouvée.
     */
    Optional<ReponseQuestionEtudiant> getReponseQuestionEtudiantById(Long id);

    /**
     * Récupère toutes les réponses individuelles aux questions soumises par les étudiants.
     *
     * @return Une liste de toutes les ReponseQuestionEtudiant.
     */
    List<ReponseQuestionEtudiant> getAllReponseQuestionEtudiants();

    /**
     * Récupère toutes les réponses aux questions associées à une soumission d'examen principale.
     *
     * @param reponseEtudiantId L'ID de la ReponseEtudiant parente.
     * @return Une liste des ReponseQuestionEtudiant liées à cette soumission.
     */
    List<ReponseQuestionEtudiant> getReponsesByReponseEtudiantId(Long reponseEtudiantId);

    /**
     * Récupère toutes les réponses soumises pour une question spécifique, peu importe l'étudiant ou l'examen.
     *
     * @param questionId L'ID de la question.
     * @return Une liste des ReponseQuestionEtudiant pour cette question.
     */
    List<ReponseQuestionEtudiant> getReponsesByQuestionId(Long questionId);

    /**
     * Met à jour une réponse à une question spécifique.
     * Ceci est utile pour la correction manuelle des questions ouvertes par un professeur.
     *
     * @param id L'ID de la ReponseQuestionEtudiant à mettre à jour.
     * @param updatedReponseQuestionEtudiant L'objet ReponseQuestionEtudiant avec les données modifiées.
     * @return La ReponseQuestionEtudiant mise à jour.
     */
    ReponseQuestionEtudiant updateReponseQuestionEtudiant(Long id, ReponseQuestionEtudiant updatedReponseQuestionEtudiant);

    /**
     * Supprime une réponse à une question spécifique.
     * Cela peut être utile si une erreur a été commise lors de la saisie d'une réponse individuelle.
     * Cependant, il faut être vigilant car cela affecte la soumission globale.
     *
     * @param id L'ID de la ReponseQuestionEtudiant à supprimer.
     */
    void deleteReponseQuestionEtudiant(Long id);
}
