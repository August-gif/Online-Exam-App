package com.BrainTech.Online_exam_App_server.service.implementation;

import com.BrainTech.Online_exam_App_server.exceptions.ResourceNotFoundException;
import com.BrainTech.Online_exam_App_server.model.Exam;
import com.BrainTech.Online_exam_App_server.repository.ExamRepository;
import com.BrainTech.Online_exam_App_server.service.ExamService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
public class ExamServiceImpl implements ExamService {
    private final ExamRepository examRepository;

    public ExamServiceImpl(ExamRepository examRepository) {
        this.examRepository = examRepository;
    }
    @Override
    public List<Exam> getAllExams() {
        return examRepository.findAll();
    }

    @Override
    public Optional<Exam> getExamById(Long id) {
        return examRepository.findById(id); // à refaire
    }

    @Override
    public Exam createExam(Exam exam) {
        return examRepository.save(exam);
    }

    @Override
    public Exam updateExam(Long id, Exam exam) {
        return examRepository.findById(id)
                .map(existingExam -> {
                    exam.setId(id); // Assurez-vous que l'ID est conservé
                    return examRepository.save(exam);
                })
                .orElseThrow(() -> new ResourceNotFoundException("Exam not found with ID: " + id)); // Ou lancez une exception
    }

    @Override
    public void deleteExam(Long id) {
        examRepository.deleteById(id);

    }

    // Implémentez d'autres méthodes spécifiques ici
}
