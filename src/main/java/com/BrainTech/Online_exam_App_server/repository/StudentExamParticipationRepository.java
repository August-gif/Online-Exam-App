package com.BrainTech.Online_exam_App_server.repository;


import com.BrainTech.Online_exam_App_server.model.StudentExamParticipation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentExamParticipationRepository extends JpaRepository<StudentExamParticipation,Long> {

    //Méthodes de requete spécifiques si necessaire

    /**
     * Trouve une participation spécifique par l'ID de l'étudiant et l'ID de l'examen.
     * C'est utile pour vérifier si un étudiant est déjà inscrit à un examen ou pour récupérer sa participation active.
     *
     * @param studentId L'ID de l'étudiant.
     * @param examId L'ID de l'examen.
     * @return Un Optional contenant la participation si trouvée, vide sinon.
     */
    Optional<StudentExamParticipation> findByStudentIdAndExamId(Long studentId, Long examId);

    /**
     * Trouve toutes les participations liées à un examen spécifique.
     *
     * @param examId L'ID de l'examen.
     * @return Une liste de participations à cet examen.
     */
    List<StudentExamParticipation> findByExamId(Long examId);

    /**
     * Trouve toutes les participations liées à un étudiant spécifique.
     *
     * @param studentId L'ID de l'étudiant.
     * @return Une liste de participations de cet étudiant.
     */
    List<StudentExamParticipation> findByStudentId(Long studentId);
}
