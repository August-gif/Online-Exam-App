package com.BrainTech.Online_exam_App_server.service;

import com.BrainTech.Online_exam_App_server.model.Exam;
import com.BrainTech.Online_exam_App_server.model.Question;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ExamService {
    Exam createExam(Exam exam); // Créer un nouvel examen (sans association directe de série ici)
    Optional<Exam> getExamById(Long id);
    List<Exam> getAllExams();
    Exam updateExam(Long id, Exam examDetails); // Mettre à jour les détails d'un examen
    void deleteExam(Long id);

    // Méthodes pour gérer les questions d'un examen
    Exam addQuestionToExam(Long examId, Long questionId);
    Exam removeQuestionFromExam(Long examId, Long questionId);

    // Méthodes pour récupérer les questions pour un étudiant spécifique (tenant compte de la série)
    Set<Question> getExamQuestionsForStudent(Long examId, Long studentId);

    // Méthodes pour obtenir les examens par statut
    List<Exam> getActiveExams();
    List<Exam> getUpcomingExams();
    List<Exam> getPastExams();

    // Méthode pour calculer le score total maximum d'un examen (somme des points de toutes ses questions)
    Double calculateTotalScoreForExam(Long examId);

    // Méthode pour attribuer une série à un étudiant et démarrer sa participation (logique déplacée vers StudentExamParticipationService)
    // Exam assignSerieToStudent(Long examId, Long studentId, String serieTag); // Cette logique sera dans StudentExamParticipationService
}
