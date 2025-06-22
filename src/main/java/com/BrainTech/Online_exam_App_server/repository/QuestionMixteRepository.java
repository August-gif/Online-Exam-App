package com.BrainTech.Online_exam_App_server.repository;

import com.BrainTech.Online_exam_App_server.model.QuestionMixte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface QuestionMixteRepository extends JpaRepository<QuestionMixte,Long> {
    // Vous pourriez ajouter des méthodes spécifiques à ce type de question si nécessaire.
}
