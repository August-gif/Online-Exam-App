package com.BrainTech.Online_exam_App_server.repository;

import com.BrainTech.Online_exam_App_server.model.Exam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ExamRepository extends JpaRepository<Exam, Long> {
    // On peut ajouter des méthodes de requête personnalisées ici si nécessaire,
    // par exemple, pour rechercher des examens par titre, par professeur créateur,
    // par date d'ouverture, etc.

    // Exemple :
    List<Exam> findByTitreContainingIgnoreCase(String titre);
    List<Exam> findByProfesseurCreateurId(Long professeurId);
    List<Exam> findByDateOuvertureBefore(LocalDateTime date);
}
