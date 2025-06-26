package com.BrainTech.Online_exam_App_server.service.implementation;

import com.BrainTech.Online_exam_App_server.exceptions.InvalidOperationException;
import com.BrainTech.Online_exam_App_server.exceptions.ResourceNotFoundException;
import com.BrainTech.Online_exam_App_server.model.*;
import com.BrainTech.Online_exam_App_server.repository.ExamRepository;
import com.BrainTech.Online_exam_App_server.repository.ReponseEtudiantRepository;
import com.BrainTech.Online_exam_App_server.repository.StudentExamParticipationRepository;
import com.BrainTech.Online_exam_App_server.repository.StudentRepository;
import com.BrainTech.Online_exam_App_server.service.StudentExamparticipationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class StudentExamParticipationServiceImpl implements StudentExamparticipationService {

   private final StudentExamParticipationRepository studentExamParticipationRepository;
    private final StudentRepository studentRepository;
    private final ExamRepository examRepository;
    private final ReponseEtudiantRepository reponseEtudiantRepository;

    /**
     * Inscrit un étudiant à un examen en créant une participation.
     * Cette méthode ne démarre pas encore la session ni n'attribue de série.
     */
    @Override
    @Transactional
    public StudentExamParticipation enrollStudentInExam(Long studentId, Long examId) {
        log.info("Tentative d'inscription de l'étudiant ID {} à l'examen ID {}.", studentId, examId);
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Étudiant non trouvé avec l'ID: " + studentId));
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new ResourceNotFoundException("Examen non trouvé avec l'ID: " + examId));

        if (studentExamParticipationRepository.findByStudentIdAndExamId(studentId, examId).isPresent()) {
            throw new InvalidOperationException("L'étudiant ID " + studentId + " est déjà inscrit à l'examen ID " + examId + ".");
        }
        // Au moment de l'inscription, la série n'est pas encore attribuée, elle le sera au démarrage de la session.
        StudentExamParticipation participation = new StudentExamParticipation(student, exam, null);
        StudentExamParticipation savedParticipation = studentExamParticipationRepository.save(participation);
        log.info("Étudiant ID {} inscrit à l'examen ID {} avec la participation ID {}.", studentId, examId, savedParticipation.getId());
        return savedParticipation;
    }

    /**
     * Démarre la session d'examen pour un étudiant.
     * Attribue une série si l'examen utilise des séries, et enregistre l'heure de début.
     */
    @Override
    @Transactional
    public StudentExamParticipation startExamSession(Long studentId, Long examId) {
        log.info("Tentative de démarrage de la session d'examen pour l'étudiant ID {} et l'examen ID {}.", studentId, examId);
        StudentExamParticipation participation = studentExamParticipationRepository
                .findByStudentIdAndExamId(studentId, examId)
                .orElseThrow(() -> new ResourceNotFoundException("Participation non trouvée pour l'étudiant ID " + studentId + " et l'examen ID " + examId + ". L'étudiant doit être inscrit d'abord."));
        if (participation.getDebutParticipation() != null) {
            throw new InvalidOperationException("La session d'examen a déjà commencé pour l'étudiant ID " + studentId + " et l'examen ID " + examId + ".");
        }
        if (participation.getExamenTermine()) {
            throw new InvalidOperationException("L'examen est déjà terminé pour cet étudiant.");
        }

        Exam exam = participation.getExam();

        // Vérifier si l'examen est dans sa période active
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(exam.getDateDebut()) || now.isAfter(exam.getDateFin())) {
            throw new InvalidOperationException("L'examen ID " + examId + " n'est pas actif pour le moment.");
        }

        // Logique d'attribution de série (si applicable)
        String serieAttribuee = null;
        Set<String> availableSeriesTags = exam.getQuestions().stream()
                .map(Question::getSerieExamenTag)
                .filter(tag -> tag != null && !tag.isEmpty())
                .collect(Collectors.toSet());

        if (!availableSeriesTags.isEmpty()) {
            // Si des séries existent, attribuer une série (ici, on prend la première pour l'exemple,
            // mais tu pourrais implémenter une logique de distribution aléatoire ou équitable).
            serieAttribuee = availableSeriesTags.iterator().next();
            log.info("Série d'examen '{}' attribuée à l'étudiant ID {} pour l'examen ID {}.", serieAttribuee, studentId, examId);
        } else {
            log.info("Aucune série de questions détectée pour l'examen ID {}. L'étudiant recevra toutes les questions sans tag de série.", examId);
        }

        participation.setSerieAttribuee(serieAttribuee);
        participation.setDebutParticipation(now);
        StudentExamParticipation updatedParticipation = studentExamParticipationRepository.save(participation);
        log.info("Session d'examen démarrée pour l'étudiant ID {} et l'examen ID {}.", studentId, examId);
        return updatedParticipation;
    }

    @Override
    @Transactional
    public StudentExamParticipation submitExam(Long studentId, Long examId) {
        log.info("Tentative de soumission de l'examen pour l'étudiant ID {} et l'examen ID {}.", studentId, examId);
        StudentExamParticipation participation = studentExamParticipationRepository
                .findByStudentIdAndExamId(studentId, examId)
                .orElseThrow(() -> new ResourceNotFoundException("Participation non trouvée pour l'étudiant ID " + studentId + " et l'examen ID " + examId + "."));

        if (participation.getExamenTermine()) {
            throw new InvalidOperationException("L'examen a déjà été soumis par l'étudiant ID " + studentId + " pour l'examen ID " + examId + ".");
        }

        LocalDateTime now = LocalDateTime.now();
        participation.setFinParticipation(now);
        participation.setExamenTermine(true);

        // Calculer le score final de l'étudiant pour cette participation
        Double finalScore = calculateStudentExamScore(studentId, examId);
        participation.setScoreFinalExamen(finalScore);

        StudentExamParticipation submittedParticipation = studentExamParticipationRepository.save(participation);
        log.info("Examen soumis par l'étudiant ID {} pour l'examen ID {}. Score final: {}", studentId, examId, finalScore);
        return submittedParticipation;

    }

    @Override
    @Transactional(readOnly = true)
    public Optional<StudentExamParticipation> getParticipationById(Long id) {
        log.debug("Récupération de la participation avec l'ID: {}", id);
        return studentExamParticipationRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<StudentExamParticipation> getParticipationByStudentAndExam(Long studentId, Long examId) {
        log.debug("Récupération de la participation pour l'étudiant ID {} et l'examen ID {}.", studentId, examId);
        return studentExamParticipationRepository.findByStudentIdAndExamId(studentId, examId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentExamParticipation> getAllParticipations() {
        log.debug("Récupération de toutes les participations aux examens.");
        return studentExamParticipationRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentExamParticipation> getParticipationsByStudent(Long studentId) {
        log.debug("Récupération des participations pour l'étudiant ID: {}", studentId);
        if (!studentRepository.existsById(studentId)) {
            throw new ResourceNotFoundException("Étudiant non trouvé avec l'ID: " + studentId);
        }
        return studentExamParticipationRepository.findByStudentId(studentId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentExamParticipation> getParticipationsByExam(Long examId) {
        log.debug("Récupération des participations pour l'examen ID: {}", examId);
        if (!examRepository.existsById(examId)) {
            throw new ResourceNotFoundException("Examen non trouvé avec l'ID: " + examId);
        }
        return studentExamParticipationRepository.findByExamId(examId);
    }

    /**
     * Calcule le score total obtenu par un étudiant pour un examen donné,
     * en se basant sur les questions de la série qui lui a été attribuée (ou non).
     */

    @Override
    @Transactional(readOnly = true)
    public Double calculateStudentExamScore(Long studentId, Long examId) {
        log.info("Calcul du score de l'étudiant ID {} pour l'examen ID {}.", studentId, examId);
        StudentExamParticipation participation = studentExamParticipationRepository
                .findByStudentIdAndExamId(studentId, examId)
                .orElseThrow(() -> new ResourceNotFoundException("Participation non trouvée pour l'étudiant ID " + studentId + " et l'examen ID " + examId + "."));

        String serieAttribuee = participation.getSerieAttribuee();

        // Récupérer toutes les réponses de l'étudiant pour les questions de cet examen
        List<ReponseEtudiant> studentResponses = reponseEtudiantRepository.findByStudentIdAndQuestion_ExamenId(studentId, examId);

        // Filtrer les réponses en fonction de la série attribuée
        double totalScore = studentResponses.stream()
                .filter(reponse -> {
                    Question question = reponse.getQuestion();
                    if (serieAttribuee != null && !serieAttribuee.isEmpty()) {
                        // Si une série est attribuée, inclure seulement les réponses aux questions de cette série
                        return serieAttribuee.equals(question.getSerieExamenTag());
                    } else {
                        // Si aucune série n'est attribuée, inclure seulement les réponses aux questions sans tag de série
                        return question.getSerieExamenTag() == null || question.getSerieExamenTag().isEmpty();
                    }
                })
                .mapToDouble(reponse -> reponse.getScoreObtenu() != null ? reponse.getScoreObtenu() : 0.0)
                .sum();

        log.info("Score calculé pour l'étudiant ID {} pour l'examen ID {}: {}", studentId, examId, totalScore);
        return totalScore;
    }
}






