package com.BrainTech.Online_exam_App_server.service;

import com.BrainTech.Online_exam_App_server.model.ReponseEtudiant;

import java.util.List;
import java.util.Optional;

public interface ReponseEtudiantService {
    /**
     * Enregistre la réponse d'un étudiant à une question.
     * La logique interne gérera les différents types de questions.
     * @param reponseEtudiant L'objet ReponseEtudiant à sauvegarder.
     * @return La ReponseEtudiant persistante.
     */
    ReponseEtudiant saveReponse(ReponseEtudiant reponseEtudiant);

    /**
     * Récupère une réponse d'étudiant par son ID.
     * @param id L'ID de la réponse.
     * @return Un Optional contenant la ReponseEtudiant si trouvée, vide sinon.
     */
    Optional<ReponseEtudiant> getReponseById(Long id);

    /**
     * Récupère toutes les réponses pour un étudiant donné.
     * @param studentId L'ID de l'étudiant.
     * @return Une liste de ReponseEtudiant.
     */
    List<ReponseEtudiant> getReponsesByStudentId(Long studentId);

    /**
     * Récupère toutes les réponses pour une question donnée.
     * @param questionId L'ID de la question.
     * @return Une liste de ReponseEtudiant.
     */
    List<ReponseEtudiant> getReponsesByQuestionId(Long questionId);

    /**
     * Récupère toutes les réponses d'un étudiant pour un examen donné.
     * @param studentId L'ID de l'étudiant.
     * @param examenId L'ID de l'examen.
     * @return Une liste de ReponseEtudiant.
     */
    List<ReponseEtudiant> getReponsesByStudentAndExamen(Long studentId, Long examenId);

    /**
     * Met à jour une réponse existante.
     * @param id L'ID de la réponse à mettre à jour.
     * @param updatedReponse L'objet ReponseEtudiant avec les informations mises à jour.
     * @return La ReponseEtudiant mise à jour.
     */
    ReponseEtudiant updateReponse(Long id, ReponseEtudiant updatedReponse);

    /**
     * Supprime une réponse par son ID.
     * @param id L'ID de la réponse à supprimer.
     */
    void deleteReponse(Long id);

    /**
     * Calcule et enregistre le score pour une réponse spécifique (pour une correction manuelle ou automatique).
     * @param reponseId L'ID de la réponse à corriger.
     * @return La ReponseEtudiant avec le score mis à jour.
     */
    ReponseEtudiant corrigerReponse(Long reponseId);

    /**
     * Permet à un professeur de noter manuellement une réponse à une question conventionnelle ou mixte.
     * @param reponseId L'ID de la réponse à noter.
     * @param score Le score attribué par le professeur.
     * @return La ReponseEtudiant mise à jour avec le score manuel.
     * @throws ResourceNotFoundException si la réponse n'est pas trouvée.
     * @throws InvalidOperationException si le score est invalide ou si la question n'est pas de type conventionnel/mixte.
     */
    ReponseEtudiant noterManuellementReponse(Long reponseId, Double score);
}
