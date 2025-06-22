package com.BrainTech.Online_exam_App_server.service;

import com.BrainTech.Online_exam_App_server.model.ReponseEtudiant;
import com.BrainTech.Online_exam_App_server.model.ReponseQuestionEtudiant;

import java.util.List;
import java.util.Optional;

public interface ReponseEtudiantService {
    /**
     * Soumet la réponse complète d'un étudiant pour un examen.
     * Cette méthode valide si l'examen est ouvert, empêche les soumissions multiples
     * et calcule un score initial basé sur les réponses automatiques.
     *
     * @param studentId L'ID de l'étudiant.
     * @param examId L'ID de l'examen concerné.
     * @param reponsesAuxQuestions La liste des réponses individuelles à chaque question de l'examen.
     * @return L'objet ReponseEtudiant persistant après la soumission.
     */
    ReponseEtudiant soumettreReponseExamen(Long studentId, Long examId, List<ReponseQuestionEtudiant> reponsesAuxQuestions);

    /**
     * Récupère une soumission d'examen spécifique par son ID.
     *
     * @param id L'ID de la ReponseEtudiant.
     * @return Un Optional contenant la ReponseEtudiant si trouvée.
     */
    Optional<ReponseEtudiant> getReponseEtudiantById(Long id);

    /**
     * Récupère toutes les soumissions d'examens existantes dans le système.
     *
     * @return Une liste de toutes les ReponseEtudiant.
     */
    List<ReponseEtudiant> getAllReponsesEtudiant();

    /**
     * Récupère toutes les soumissions d'examens effectuées par un étudiant spécifique.
     *
     * @param studentId L'ID de l'étudiant.
     * @return Une liste des ReponseEtudiant de cet étudiant.
     */
    List<ReponseEtudiant> getReponsesByStudentId(Long studentId);

    /**
     * Récupère toutes les soumissions d'examens pour un examen donné.
     *
     * @param examId L'ID de l'examen.
     * @return Une liste des ReponseEtudiant pour cet examen.
     */
    List<ReponseEtudiant> getReponsesByExamId(Long examId);

    /**
     * Récupère la soumission d'un étudiant pour un examen spécifique.
     * Utile pour vérifier si un étudiant a déjà soumis un examen particulier.
     *
     * @param studentId L'ID de l'étudiant.
     * @param examId    L'ID de l'examen.
     * @return Un Optional contenant la ReponseEtudiant si trouvée.
     */
    Optional<List<ReponseEtudiant>> getReponseByStudentAndExam(Long studentId, Long examId);

    /**
     * Met à jour une soumission d'examen existante.
     * Cela peut inclure la mise à jour du score total après correction manuelle.
     *
     * @param id L'ID de la ReponseEtudiant à mettre à jour.
     * @param updatedReponseEtudiant L'objet ReponseEtudiant avec les données modifiées.
     * @return La ReponseEtudiant mise à jour.
     */
    ReponseEtudiant updateReponseEtudiant(Long id, ReponseEtudiant updatedReponseEtudiant);

    // Note : La suppression physique de ReponseEtudiant n'est pas recommandée
    // pour des raisons d'audit et d'intégrité des données, comme discuté précédemment.
    // Si une "suppression" est nécessaire, il est préférable d'implémenter un "soft delete"
    // ou un changement de statut (par exemple, "invalide", "archivée").
}
