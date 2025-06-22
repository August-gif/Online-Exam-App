package com.BrainTech.Online_exam_App_server.repository;

import com.BrainTech.Online_exam_App_server.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    // Pour récupérer toutes les questions d'un examen spécifique.
    List<Question> findByExamenId(Long examenId);

    // Pour récupérer les questions d'un examen triées par ordre (si vous avez un champ d'ordre).
    List<Question> findByExamenIdOrderByOrdreAsc(Long examenId);

    // Exemple d'une requête JPQL personnalisée pour récupérer les questions d'un certain type pour un examen.
    @Query("SELECT q FROM Question q WHERE q.examen.id = :examenId AND TYPE(q) = :type")
    List<Question> findByExamenIdAndType(@Param("examenId") Long examenId, @Param("type") Class<? extends Question> type);

}
