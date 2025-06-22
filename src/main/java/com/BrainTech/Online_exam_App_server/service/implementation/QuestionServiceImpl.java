package com.BrainTech.Online_exam_App_server.service.impl;

import com.BrainTech.Online_exam_App_server.exceptions.ResourceNotFoundException;
import com.BrainTech.Online_exam_App_server.exceptions.InvalidOperationException;
import com.BrainTech.Online_exam_App_server.model.Exam;
import com.BrainTech.Online_exam_App_server.model.Option;
import com.BrainTech.Online_exam_App_server.model.Question;
import com.BrainTech.Online_exam_App_server.model.QuestionChoixMultiple;
import com.BrainTech.Online_exam_App_server.model.QuestionConventionnelle;
import com.BrainTech.Online_exam_App_server.model.QuestionMixte;
import com.BrainTech.Online_exam_App_server.enums.QuestionType;
import com.BrainTech.Online_exam_App_server.repository.ExamRepository;
import com.BrainTech.Online_exam_App_server.repository.OptionRepository; // Si tu as un repository pour Option
import com.BrainTech.Online_exam_App_server.repository.QuestionRepository;
import com.BrainTech.Online_exam_App_server.service.FileStorageService;
import com.BrainTech.Online_exam_App_server.service.QuestionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class QuestionServiceImpl implements QuestionService {

    private final QuestionRepository questionRepository;
    private final ExamRepository examRepository;
    private final OptionRepository optionRepository; // Pour gérer les options si elles sont persistées séparément
    private final FileStorageService fileStorageService; // Injection du service de stockage de fichiers

    /**
     * Crée une nouvelle question. La question peut être de type CHOIX_MULTIPLE, CONVENTIONNELLE ou MIXTE.
     * Gère la persistance des options pour les questions QCM et Mixtes.
     *
     * @param question L'objet Question à créer.
     * @return La Question persistante.
     * @throws ResourceNotFoundException si l'examen lié n'est pas trouvé.
     * @throws InvalidOperationException si le type de question est inconnu ou la question est mal formée.
     */
    @Override
    @Transactional
    public Question createQuestion(Question question) {
        log.info("Tentative de création d'une question de type: {}", question.getType());

        // 1. Valider l'existence de l'examen associé
        if (question.getExamen() == null || question.getExamen().getId() == null) {
            log.error("Tentative de créer une question sans examen associé.");
            throw new InvalidOperationException("Une question doit être associée à un examen.");
        }
        Exam exam = examRepository.findById(question.getExamen().getId())
                .orElseThrow(() -> {
                    log.error("Examen avec l'ID {} non trouvé pour la création de question.", question.getExamen().getId());
                    return new ResourceNotFoundException("Examen non trouvé avec l'ID: " + question.getExamen().getId());
                });
        question.setExamen(exam); // Assure que l'entité Exam est gérée par le contexte de persistance

        // 2. Gérer l'URL de l'annexe (elle est supposée être déjà uploadée par un autre endpoint)
        if (question.getAttachmentUrl() != null && !question.getAttachmentUrl().isEmpty()) {
            log.debug("Question à créer avec annexe URL: {}", question.getAttachmentUrl());
            // Tu peux ajouter ici une validation pour t'assurer que l'URL fournie est valide ou que le fichier existe
        }

        // 3. Gérer les spécificités par type de question
        if (question.getType() == QuestionType.CHOIX_MULTIPLE) {
            if (!(question instanceof QuestionChoixMultiple)) {
                log.error("Type d'objet incompatible pour CHOIX_MULTIPLE. Attendu: QuestionChoixMultiple.");
                throw new InvalidOperationException("L'objet fourni n'est pas une instance de QuestionChoixMultiple pour le type CHOIX_MULTIPLE.");
            }
            QuestionChoixMultiple qcm = (QuestionChoixMultiple) question;
            // Assurer que les options sont correctement liées à la question parente
            if (qcm.getOptions() != null) {
                qcm.getOptions().forEach(option -> option.setQuestion(qcm));
            }
            log.debug("Question CHOIX_MULTIPLE avec {} options à créer.", qcm.getOptions() != null ? qcm.getOptions().size() : 0);

        } else if (question.getType() == QuestionType.MIXTE) {
            if (!(question instanceof QuestionMixte)) {
                log.error("Type d'objet incompatible pour MIXTE. Attendu: QuestionMixte.");
                throw new InvalidOperationException("L'objet fourni n'est pas une instance de QuestionMixte pour le type MIXTE.");
            }
            QuestionMixte qMixte = (QuestionMixte) question;
            // Pour ManyToMany, la relation est gérée par la table de jointure.
            // Les méthodes add/remove dans l'entité QuestionMixte gèrent la collection.
            log.debug("Question MIXTE avec {} options de QCM à créer.", qMixte.getOptionsPartieChoixMultiple() != null ? qMixte.getOptionsPartieChoixMultiple().size() : 0);

        } else if (question.getType() == QuestionType.CONVENTIONNELLE) {
            if (!(question instanceof QuestionConventionnelle)) {
                log.error("Type d'objet incompatible pour CONVENTIONNELLE. Attendu: QuestionConventionnelle.");
                throw new InvalidOperationException("L'objet fourni n'est pas une instance de QuestionConventionnelle pour le type CONVENTIONNELLE.");
            }
            log.debug("Question CONVENTIONNELLE à créer.");

        } else {
            log.error("Type de question inconnu: {}", question.getType());
            throw new InvalidOperationException("Type de question inconnu: " + question.getType());
        }

        Question savedQuestion = questionRepository.save(question);
        log.info("Question créée avec succès avec l'ID: {}", savedQuestion.getId());
        return savedQuestion;
    }

    /**
     * Récupère une question par son ID.
     *
     * @param id L'ID de la question.
     * @return Un Optional contenant la Question si trouvée, vide sinon.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Question> getQuestionById(Long id) {
        log.debug("Récupération de la question avec l'ID: {}", id);
        return questionRepository.findById(id);
    }

    /**
     * Récupère toutes les questions existantes.
     *
     * @return Une liste de toutes les Questions.
     */
    @Override
    @Transactional(readOnly = true)
    public List<Question> getAllQuestions() {
        log.debug("Récupération de toutes les questions.");
        return questionRepository.findAll();
    }

    /**
     * Récupère toutes les questions pour un examen spécifique.
     *
     * @param examId L'ID de l'examen.
     * @return Une liste de Questions appartenant à cet examen.
     * @throws ResourceNotFoundException si l'examen n'est pas trouvé.
     */
    @Override
    @Transactional(readOnly = true)
    public List<Question> getQuestionsByExamId(Long examId) {
        log.debug("Récupération des questions pour l'examen ID: {}", examId);
        if (!examRepository.existsById(examId)) {
            log.warn("Tentative de récupérer des questions pour un examen inexistant avec l'ID: {}", examId);
            throw new ResourceNotFoundException("Examen non trouvé avec l'ID: " + examId);
        }
        return questionRepository.findByExamenId(examId);
    }

    /**
     * Met à jour une question existante.
     * La mise à jour gère les spécificités des options pour les questions QCM et Mixtes.
     *
     * @param id L'ID de la question à mettre à jour.
     * @param updatedQuestion L'objet Question avec les informations mises à jour.
     * @return La Question mise à jour.
     * @throws ResourceNotFoundException si la question n'est pas trouvée.
     * @throws InvalidOperationException si le type de la question mise à jour ne correspond pas à l'originale,
     * ou si la structure des options est invalide.
     */
    @Override
    @Transactional
    public Question updateQuestion(Long id, Question updatedQuestion) {
        log.info("Tentative de mise à jour de la question avec l'ID: {}", id);

        Question existingQuestion = questionRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Question avec l'ID {} non trouvée pour la mise à jour.", id);
                    return new ResourceNotFoundException("Question non trouvée avec l'ID: " + id);
                });

        // 1. Vérifier que le type de question ne change pas pendant la mise à jour
        if (!existingQuestion.getType().equals(updatedQuestion.getType())) {
            log.error("Tentative de modifier le type de question de {} à {} pour l'ID: {}. Cette opération n'est pas supportée.",
                    existingQuestion.getType(), updatedQuestion.getType(), id);
            throw new InvalidOperationException("Le type d'une question ne peut pas être modifié après sa création.");
        }

        // 2. Mettre à jour les champs communs
        existingQuestion.setIntitule(updatedQuestion.getIntitule());
        existingQuestion.setPoints(updatedQuestion.getPoints());
        // L'examen ne devrait pas être modifiable après création, ou doit être validé

        // 3. Gérer l'annexe (logique d'update/suppression du fichier physique)
        String oldAttachmentUrl = existingQuestion.getAttachmentUrl();
        String newAttachmentUrl = updatedQuestion.getAttachmentUrl();

        // Si une nouvelle annexe est fournie ou l'annexe existante est remplacée
        if (newAttachmentUrl != null && !newAttachmentUrl.isEmpty() && !newAttachmentUrl.equals(oldAttachmentUrl)) {
            if (oldAttachmentUrl != null && !oldAttachmentUrl.isEmpty()) {
                // Supprimer l'ancienne annexe si elle existe et est différente de la nouvelle
                try {
                    fileStorageService.deleteFile(oldAttachmentUrl);
                    log.info("Ancienne annexe {} supprimée pour la question ID {}.", oldAttachmentUrl, id);
                } catch (IOException e) {
                    log.warn("Échec de la suppression de l'ancienne annexe {} pour la question ID {}: {}", oldAttachmentUrl, id, e.getMessage());
                    // Loguer l'erreur, mais ne pas bloquer la mise à jour de la question
                }
            }
            existingQuestion.setAttachmentUrl(newAttachmentUrl); // Définir la nouvelle URL
        }
        // Si l'annexe existante est supprimée (newAttachmentUrl est null ou vide, mais oldAttachmentUrl existe)
        else if ((newAttachmentUrl == null || newAttachmentUrl.isEmpty()) && (oldAttachmentUrl != null && !oldAttachmentUrl.isEmpty())) {
            try {
                fileStorageService.deleteFile(oldAttachmentUrl);
                log.info("Annexe {} supprimée de la question ID {}.", oldAttachmentUrl, id);
            } catch (IOException e) {
                log.warn("Échec de la suppression de l'annexe {} pour la question ID {}: {}", oldAttachmentUrl, id, e.getMessage());
            }
            existingQuestion.setAttachmentUrl(null); // Supprimer la référence dans l'entité
        }
        // Si l'annexe n'a pas changé ou n'a jamais existé, aucune action sur le fichier physique.

        // 4. Gérer les spécificités par type de question
        if (existingQuestion instanceof QuestionChoixMultiple) {
            QuestionChoixMultiple existingQCM = (QuestionChoixMultiple) existingQuestion;
            if (!(updatedQuestion instanceof QuestionChoixMultiple)) {
                log.error("Mise à jour d'un QCM avec un objet de type incorrect pour l'ID: {}", id);
                throw new InvalidOperationException("L'objet de mise à jour doit être de type QuestionChoixMultiple.");
            }
            QuestionChoixMultiple updatedQCM = (QuestionChoixMultiple) updatedQuestion;

            // Mise à jour de la liste d'options : remplacement complet
            existingQCM.getOptions().clear();
            if (updatedQCM.getOptions() != null) {
                updatedQCM.getOptions().forEach(option -> existingQCM.addOption(option));
            }
            existingQCM.setEstChoixMultiplePermis(updatedQCM.isEstChoixMultiplePermis());
            log.debug("Question CHOIX_MULTIPLE {} mise à jour. Nouvelles options: {}", id, existingQCM.getOptions().size());

        } else if (existingQuestion instanceof QuestionMixte) {
            QuestionMixte existingQM = (QuestionMixte) existingQuestion;
            if (!(updatedQuestion instanceof QuestionMixte)) {
                log.error("Mise à jour d'une question mixte avec un objet de type incorrect pour l'ID: {}", id);
                throw new InvalidOperationException("L'objet de mise à jour doit être de type QuestionMixte.");
            }
            QuestionMixte updatedQM = (QuestionMixte) updatedQuestion;

            // Mise à jour des options pour la partie QCM de la question mixte
            existingQM.getOptionsPartieChoixMultiple().clear();
            if (updatedQM.getOptionsPartieChoixMultiple() != null) {
                updatedQM.getOptionsPartieChoixMultiple().forEach(option -> existingQM.addOptionPartieChoixMultiple(option));
            }
            log.debug("Question MIXTE {} mise à jour. Nouvelles options QCM: {}", id, existingQM.getOptionsPartieChoixMultiple().size());

        } else if (existingQuestion instanceof QuestionConventionnelle) {
            log.debug("Question CONVENTIONNELLE {} mise à jour (champs communs uniquement).", id);
        }

        Question savedQuestion = questionRepository.save(existingQuestion);
        log.info("Question avec l'ID {} mise à jour avec succès.", id);
        return savedQuestion;
    }


    /**
     * Supprime une question par son ID.
     * La suppression en cascade gérera les options associées pour les QCM et les questions mixtes.
     *
     * @param id L'ID de la question à supprimer.
     * @throws ResourceNotFoundException si la question n'est pas trouvée.
     */
    @Override
    @Transactional
    public void deleteQuestion(Long id) {
        log.info("Tentative de suppression de la question avec l'ID: {}", id);
        Question questionToDelete = questionRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Question avec l'ID {} non trouvée pour la suppression.", id);
                    return new ResourceNotFoundException("Question non trouvée avec l'ID: " + id);
                });
        // Si la question a une annexe associée, la supprimer du stockage physique
        if (questionToDelete.getAttachmentUrl() != null && !questionToDelete.getAttachmentUrl().isEmpty()) {
            try {
                fileStorageService.deleteFile(questionToDelete.getAttachmentUrl());
                log.info("Annexe {} supprimée du stockage physique pour la question ID {}.", questionToDelete.getAttachmentUrl(), id);
            } catch (IOException e) {
                log.error("Échec de la suppression de l'annexe {} pour la question ID {}: {}", questionToDelete.getAttachmentUrl(), id, e.getMessage());
                // Important : Loguer l'erreur, mais ne pas bloquer la suppression de la question en base
                // pour éviter les références orphelines dans la DB si le fichier est déjà manquant.
            }
        }
        questionRepository.deleteById(id);
        log.info("Question avec l'ID {} supprimée avec succès.", id);
    }
}