package com.BrainTech.Online_exam_App_server.repository;

import com.BrainTech.Online_exam_App_server.model.QuestionConventionnelle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionConventionnelleRepository extends JpaRepository<QuestionConventionnelle,Long> {
    // Vous pourriez ajouter des méthodes spécifiques à ce type de question si nécessaire.
}
