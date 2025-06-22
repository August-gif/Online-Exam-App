package com.BrainTech.Online_exam_App_server.repository;

import com.BrainTech.Online_exam_App_server.model.Option;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OptionRepository extends JpaRepository<Option,Long> {
    //On pourra avoir besoin de méthodes pour trouver les options associées à une question.
    // Exemple :
    List<Option> findByQuestionChoixMultipleId(Long questionId);
    List<Option> findByQuestionMixteId(Long questionMixteId);
}
