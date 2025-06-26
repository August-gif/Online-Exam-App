package com.BrainTech.Online_exam_App_server.service.implementation;

import com.BrainTech.Online_exam_App_server.exceptions.InvalidOperationException;
import com.BrainTech.Online_exam_App_server.exceptions.ResourceNotFoundException;
import com.BrainTech.Online_exam_App_server.model.Student; // Utilise "Student"
import com.BrainTech.Online_exam_App_server.model.Option;
import com.BrainTech.Online_exam_App_server.model.Question;
import com.BrainTech.Online_exam_App_server.model.QuestionChoixMultiple;
import com.BrainTech.Online_exam_App_server.model.QuestionMixte; // Pour le cas de la question mixte
import com.BrainTech.Online_exam_App_server.model.ReponseEtudiant;
import com.BrainTech.Online_exam_App_server.repository.StudentRepository; // Utilise "StudentRepository"
import com.BrainTech.Online_exam_App_server.repository.OptionRepository;
import com.BrainTech.Online_exam_App_server.repository.QuestionRepository;
import com.BrainTech.Online_exam_App_server.repository.ReponseEtudiantRepository;
import com.BrainTech.Online_exam_App_server.service.ReponseEtudiantService;
import com.BrainTech.Online_exam_App_server.enums.QuestionType;
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
public class ReponseEtudiantServiceImpl implements ReponseEtudiantService {

    private final ReponseEtudiantRepository reponseEtudiantRepository;
    private final StudentRepository studentRepository; // Injection de StudentRepository
    private final QuestionRepository questionRepository;
    private final OptionRepository optionRepository;

    /**
     * Enregistre la réponse d'un étudiant à une question.
     * Valide l'étudiant et la question, et s'assure que le type de réponse correspond au type de question.
     * @param reponseEtudiant L'objet ReponseEtudiant à sauvegarder.
     * @return La ReponseEtudiant persistante.
     * @throws ResourceNotFoundException si l'étudiant ou la question n'est pas trouvée.
     * @throws InvalidOperationException si la réponse ne correspond pas au type de question.
     */
    @Override
    @Transactional
    public ReponseEtudiant saveReponse(ReponseEtudiant reponseEtudiant) {
        log.info("Tentative d'enregistrement de la réponse pour l'étudiant ID {} et la question ID {}.",
                reponseEtudiant.getStudent().getId(), reponseEtudiant.getQuestion().getId());

        // 1. Valider l'existence de l'étudiant
        Student student = studentRepository.findById(reponseEtudiant.getStudent().getId())
                .orElseThrow(() -> {
                    log.error("Étudiant avec l'ID {} non trouvé.", reponseEtudiant.getStudent().getId());
                    return new ResourceNotFoundException("Étudiant non trouvé avec l'ID: " + reponseEtudiant.getStudent().getId());
                });
        reponseEtudiant.setStudent(student);

        // 2. Valider l'existence de la question et son type
        Question question = questionRepository.findById(reponseEtudiant.getQuestion().getId())
                .orElseThrow(() -> {
                    log.error("Question avec l'ID {} non trouvée.", reponseEtudiant.getQuestion().getId());
                    return new ResourceNotFoundException("Question non trouvée avec l'ID: " + reponseEtudiant.getQuestion().getId());
                });
        reponseEtudiant.setQuestion(question);

        // 3. Valider et adapter la réponse en fonction du type de question
        validateAndAdaptReponse(reponseEtudiant, question);

        reponseEtudiant.setDateReponse(LocalDateTime.now());
        reponseEtudiant.setScoreObtenu(null); // Le score est calculé ultérieurement via corrigerReponse()

        ReponseEtudiant savedReponse = reponseEtudiantRepository.save(reponseEtudiant);
        log.info("Réponse enregistrée avec succès avec l'ID: {}", savedReponse.getId());
        return savedReponse;
    }

    /**
     * Récupère une réponse d'étudiant par son ID.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<ReponseEtudiant> getReponseById(Long id) {
        log.debug("Récupération de la réponse avec l'ID: {}", id);
        return reponseEtudiantRepository.findById(id);
    }

    /**
     * Récupère toutes les réponses pour un étudiant donné.
     */
    @Override
    @Transactional(readOnly = true)
    public List<ReponseEtudiant> getReponsesByStudentId(Long studentId) {
        log.debug("Récupération des réponses pour l'étudiant ID: {}", studentId);
        if (!studentRepository.existsById(studentId)) {
            throw new ResourceNotFoundException("Étudiant non trouvé avec l'ID: " + studentId);
        }
        return reponseEtudiantRepository.findByStudentId(studentId);
    }

