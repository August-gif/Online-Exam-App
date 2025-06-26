package com.BrainTech.Online_exam_App_server.service.implementation;

import com.BrainTech.Online_exam_App_server.exceptions.InvalidOperationException;
import com.BrainTech.Online_exam_App_server.exceptions.ResourceNotFoundException;
import com.BrainTech.Online_exam_App_server.model.Exam;
import com.BrainTech.Online_exam_App_server.model.Question;
import com.BrainTech.Online_exam_App_server.model.StudentExamParticipation;
import com.BrainTech.Online_exam_App_server.repository.ExamRepository;
import com.BrainTech.Online_exam_App_server.repository.QuestionRepository;
import com.BrainTech.Online_exam_App_server.repository.StudentExamParticipationRepository;
import com.BrainTech.Online_exam_App_server.service.ExamService;
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
@RequiredArgsConstructor // Génère un constructeur avec les champs 'final' pour l'injection des dépendances
@Slf4j // Ajoute un logger pour la journalisation
public class ExamServiceImpl implements ExamService {
    private final ExamRepository examRepository;
    private final QuestionRepository questionRepository;
    private final StudentExamParticipationRepository studentExamParticipationRepository; // Injection du repository de participation

    /**
     * Crée un nouvel examen après des validations de base.
     * Le score maximum est initialisé à 0 et sera mis à jour à l'ajout des questions.
     *
     * @param exam L'objet Exam à créer.
     * @return L'examen créé et sauvegardé.
     * @throws InvalidOperationException si un examen avec le même titre existe déjà, ou si les dates/durée sont invalides.
     */
    @Override
    @Transactional
    public Exam createExam(Exam exam) {
        log.info("Tentative de création d'un examen avec le titre: {}", exam.getTitre());
        if (examRepository.findByTitre(exam.getTitre()).isPresent()) {
            throw new InvalidOperationException("Un examen avec ce titre existe déjà.");
        }
        if (exam.getDateDebut().isAfter(exam.getDateFin())) {
            throw new InvalidOperationException("La date de début ne peut pas être postérieure à la date de fin.");
        }
        if (exam.getDureeMinutes() == null || exam.getDureeMinutes() <= 0) {
            throw new InvalidOperationException("La durée de l'examen doit être un nombre positif.");
        }
        exam.setScoreMaximum(0.0); // Initialise le score max, sera calculé lors de l'ajout de questions.
        Exam savedExam = examRepository.save(exam);
        log.info("Examen créé avec succès avec l'ID: {}", savedExam.getId());
        return savedExam;
    }

    /**
     * Récupère un examen par son identifiant.
     *
     * @param id L'identifiant de l'examen.
     * @return Un Optional contenant l'examen s'il existe, vide sinon.
     */
    @Override
    @Transactional(readOnly = true) // Indique que cette méthode ne modifie pas la base de données
    public Optional<Exam> getExamById(Long id) {
        log.debug("Récupération de l'examen avec l'ID: {}", id);
        return examRepository.findById(id);
    }

    /**
     * Récupère la liste de tous les examens.
     *
     * @return Une liste de tous les examens.
     */

    @Override
    @Transactional(readOnly = true)
    public List<Exam> getAllExams() {
        log.debug("Récupération de tous les examens.");
        return examRepository.findAll();
    }

    /**
     * Met à jour les détails d'un examen existant.
     * Valide le titre, les dates et la durée.
     *
     * @param id L'identifiant de l'examen à mettre à jour.
     * @param examDetails L'objet Exam contenant les nouvelles informations.
     * @return L'examen mis à jour.
     * @throws ResourceNotFoundException si l'examen n'est pas trouvé.
     * @throws InvalidOperationException si les détails de l'examen sont invalides (titre existant, dates incohérentes, durée invalide).
     */

