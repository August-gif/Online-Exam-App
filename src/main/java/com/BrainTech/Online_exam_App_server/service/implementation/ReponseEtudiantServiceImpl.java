package com.BrainTech.Online_exam_App_server.service.impl;

import com.BrainTech.Online_exam_App_server.exceptions.ResourceNotFoundException;
import com.BrainTech.Online_exam_App_server.exceptions.InvalidOperationException;
import com.BrainTech.Online_exam_App_server.model.Exam;
import com.BrainTech.Online_exam_App_server.model.ReponseEtudiant;
import com.BrainTech.Online_exam_App_server.model.ReponseQuestionEtudiant;
import com.BrainTech.Online_exam_App_server.model.Student;
import com.BrainTech.Online_exam_App_server.model.Question;
import com.BrainTech.Online_exam_App_server.model.QuestionChoixMultiple;
import com.BrainTech.Online_exam_App_server.model.QuestionMixte;
import com.BrainTech.Online_exam_App_server.model.Option;
import com.BrainTech.Online_exam_App_server.repository.ExamRepository;
import com.BrainTech.Online_exam_App_server.repository.QuestionRepository;
import com.BrainTech.Online_exam_App_server.repository.ReponseEtudiantRepository;
import com.BrainTech.Online_exam_App_server.repository.ReponseQuestionEtudiantRepository;
import com.BrainTech.Online_exam_App_server.repository.StudentRepository;
import com.BrainTech.Online_exam_App_server.service.ReponseEtudiantService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors; // Pour Java 8+

@Service
@RequiredArgsConstructor
@Slf4j
public class ReponseEtudiantServiceImpl implements ReponseEtudiantService {

    private final ReponseEtudiantRepository reponseEtudiantRepository;
    private final StudentRepository studentRepository;
    private final ExamRepository examRepository;
    private final QuestionRepository questionRepository;
    private final ReponseQuestionEtudiantRepository reponseQuestionEtudiantRepository;

