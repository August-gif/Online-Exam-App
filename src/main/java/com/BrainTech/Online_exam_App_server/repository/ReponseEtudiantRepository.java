package com.BrainTech.Online_exam_App_server.repository;

import com.BrainTech.Online_exam_App_server.model.ReponseEtudiant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository // Indique que c'est un composant de persistance
public interface ReponseEtudiantRepository extends JpaRepository<ReponseEtudiant,Long> {
    // Récupère toutes les réponses soumises par un étudiant spécifique
    List<ReponseEtudiant> findByStudentId(Long studentId);

    // Récupère toutes les réponses soumises pour une question spécifique
    List<ReponseEtudiant> findByQuestionId(Long questionId);

    // Récupère toutes les réponses d'un étudiant pour toutes les questions d'un examen donné
    List<ReponseEtudiant> findByStudentIdAndQuestion_ExamenId(Long studentId, Long examenId);

    // Tu pourrais ajouter d'autres méthodes de recherche si nécessaire
    // Par exemple, trouver une réponse spécifique d'un étudiant à une question donnée :
    // Optional<ReponseEtudiant> findByStudentIdAndQuestionId(Long studentId, Long questionId);
}