    @Override
    @Transactional
    public Exam updateExam(Long id, Exam examDetails) {
        log.info("Tentative de mise à jour de l'examen avec l'ID: {}", id);
        Exam existingExam = examRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Examen non trouvé avec l'ID: " + id));

        // Vérifier si le nouveau titre est déjà pris par un autre examen
        if (!existingExam.getTitre().equals(examDetails.getTitre())) {
            if (examRepository.findByTitre(examDetails.getTitre()).isPresent()) {
                throw new InvalidOperationException("Un autre examen avec ce titre existe déjà.");
            }
        }
        if (examDetails.getDateDebut().isAfter(examDetails.getDateFin())) {
            throw new InvalidOperationException("La date de début ne peut pas être postérieure à la date de fin.");
        }
        if (examDetails.getDureeMinutes() == null || examDetails.getDureeMinutes() <= 0) {
            throw new InvalidOperationException("La durée de l'examen doit être un nombre positif.");
        }

        existingExam.setTitre(examDetails.getTitre());
        existingExam.setDescription(examDetails.getDescription());
        existingExam.setDateDebut(examDetails.getDateDebut());
        existingExam.setDateFin(examDetails.getDateFin());
        existingExam.setDureeMinutes(examDetails.getDureeMinutes());
        existingExam.setExamenActif(examDetails.getExamenActif());

        Exam updatedExam = examRepository.save(existingExam);
        log.info("Examen avec l'ID {} mis à jour avec succès.", id);
        return updatedExam;
    }

    /**
     * Supprime un examen par son identifiant.
     *
     * @param id L'identifiant de l'examen à supprimer.
     * @throws ResourceNotFoundException si l'examen n'est pas trouvé.
     */
    @Override
    @Transactional
    public void deleteExam(Long id) {
        log.info("Tentative de suppression de l'examen avec l'ID: {}", id);
        if (!examRepository.existsById(id)) {
            throw new ResourceNotFoundException("Examen non trouvé avec l'ID: " + id);
        }
        examRepository.deleteById(id);
        log.info("Examen avec l'ID {} supprimé avec succès.", id);

    }

    /**
     * Ajoute une question existante à un examen.
     * Recalcule le score maximum de l'examen après l'ajout.
     *
     * @param examId L'identifiant de l'examen.
     * @param questionId L'identifiant de la question à ajouter.
     * @return L'examen mis à jour.
     * @throws ResourceNotFoundException si l'examen ou la question n'est pas trouvée.
     * @throws InvalidOperationException si la question est déjà associée à un autre examen ou à cet examen.
     */

    @Override
    @Transactional
    public Exam addQuestionToExam(Long examId, Long questionId) {
        log.info("Tentative d'ajout de la question ID {} à l'examen ID {}.", questionId, examId);
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new ResourceNotFoundException("Examen non trouvé avec l'ID: " + examId));
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Question non trouvée avec l'ID: " + questionId));

        if (question.getExamen() != null && !question.getExamen().getId().equals(examId)) {
            throw new InvalidOperationException("La question ID " + questionId + " est déjà associée à un autre examen (ID: " + question.getExamen().getId() + ").");
        }
        if (exam.getQuestions().contains(question)) {
            throw new InvalidOperationException("La question ID " + questionId + " est déjà associée à cet examen ID " + examId + ".");
        }

