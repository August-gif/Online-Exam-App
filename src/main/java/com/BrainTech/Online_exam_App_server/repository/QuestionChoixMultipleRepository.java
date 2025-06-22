package com.BrainTech.Online_exam_App_server.repository;

import com.BrainTech.Online_exam_App_server.model.QuestionChoixMultiple;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionChoixMultipleRepository extends JpaRepository<QuestionChoixMultiple,Long> {
    // Vous pourriez ajouter des méthodes spécifiques à ce type de question si nécessaire.
}
