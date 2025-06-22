package com.BrainTech.Online_exam_App_server.repository;

import com.BrainTech.Online_exam_App_server.model.ReponseQuestionEtudiant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface ReponseQuestionEtudiantRepository extends JpaRepository<ReponseQuestionEtudiant,Long> {
    // Pour trouver les réponses aux questions pour une soumission d'étudiant spécifique.
    List<ReponseQuestionEtudiant> findByReponseEtudiantId(Long reponseEtudiantId);

    // Pour trouver la réponse d'un étudiant à une question spécifique dans une soumission.
    Optional<ReponseQuestionEtudiant> findByReponseEtudiantIdAndQuestionId(Long reponseEtudiantId, Long questionId);

}