    /**
     * Soumet la réponse complète d'un étudiant pour un examen.
     * Cette méthode valide si l'examen est ouvert, empêche les soumissions multiples
     * et calcule un score initial basé sur les réponses automatiques.
     *
     * @param studentId L'ID de l'étudiant.
     * @param examId L'ID de l'examen concerné.
     * @param reponsesAuxQuestions La liste des réponses individuelles à chaque question de l'examen.
     * @return L'objet ReponseEtudiant persistant après la soumission.
     * @throws ResourceNotFoundException si l'étudiant ou l'examen n'est pas trouvé.
     * @throws InvalidOperationException si la soumission est hors délai ou déjà effectuée.
     */
    @Override
    @Transactional // Assure que toutes les opérations de cette méthode sont atomiques
    public ReponseEtudiant soumettreReponseExamen(Long studentId, Long examId, List<ReponseQuestionEtudiant> reponsesAuxQuestions) {
        log.info("Tentative de soumission de la réponse d'examen pour l'étudiant ID: {} et l'examen ID: {}", studentId, examId);

        // 1. Vérifier l'existence de l'étudiant et de l'examen
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> {
                    log.error("Étudiant avec l'ID {} non trouvé.", studentId);
                    return new ResourceNotFoundException("Étudiant non trouvé avec l'ID: " + studentId);
                });
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> {
                    log.error("Examen avec l'ID {} non trouvé.", examId);
                    return new ResourceNotFoundException("Examen non trouvé avec l'ID: " + examId);
                });

        // 2. Vérifier si l'examen est encore ouvert
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(exam.getDateOuverture()) || now.isAfter(exam.getDateFermeture())) {
            log.warn("Tentative de soumission de réponse pour l'examen ID {} en dehors des délais autorisés. Date d'ouverture: {}, Date de fermeture: {}",
                    examId, exam.getDateOuverture(), exam.getDateFermeture());
            throw new InvalidOperationException("L'examen n'est pas actuellement ouvert pour les soumissions.");
        }

        // 3. Vérifier si l'étudiant a déjà soumis une réponse pour cet examen
        if (reponseEtudiantRepository.findByEtudiantIdAndExamenId(studentId, examId).isPresent()) {
            log.warn("L'étudiant {} a déjà soumis une réponse pour l'examen {}. Les soumissions multiples ne sont pas autorisées.", studentId, examId);
            throw new InvalidOperationException("L'étudiant a déjà soumis une réponse pour cet examen.");
        }

        // 4. Créer l'entité ReponseEtudiant principale
        ReponseEtudiant reponseEtudiant = new ReponseEtudiant();
        reponseEtudiant.setEtudiant(student);
        reponseEtudiant.setExamen(exam);
        reponseEtudiant.setDateSoumission(now);
        reponseEtudiant.setScoreTotal(0.0); // Le score sera calculé ci-dessous

        // Enregistrer d'abord la ReponseEtudiant pour obtenir un ID si nécessaire pour les enfants
        reponseEtudiant = reponseEtudiantRepository.save(reponseEtudiant);
        log.debug("ReponseEtudiant créée avec l'ID: {}", reponseEtudiant.getId());

        double totalScoreCalculated = 0.0;
        // 5. Traiter chaque réponse de question individuelle
        for (ReponseQuestionEtudiant reponseQuestion : reponsesAuxQuestions) {
            if (reponseQuestion.getQuestion() == null || reponseQuestion.getQuestion().getId() == null) {
                log.error("Réponse de question invalide: l'ID de la question est manquant.");
                throw new InvalidOperationException("Chaque réponse de question doit être liée à une question existante.");
            }
            Question question = questionRepository.findById(reponseQuestion.getQuestion().getId())
                    .orElseThrow(() -> {
                        log.error("Question avec l'ID {} non trouvée pour la soumission de réponse.", reponseQuestion.getQuestion().getId());
                        return new ResourceNotFoundException("Question non trouvée avec l'ID: " + reponseQuestion.getQuestion().getId());
                    });

            // Assurer que la question appartient bien à l'examen
            if (!question.getExamen().getId().equals(exam.getId())) {
                log.warn("La question {} n'appartient pas à l'examen {}. Tentative de soumission invalide.", question.getId(), examId);
                throw new InvalidOperationException("La question " + question.getId() + " n'appartient pas à l'examen fourni.");
            }

            reponseQuestion.setQuestion(question);
            reponseQuestion.setReponseEtudiant(reponseEtudiant);

            // Calculer le score pour chaque question
            double scoreQuestion = calculerScoreQuestion(reponseQuestion);
            reponseQuestion.setScore(scoreQuestion);
            totalScoreCalculated += scoreQuestion;

            reponseQuestionEtudiantRepository.save(reponseQuestion); // Enregistrer chaque réponse de question
            log.debug("ReponseQuestionEtudiant sauvegardée pour la question {}. Score: {}", question.getId(), scoreQuestion);
        }

        // 6. Mettre à jour le score total de la ReponseEtudiant
        reponseEtudiant.setScoreTotal(totalScoreCalculated);
        reponseEtudiant = reponseEtudiantRepository.save(reponseEtudiant); // Sauvegarder la ReponseEtudiant mise à jour

        log.info("Réponse d'examen soumise avec succès pour l'étudiant ID: {} et l'examen ID: {}. Score total: {}", studentId, examId, totalScoreCalculated);
        return reponseEtudiant;
    }

    /**
     * Méthode privée pour calculer le score d'une question.
     *
     * @param reponseQuestion La ReponseQuestionEtudiant à évaluer.
     * @return Le score calculé pour cette question.
     */
    private double calculerScoreQuestion(ReponseQuestionEtudiant reponseQuestion) {
        Question question = reponseQuestion.getQuestion();
        double pointsQuestion = (question.getPoints() != null) ? question.getPoints() : 0.0;

        switch (question.getType()) {
            case "CHOIX_MULTIPLE":
                if (question instanceof QuestionChoixMultiple) {
                    QuestionChoixMultiple qcm = (QuestionChoixMultiple) question;
                    // Options correctes définies par le professeur
                    List<Long> correctOptionIds = qcm.getOptions().stream()
                            .filter(Option::isEstCorrecte)
                            .map(Option::getId)
                            .collect(Collectors.toList());

                    // Options choisies par l'étudiant
                    List<Long> studentChosenOptionIds = reponseQuestion.getOptionChoisies().stream()
                            .map(Option::getId)
                            .collect(Collectors.toList());

                    // Vérifier si l'étudiant a coché EXACTEMENT les bonnes réponses et RIEN d'autre
                    if (correctOptionIds.size() == studentChosenOptionIds.size() &&
                            studentChosenOptionIds.containsAll(correctOptionIds)) {
                        return pointsQuestion;
                    }
                }
                return 0.0; // Si incorrect ou type non géré, ou si une seule mauvaise option cochée
            case "CONVENTIONNELLE":
                // Pour les questions conventionnelles, le scoring est manuel.
                // Le score sera 0.0 au moment de la soumission. Il devra être mis à jour par un professeur.
                return 0.0;
            case "MIXTE":
                if (question instanceof QuestionMixte) {
                    QuestionMixte qm = (QuestionMixte) question;
                    double scorePartiel = 0.0;

                    // Scoring de la partie QCM de la question mixte
                    List<Long> correctOptionIdsMixte = qm.getOptionsPartieChoixMultiple().stream()
                            .filter(Option::isEstCorrecte)
                            .map(Option::getId)
                            .collect(Collectors.toList());

                    List<Long> studentChosenOptionIdsMixte = reponseQuestion.getOptionMixteChoisie().stream()
                            .map(Option::getId)
                            .collect(Collectors.toList());

                    if (correctOptionIdsMixte.size() == studentChosenOptionIdsMixte.size() &&
                            studentChosenOptionIdsMixte.containsAll(correctOptionIdsMixte)) {
                        // Exemple: attribuer une partie des points pour la partie QCM
                        scorePartiel += pointsQuestion * 0.5; // Par exemple, 50% des points pour la partie QCM correcte
                    }

                    // La partie conventionnelle est notée manuellement, donc 0 pour l'auto-notation
                    return scorePartiel;
                }
                return 0.0;
            default:
                log.warn("Type de question non géré pour le scoring automatique: {}", question.getType());
                return 0.0;
        }
    }

    /**
     * Récupère une soumission d'examen spécifique par son ID.
     *
     * @param id L'ID de la ReponseEtudiant.
     * @return Un Optional contenant la ReponseEtudiant si trouvée.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<ReponseEtudiant> getReponseEtudiantById(Long id) {
        log.debug("Récupération de ReponseEtudiant avec l'ID: {}", id);
        return reponseEtudiantRepository.findById(id);
    }

    /**
     * Récupère toutes les soumissions d'examens existantes dans le système.
     *
     * @return Une liste de toutes les ReponseEtudiant.
     */
    @Override
    @Transactional(readOnly = true)
    public List<ReponseEtudiant> getAllReponsesEtudiant() {
        log.debug("Récupération de toutes les ReponsesEtudiant.");
        return reponseEtudiantRepository.findAll();
    }

    /**
     * Récupère toutes les soumissions d'examens effectuées par un étudiant spécifique.
     *
     * @param studentId L'ID de l'étudiant.
     * @return Une liste des ReponseEtudiant de cet étudiant.
     */
    @Override
    @Transactional(readOnly = true)
    public List<ReponseEtudiant> getReponsesByStudentId(Long studentId) {
        log.debug("Récupération des ReponsesEtudiant pour l'étudiant ID: {}", studentId);
        // Vérifier si l'étudiant existe avant de chercher ses réponses
        if (!studentRepository.existsById(studentId)) {
            log.warn("Tentative de récupérer les réponses pour un étudiant inexistant avec l'ID: {}", studentId);
            throw new ResourceNotFoundException("Étudiant non trouvé avec l'ID: " + studentId);
        }
        return reponseEtudiantRepository.findByEtudiantId(studentId);
    }

    /**
     * Récupère toutes les soumissions d'examens pour un examen donné.
     *
     * @param examId L'ID de l'examen.
     * @return Une liste des ReponseEtudiant pour cet examen.
     */
    @Override
    @Transactional(readOnly = true)
    public List<ReponseEtudiant> getReponsesByExamId(Long examId) {
        log.debug("Récupération des ReponsesEtudiant pour l'examen ID: {}", examId);
        // Vérifier si l'examen existe avant de chercher ses réponses
        if (!examRepository.existsById(examId)) {
            log.warn("Tentative de récupérer les réponses pour un examen inexistant avec l'ID: {}", examId);
            throw new ResourceNotFoundException("Examen non trouvé avec l'ID: " + examId);
        }
        return reponseEtudiantRepository.findByExamenId(examId);
    }

    /**
     * Récupère la soumission d'un étudiant pour un examen spécifique.
     * Utile pour vérifier si un étudiant a déjà soumis un examen particulier.
     *
     * @param studentId L'ID de l'étudiant.
     * @param examId    L'ID de l'examen.
     * @return Un Optional contenant la ReponseEtudiant si trouvée.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<List<ReponseEtudiant>> getReponseByStudentAndExam(Long studentId, Long examId) {
        log.debug("Récupération de ReponseEtudiant pour l'étudiant ID: {} et l'examen ID: {}", studentId, examId);
        // Vérifier l'existence de l'étudiant et de l'examen n'est pas strictement nécessaire pour un findBy multiple
        // mais peut ajouter de la robustesse si la logique métier l'exige.
        return reponseEtudiantRepository.findByEtudiantIdAndExamenId(studentId, examId);
    }

    /**
     * Met à jour une soumission d'examen existante.
     * Cela peut inclure la mise à jour du score total après correction manuelle.
     *
     * @param id L'ID de la ReponseEtudiant à mettre à jour.
     * @param updatedReponseEtudiant L'objet ReponseEtudiant avec les données modifiées.
     * @return La ReponseEtudiant mise à jour.
     * @throws ResourceNotFoundException si la ReponseEtudiant n'est pas trouvée.
     */
    @Override
    @Transactional
    public ReponseEtudiant updateReponseEtudiant(Long id, ReponseEtudiant updatedReponseEtudiant) {
        log.info("Mise à jour de ReponseEtudiant avec l'ID: {}", id);
        ReponseEtudiant existingReponse = reponseEtudiantRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("ReponseEtudiant avec l'ID {} non trouvée pour la mise à jour.", id);
                    return new ResourceNotFoundException("ReponseEtudiant non trouvée avec l'ID: " + id);
                });

        // Mise à jour des champs pertinents. Les relations (étudiant, examen) ne devraient pas changer ici.
        // Seuls les champs que l'on veut permettre de modifier sont copiés.
        // Par exemple, le score total pourrait être mis à jour manuellement après la correction.
        if (updatedReponseEtudiant.getScoreTotal() != null) {
            existingReponse.setScoreTotal(updatedReponseEtudiant.getScoreTotal());
        }
        // La date de soumission ne devrait normalement pas être modifiable.
        // Si d'autres champs doivent être modifiables, ajoutez-les ici.

        ReponseEtudiant savedReponse = reponseEtudiantRepository.save(existingReponse);
        log.info("ReponseEtudiant avec l'ID {} mise à jour avec succès.", id);
        return savedReponse;
    }

    // Comme discuté précédemment, la méthode de suppression est commentée
    // pour favoriser la rétention des données pour l'audit et la traçabilité.
    // Si une "suppression" logique est nécessaire, on implémenterait un "soft delete"
    // via un champ de statut ou un indicateur d'activité.
    // @Override
    // @Transactional
    // public void deleteReponseEtudiant(Long id) {
    //     log.info("Suppression de ReponseEtudiant avec l'ID: {}", id);
    //     if (!reponseEtudiantRepository.existsById(id)) {
    //         log.warn("ReponseEtudiant avec l'ID {} non trouvée pour la suppression.", id);
    //         throw new ResourceNotFoundException("ReponseEtudiant non trouvée avec l'ID: " + id);
    //     }
    //     reponseEtudiantRepository.deleteById(id);
    //     log.info("ReponseEtudiant avec l'ID {} supprimée avec succès.", id);
    // }
}