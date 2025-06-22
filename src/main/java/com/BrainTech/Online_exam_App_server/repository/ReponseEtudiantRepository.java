package com.BrainTech.Online_exam_App_server.repository;

import com.BrainTech.Online_exam_App_server.model.ReponseEtudiant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReponseEtudiantRepository extends JpaRepository<ReponseEtudiant,Long> {
    // Pour trouver les réponses d'un étudiant à un examen spécifique.
    Optional<List<ReponseEtudiant> >findByEtudiantIdAndExamenId(Long etudiantId, Long examenId);

    // Pour trouver toutes les réponses soumises pour un examen.
    List<ReponseEtudiant> findByExamenId(Long examenId);

    // Pour trouver toutes les réponses soumises par un étudiant.
    List<ReponseEtudiant> findByEtudiantId(Long etudiantId);
}
