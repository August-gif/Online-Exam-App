package com.BrainTech.Online_exam_App_server.service;



import com.BrainTech.Online_exam_App_server.model.StudentExamParticipation;

import java.util.List;
import java.util.Optional;

public interface StudentExamparticipationService {
    /**
     * Inscrit un étudiant à un examen. Cela crée un enregistrement de participation
     * sans démarrer la session ni attribuer de série pour l'instant.
     *
     * @param studentId L'ID de l'étudiant.
     * @param examId L'ID de l'examen.
     * @return La participation créée.
     * @throws ResourceNotFoundException si l'étudiant ou l'examen n'est pas trouvé.
     * @throws InvalidOperationException si l'étudiant est déjà inscrit à cet examen.
     */
    StudentExamParticipation enrollStudentInExam(Long studentId, Long examId);

    /**
     * Démarre la session d'examen pour un étudiant. Cela enregistre l'heure de début
     * et attribue une série de questions à l'étudiant pour cet examen si l'examen utilise des séries.
     *
     * @param studentId L'ID de l'étudiant.
     * @param examId L'ID de l'examen.
     * @return La participation mise à jour.
     * @throws ResourceNotFoundException si la participation n'est pas trouvée.
     * @throws InvalidOperationException si la session a déjà commencé ou si l'examen n'est pas actif.
     */
    StudentExamParticipation startExamSession(Long studentId, Long examId);

    /**
     * Soumet l'examen pour un étudiant. Cela enregistre l'heure de fin, marque la participation comme terminée,
     * et calcule le score final de l'étudiant pour cet examen.
     *
     * @param studentId L'ID de l'étudiant.
     * @param examId L'ID de l'examen.
     * @return La participation mise à jour avec le score final.
     * @throws ResourceNotFoundException si la participation n'est pas trouvée.
     * @throws InvalidOperationException si l'examen a déjà été soumis.
     */
    StudentExamParticipation submitExam(Long studentId, Long examId);

    /**
     * Récupère une participation par son ID unique.
     * @param id L'ID de la participation.
     * @return Un Optional contenant la participation si trouvée, vide sinon.
     */
    Optional<StudentExamParticipation> getParticipationById(Long id);

    /**
     * Récupère une participation par l'ID de l'étudiant et l'ID de l'examen.
     * @param studentId L'ID de l'étudiant.
     * @param examId L'ID de l'examen.
     * @return Un Optional contenant la participation si trouvée, vide sinon.
     */
    Optional<StudentExamParticipation> getParticipationByStudentAndExam(Long studentId, Long examId);

    /**
     * Récupère toutes les participations enregistrées.
     * @return Une liste de toutes les participations.
     */
    List<StudentExamParticipation> getAllParticipations();

    /**
     * Récupère toutes les participations d'un étudiant donné à différents examens.
     * @param studentId L'ID de l'étudiant.
     * @return Une liste de participations de l'étudiant.
     * @throws ResourceNotFoundException si l'étudiant n'est pas trouvé.
     */
    List<StudentExamParticipation> getParticipationsByStudent(Long studentId);

    /**
     * Récupère toutes les participations à un examen donné par différents étudiants.
     * @param examId L'ID de l'examen.
     * @return Une liste de participations à l'examen.
     * @throws ResourceNotFoundException si l'examen n'est pas trouvé.
     */
    List<StudentExamParticipation> getParticipationsByExam(Long examId);

    /**
     * Calcule le score final obtenu par un étudiant pour un examen spécifique,
     * en tenant compte de la série de questions qui lui a été attribuée (si applicable).
     *
     * @param studentId L'ID de l'étudiant.
     * @param examId L'ID de l'examen.
     * @return Le score total obtenu par l'étudiant pour cet examen.
     * @throws ResourceNotFoundException si la participation n'est pas trouvée.
     */
    Double calculateStudentExamScore(Long studentId, Long examId);

}