        exam.addQuestion(question); // Gère la relation bidirectionnelle
        exam.setScoreMaximum(calculateTotalScoreForExam(examId)); // Recalcule le score max de l'examen
        Exam updatedExam = examRepository.save(exam);
        log.info("Question ID {} ajoutée à l'examen ID {} avec succès.", questionId, examId);
        return updatedExam;
    }

    /**
     * Retire une question d'un examen.
     * Recalcule le score maximum de l'examen après le retrait.
     *
     * @param examId L'identifiant de l'examen.
     * @param questionId L'identifiant de la question à retirer.
     * @return L'examen mis à jour.
     * @throws ResourceNotFoundException si l'examen ou la question n'est pas trouvée.
     * @throws InvalidOperationException si la question n'est pas associée à cet examen.
     */

    @Override
    @Transactional
    public Exam removeQuestionFromExam(Long examId, Long questionId) {
        log.info("Tentative de suppression de la question ID {} de l'examen ID {}.", questionId, examId);
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new ResourceNotFoundException("Examen non trouvé avec l'ID: " + examId));
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Question non trouvée avec l'ID: " + questionId));

        if (!exam.getQuestions().contains(question)) {
            throw new InvalidOperationException("La question ID " + questionId + " n'est pas associée à cet examen ID " + examId + ".");
        }

        exam.removeQuestion(question); // Gère la relation bidirectionnelle
        exam.setScoreMaximum(calculateTotalScoreForExam(examId)); // Recalcule le score max de l'examen
        Exam updatedExam = examRepository.save(exam);
        log.info("Question ID {} retirée de l'examen ID {} avec succès.", questionId, examId);
        return updatedExam;
    }

    /**
     * Récupère l'ensemble des questions qu'un étudiant doit voir pour un examen donné.
     * La sélection des questions dépend de la "série" attribuée à l'étudiant pour cet examen.
     * Si aucune série n'est attribuée (car l'examen n'utilise pas de séries),
     * l'étudiant verra les questions qui n'ont pas de tag de série.
     *
     * @param examId L'identifiant de l'examen.
     * @param studentId L'identifiant de l'étudiant.
     * @return Un Set de Questions que l'étudiant doit passer.
     * @throws ResourceNotFoundException si la participation de l'étudiant à cet examen n'est pas trouvée.
     */

    @Override
    @Transactional(readOnly = true)
    public Set<Question> getExamQuestionsForStudent(Long examId, Long studentId) {
        log.debug("Récupération des questions pour l'examen ID {} pour l'étudiant ID {}.", examId, studentId);

        // Récupérer la participation de l'étudiant pour cet examen
        StudentExamParticipation participation = studentExamParticipationRepository
                .findByStudentIdAndExamId(studentId, examId)
                .orElseThrow(() -> new ResourceNotFoundException("Participation non trouvée pour l'étudiant ID " + studentId + " et l'examen ID " + examId + ". L'étudiant n'est peut-être pas inscrit ou n'a pas commencé l'examen."));

        Exam exam = participation.getExam(); // L'examen est déjà chargé via la participation
        String serieAttribuee = participation.getSerieAttribuee(); // Récupérer la série attribuée (peut être null)

        Set<Question> allQuestions = exam.getQuestions();

        if (serieAttribuee != null && !serieAttribuee.isEmpty()) {
            // Si une série est attribuée à l'étudiant, filtrer les questions pour ne montrer que celles de cette série.
            return allQuestions.stream()
                    .filter(q -> serieAttribuee.equals(q.getSerieExamenTag()))
                    .collect(Collectors.toSet());
        } else {
            // Si aucune série n'est attribuée à l'étudiant (par exemple, le professeur n'utilise pas de séries),
            // retourner toutes les questions de l'examen qui n'ont pas de tag de série (ou un tag vide).
            // Ceci est la "série par défaut" lorsque l'option de série n'est pas utilisée.
            return allQuestions.stream()
                    .filter(q -> q.getSerieExamenTag() == null || q.getSerieExamenTag().isEmpty())
                    .collect(Collectors.toSet());
        }
    }
    /**
     * Récupère tous les examens qui sont actuellement en cours.
     *
     * @return Une liste d'examens actifs.
     */
    @Override
    @Transactional(readOnly = true)
    public List<Exam> getActiveExams() {
        log.debug("Récupération des examens actifs.");
        LocalDateTime now = LocalDateTime.now();
        return examRepository.findByDateDebutBeforeAndDateFinAfter(now);
    }
    /**
     * Récupère tous les examens dont la date de début est future.
     *
     * @return Une liste d'examens à venir.
     */
    @Override
    @Transactional(readOnly = true)
    public List<Exam> getUpcomingExams() {
        log.debug("Récupération des examens à venir.");
        LocalDateTime now = LocalDateTime.now();
        return examRepository.findByDateDebutAfter(now);
    }

    /**
     * Récupère tous les examens dont la date de fin est passée.
     *
     * @return Une liste d'examens terminés.
     */

    @Override
    @Transactional(readOnly = true)
    public List<Exam> getPastExams() {
        log.debug("Récupération des examens terminés.");
        LocalDateTime now = LocalDateTime.now();
        return examRepository.findByDateFinBefore(now);
    }

    /**
     * Calcule le score total maximum possible pour un examen donné.
     * Ceci est la somme des points de toutes les questions associées à cet examen,
     * qu'elles aient des tags de série ou non.
     *
     * @param examId L'identifiant de l'examen.
     * @return Le score maximum total de l'examen.
     * @throws ResourceNotFoundException si l'examen n'est pas trouvé.
     */

    @Override
    @Transactional(readOnly = true)
    public Double calculateTotalScoreForExam(Long examId) {
        log.debug("Calcul du score total maximum pour l'examen ID: {}", examId);
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new ResourceNotFoundException("Examen non trouvé avec l'ID: " + examId));

        // Le score maximum de l'examen est la somme de tous les points de toutes ses questions,
        // indépendamment des séries.
        return exam.getQuestions().stream()
                .mapToDouble(Question::getPoints)
                .sum();
    }


    // Implémentez d'autres méthodes spécifiques ici
}
