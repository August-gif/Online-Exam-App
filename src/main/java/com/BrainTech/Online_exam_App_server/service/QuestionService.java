package com.BrainTech.Online_exam_App_server.service;

import com.BrainTech.Online_exam_App_server.model.Question;

import java.util.List;
import java.util.Optional;

public interface QuestionService {
    /**
     * Crée une nouvelle question. La question peut être de type CHOIX_MULTIPLE, CONVENTIONNELLE ou MIXTE.
     * Les détails spécifiques à chaque type (options pour QCM/Mixte) doivent être inclus dans l'objet Question.
     *
     * @param question L'objet Question à créer (peut être QuestionChoixMultiple, QuestionConventionnelle, ou QuestionMixte).
     * @return La Question persistante.
     */
    Question createQuestion(Question question);

    /**
     * Récupère une question par son ID.
     *
     * @param id L'ID de la question.
     * @return Un Optional contenant la Question si trouvée, vide sinon.
     */
    Optional<Question> getQuestionById(Long id);

    /**
     * Récupère toutes les questions existantes.
     *
     * @return Une liste de toutes les Questions.
     */
    List<Question> getAllQuestions();

    /**
     * Récupère toutes les questions pour un examen spécifique.
     *
     * @param examId L'ID de l'examen.
     * @return Une liste de Questions appartenant à cet examen.
     */
    List<Question> getQuestionsByExamId(Long examId);

    /**
     * Met à jour une question existante.
     * La mise à jour doit gérer les spécificités de chaque type de question.
     *
     * @param id L'ID de la question à mettre à jour.
     * @param updatedQuestion L'objet Question avec les informations mises à jour.
     * @return La Question mise à jour.
     */
    Question updateQuestion(Long id, Question updatedQuestion);

    /**
     * Supprime une question par son ID.
     *
     * @param id L'ID de la question à supprimer.
     */
    void deleteQuestion(Long id);
}