    /**
     * Récupère toutes les réponses pour une question donnée.
     */
    @Override
    @Transactional(readOnly = true)
    public List<ReponseEtudiant> getReponsesByQuestionId(Long questionId) {
        log.debug("Récupération des réponses pour la question ID: {}", questionId);
        if (!questionRepository.existsById(questionId)) {
            throw new ResourceNotFoundException("Question non trouvée avec l'ID: " + questionId);
        }
        return reponseEtudiantRepository.findByQuestionId(questionId);
    }

    /**
     * Récupère toutes les réponses d'un étudiant pour un examen donné.
     */
    @Override
    @Transactional(readOnly = true)
    public List<ReponseEtudiant> getReponsesByStudentAndExamen(Long studentId, Long examenId) {
        log.debug("Récupération des réponses pour l'étudiant ID {} et l'examen ID {}.", studentId, examenId);
        // La méthode du repository findByStudentIdAndQuestion_ExamenId gère la jointure.
        return reponseEtudiantRepository.findByStudentIdAndQuestion_ExamenId(studentId, examenId);
    }

    /**
     * Met à jour une réponse existante.
     * Les champs Student et Question ne sont pas modifiables après la création.
     * @param id L'ID de la réponse à mettre à jour.
     * @param updatedReponse L'objet ReponseEtudiant avec les informations mises à jour.
     * @return La ReponseEtudiant mise à jour.
     * @throws ResourceNotFoundException si la réponse n'est pas trouvée.
     * @throws InvalidOperationException si le type de réponse est incompatible avec le type de question originale.
     */
    @Override
    @Transactional
    public ReponseEtudiant updateReponse(Long id, ReponseEtudiant updatedReponse) {
        log.info("Tentative de mise à jour de la réponse avec l'ID: {}", id);

        ReponseEtudiant existingReponse = reponseEtudiantRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Réponse avec l'ID {} non trouvée pour la mise à jour.", id);
                    return new ResourceNotFoundException("Réponse non trouvée avec l'ID: " + id);
                });

        // Les champs student et question ne doivent pas être modifiables via update,
        // car une réponse est liée à une question et un étudiant spécifiques.
        // Si ces champs sont modifiés dans updatedReponse, ils seront ignorés ici.

        // Récupérer la question associée pour valider la mise à jour
        Question question = existingReponse.getQuestion();

        // Valider et adapter la nouvelle réponse en fonction du type de question
        validateAndAdaptReponse(updatedReponse, question);

        // Mettre à jour les champs de réponse spécifiques
        existingReponse.setReponseTextuelle(updatedReponse.getReponseTextuelle());

        // Pour les ManyToMany (optionsSelectionnees), on gère le remplacement complet
        existingReponse.getOptionsSelectionnees().clear();
        if (updatedReponse.getOptionsSelectionnees() != null && !updatedReponse.getOptionsSelectionnees().isEmpty()) {
            // Re-attacher les options existantes au contexte de persistance
            Set<Option> managedOptions = updatedReponse.getOptionsSelectionnees().stream()
                    .map(option -> optionRepository.findById(option.getId())
                            .orElseThrow(() -> new ResourceNotFoundException("Option sélectionnée avec l'ID " + option.getId() + " non trouvée.")))
                    .collect(Collectors.toSet());
            existingReponse.getOptionsSelectionnees().addAll(managedOptions);
        }

        existingReponse.setDateReponse(LocalDateTime.now()); // Date de dernière modification
        existingReponse.setScoreObtenu(null); // Le score est recalculé via corrigerReponse()

        ReponseEtudiant savedReponse = reponseEtudiantRepository.save(existingReponse);
        log.info("Réponse avec l'ID {} mise à jour avec succès.", id);
        return savedReponse;
    }

    /**
     * Supprime une réponse par son ID.
     */
    @Override
    @Transactional
    public void deleteReponse(Long id) {
        log.info("Tentative de suppression de la réponse avec l'ID: {}", id);
        if (!reponseEtudiantRepository.existsById(id)) {
            log.warn("Réponse avec l'ID {} non trouvée pour la suppression.", id);
            throw new ResourceNotFoundException("Réponse non trouvée avec l'ID: " + id);
        }
        reponseEtudiantRepository.deleteById(id);
        log.info("Réponse avec l'ID {} supprimée avec succès.", id);
    }

    /**
     * Calcule et enregistre le score pour une réponse spécifique.
     * Cette logique est adaptée aux différents types de questions.
     * @param reponseId L'ID de la réponse à corriger.
     * @return La ReponseEtudiant avec le score mis à jour.
     * @throws ResourceNotFoundException si la réponse n'est pas trouvée.
     * @throws InvalidOperationException si la logique de correction n'est pas implémentée pour ce type de question.
     */
    @Override
    @Transactional
    public ReponseEtudiant corrigerReponse(Long reponseId) {
        log.info("Tentative de correction de la réponse avec l'ID: {}", reponseId);
        ReponseEtudiant reponse = reponseEtudiantRepository.findById(reponseId)
                .orElseThrow(() -> new ResourceNotFoundException("Réponse non trouvée avec l'ID: " + reponseId));

        Question question = reponse.getQuestion();
        Double score = 0.0; // Initialisation du score

        if (question.getType() == QuestionType.CHOIX_MULTIPLE) {
            if (!(question instanceof QuestionChoixMultiple)) {
                log.error("Erreur de type: Question ID {} est de type {} mais n'est pas une instance de QuestionChoixMultiple.", question.getId(), question.getType());
                throw new InvalidOperationException("Incohérence de type de question pour la correction.");
            }
            QuestionChoixMultiple qcm = (QuestionChoixMultiple) question;

            // Récupère les options *correctes* pour cette question
            Set<Option> bonnesReponses = qcm.getOptions().stream()
                    .filter(Option::isCorrect)
                    .collect(Collectors.toSet());
            // Récupère les options *sélectionnées par l'étudiant*
            Set<Option> reponsesEtudiant = reponse.getOptionsSelectionnees();

            // Logique de correction QCM : toutes les bonnes réponses sont sélectionnées, et aucune mauvaise
            if (reponsesEtudiant.equals(bonnesReponses) && reponsesEtudiant.size() == bonnesReponses.size()) {
                score = question.getPoints(); // Accorde tous les points
            } else {
                score = 0.0; // Ne donne aucun point si la sélection n'est pas parfaitement correcte
                // Pour une logique plus avancée (points partiels, pénalités):
                // long correctSelected = reponsesEtudiant.stream().filter(Option::isCorrect).count();
                // long incorrectSelected = reponsesEtudiant.stream().filter(o -> !o.isCorrect()).count();
                // score = (double) correctSelected / bonnesReponses.size() * question.getPoints();
                // ou avec pénalités:
                // score = (double) (correctSelected - incorrectSelected) / (bonnesReponses.size() + incorrectSelected).
                // Il faudrait définir une pénalité par mauvaise réponse
            }
            log.debug("Correction QCM (Question ID {}): Bonnes réponses attendues: {}, Réponses étudiant: {}, Score calculé: {}", question.getId(), bonnesReponses.size(), reponsesEtudiant.size(), score);

        } else if (question.getType() == QuestionType.CONVENTIONNELLE) {
            // Les questions conventionnelles nécessitent une correction manuelle par un enseignant.
            // On ne peut pas les corriger automatiquement sans une IA complexe de NLP.
            log.warn("Correction manuelle requise pour la question conventionnelle ID {}. Score par défaut: 0.0", question.getId());
            score = 0.0; // Le score initialisé, le professeur devra le définir manuellement.

        } else if (question.getType() == QuestionType.MIXTE) {
            if (!(question instanceof QuestionMixte)) {
                log.error("Erreur de type: Question ID {} est de type {} mais n'est pas une instance de QuestionMixte.", question.getId(), question.getType());
                throw new InvalidOperationException("Incohérence de type de question pour la correction.");
            }
            QuestionMixte qMixte = (QuestionMixte) question;

            // --- Correction de la partie QCM de la question mixte ---
            Set<Option> bonnesReponsesQCM = qMixte.getOptionsPartieChoixMultiple().stream()
                    .filter(Option::isCorrect)
                    .collect(Collectors.toSet());
            Set<Option> reponsesEtudiantQCM = reponse.getOptionsSelectionnees();

            if (reponsesEtudiantQCM.equals(bonnesReponsesQCM) && reponsesEtudiantQCM.size() == bonnesReponsesQCM.size()) {
                // Si la partie QCM est correcte, attribue une fraction des points totaux (ex: 50%)
                score = question.getPoints() * 0.5; // Exemple: 50% des points pour la partie QCM
            } else {
                score = 0.0; // Pas de points pour la partie QCM si incorrecte
            }
            log.debug("Correction MIXTE (Question ID {}): Score QCM calculé: {}", question.getId(), score);

            // --- Partie textuelle de la question mixte ---
            // La partie textuelle nécessite une correction manuelle, comme une conventionnelle.
            log.warn("Correction manuelle requise pour la partie textuelle de la question MIXTE ID {}. Score actuel: {}", question.getId(), score);
            // Le score final sera mis à jour manuellement par un enseignant.

        } else {
            log.error("Type de question non supporté pour la correction automatique: {}", question.getType());
            throw new InvalidOperationException("Correction automatique non implémentée pour ce type de question: " + question.getType());
        }

        reponse.setScoreObtenu(score);
        ReponseEtudiant updatedReponse = reponseEtudiantRepository.save(reponse);
        log.info("Score de la réponse ID {} mis à jour à {}.", reponseId, score);
        return updatedReponse;
    }

    @Override
    @Transactional
    public ReponseEtudiant noterManuellementReponse(Long reponseId, Double score) {
        log.info("Tentative de notation manuelle pour la réponse ID {} avec le score {}.", reponseId, score);
        ReponseEtudiant reponse = reponseEtudiantRepository.findById(reponseId)
                .orElseThrow(() -> {
                    log.error("Réponse avec l'ID {} non trouvée pour la notation manuelle.", reponseId);
                    return new ResourceNotFoundException("Réponse non trouvée avec l'ID: " + reponseId);
                });
        Question question = reponse.getQuestion();

        // Vérifier que la question est bien de type CONVENTIONNELLE ou MIXTE
        if (question.getType() != QuestionType.CONVENTIONNELLE && question.getType() != QuestionType.MIXTE) {
            log.error("La question ID {} de type {} ne peut pas être notée manuellement de cette façon.", question.getId(), question.getType());
            throw new InvalidOperationException("Seules les questions conventionnelles ou mixtes peuvent être notées manuellement.");
        }
        // Valider le score : doit être positif et ne pas dépasser les points max de la question
        if (score < 0 || score > question.getPoints()) {
            log.error("Score manuel {} invalide pour la question ID {} (points max: {}).", score, question.getId(), question.getPoints());
            throw new InvalidOperationException("Le score manuel doit être compris entre 0 et " + question.getPoints() + " points.");
        }

        reponse.setScoreObtenu(score); // Met à jour le score avec la note du professeur
        ReponseEtudiant updatedReponse = reponseEtudiantRepository.save(reponse);
        log.info("Score de la réponse ID {} mis à jour manuellement à {}.", reponseId, score);
        return updatedReponse;

    }


    /**
     * Méthode utilitaire pour valider et adapter les champs de réponse.
     * @param reponseEtudiant L'objet ReponseEtudiant en cours de traitement.
     * @param question La question associée.
     * @throws InvalidOperationException si la réponse est invalide pour le type de question.
     * @throws ResourceNotFoundException si une option sélectionnée n'est pas trouvée.
     */
    private void validateAndAdaptReponse(ReponseEtudiant reponseEtudiant, Question question) {
        if (question.getType() == QuestionType.CHOIX_MULTIPLE) {
            if (reponseEtudiant.getReponseTextuelle() != null && !reponseEtudiant.getReponseTextuelle().isEmpty()) {
                throw new InvalidOperationException("Une question à choix multiples ne doit pas avoir de réponse textuelle.");
            }
            if (reponseEtudiant.getOptionsSelectionnees() == null || reponseEtudiant.getOptionsSelectionnees().isEmpty()) {
                // Dépend si tu veux forcer au moins une sélection
                throw new InvalidOperationException("Une question à choix multiples nécessite la sélection d'au moins une option.");
            }
            // Valider que les options sélectionnées existent et appartiennent à la question QCM
            if (!(question instanceof QuestionChoixMultiple)) {
                throw new InvalidOperationException("Erreur de type: La question est déclarée CHOIX_MULTIPLE mais n'est pas une instance de QuestionChoixMultiple.");
            }
            QuestionChoixMultiple qcmQuestion = (QuestionChoixMultiple) question;
            Set<Long> validOptionIds = qcmQuestion.getOptions().stream()
                    .map(Option::getId)
                    .collect(Collectors.toSet());

            for (Option selectedOption : reponseEtudiant.getOptionsSelectionnees()) {
                if (selectedOption.getId() == null || !validOptionIds.contains(selectedOption.getId())) {
                    throw new InvalidOperationException("Option sélectionnée avec l'ID " + selectedOption.getId() + " est invalide ou n'appartient pas à cette question.");
                }
                // Ré-attache l'option managée pour s'assurer qu'elle est dans le contexte de persistance
                // Nécessaire si les options viennent d'un DTO sans être managées
                optionRepository.findById(selectedOption.getId())
                        .orElseThrow(() -> new ResourceNotFoundException("Option avec l'ID " + selectedOption.getId() + " non trouvée."));
            }

        } else if (question.getType() == QuestionType.CONVENTIONNELLE) {
            if (reponseEtudiant.getOptionsSelectionnees() != null && !reponseEtudiant.getOptionsSelectionnees().isEmpty()) {
                throw new InvalidOperationException("Une question conventionnelle ne doit pas avoir d'options sélectionnées.");
            }
            if (reponseEtudiant.getReponseTextuelle() == null || reponseEtudiant.getReponseTextuelle().trim().isEmpty()) {
                // Tu pourrais vouloir exiger une réponse non vide ici
                log.warn("Réponse textuelle vide pour la question conventionnelle ID: {}", question.getId());
                // throw new InvalidOperationException("Une question conventionnelle nécessite une réponse textuelle non vide.");
            }
        } else if (question.getType() == QuestionType.MIXTE) {
            if (!(question instanceof QuestionMixte)) {
                throw new InvalidOperationException("Erreur de type: La question est déclarée MIXTE mais n'est pas une instance de QuestionMixte.");
            }
            QuestionMixte mixteQuestion = (QuestionMixte) question;

            // Pour les questions mixtes, les deux types de réponse sont possibles
            if ((reponseEtudiant.getReponseTextuelle() == null || reponseEtudiant.getReponseTextuelle().trim().isEmpty()) &&
                    (reponseEtudiant.getOptionsSelectionnees() == null || reponseEtudiant.getOptionsSelectionnees().isEmpty())) {
                throw new InvalidOperationException("Une question mixte nécessite une réponse textuelle et/ou des options sélectionnées.");
            }

            if (reponseEtudiant.getOptionsSelectionnees() != null && !reponseEtudiant.getOptionsSelectionnees().isEmpty()) {
                // Valider les options pour la partie QCM de la question mixte
                Set<Long> validMixteOptionIds = mixteQuestion.getOptionsPartieChoixMultiple().stream()
                        .map(Option::getId)
                        .collect(Collectors.toSet());
                for (Option selectedOption : reponseEtudiant.getOptionsSelectionnees()) {
                    if (selectedOption.getId() == null || !validMixteOptionIds.contains(selectedOption.getId())) {
                        throw new InvalidOperationException("Option sélectionnée avec l'ID " + selectedOption.getId() + " est invalide ou n'appartient pas à la partie QCM de cette question mixte.");
                    }
                    optionRepository.findById(selectedOption.getId())
                            .orElseThrow(() -> new ResourceNotFoundException("Option avec l'ID " + selectedOption.getId() + " non trouvée."));
                }
            }
            // La partie textuelle est optionnelle mais encouragée.
            if (reponseEtudiant.getReponseTextuelle() == null || reponseEtudiant.getReponseTextuelle().trim().isEmpty()) {
                log.warn("Partie textuelle vide pour la question MIXTE ID: {}", question.getId());
            }

        } else {
            throw new InvalidOperationException("Type de question inconnu ou non supporté pour la validation de réponse: " + question.getType());
        }
    }
}