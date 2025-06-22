package com.BrainTech.Online_exam_App_server.service;

import com.BrainTech.Online_exam_App_server.model.Exam;

import java.util.List;
import java.util.Optional;

public interface ExamService {
    List<Exam> getAllExams();
    Optional<Exam> getExamById(Long id);
    Exam createExam(Exam exam);
    Exam updateExam(Long id, Exam exam);
    void deleteExam(Long id);
    // Sujet aux ajouts d'autres méthodes spécifiques à la logique métier des examens
}
