package com.BrainTech.Online_exam_App_server.repository;

import com.BrainTech.Online_exam_App_server.model.Exam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ExamRepository extends JpaRepository<Exam, Long> {
    /**
     * Trouve un examen par son titre.
     * Utile pour la vérification d'unicité lors de la création ou la récupération.
     * @param titre Le titre de l'examen.
     * @return Un Optional contenant l'examen s'il est trouvé, vide sinon.
     */
    Optional<Exam> findByTitre(String titre);

    /**
     * Trouve tous les examens qui sont actuellement actifs.
     * Un examen est considéré actif si la date de début est passée et la date de fin n'est pas encore atteinte.
     * @param now La date et l'heure actuelles.
     * @return Une liste d'examens actifs.
     */
    List<Exam> findByDateDebutBeforeAndDateFinAfter(LocalDateTime now);

    /**
     * Trouve tous les examens dont la date de début est ultérieure à la date actuelle.
     * @param now La date et l'heure actuelles.
     * @return Une liste d'examens à venir.
     */
    List<Exam> findByDateDebutAfter(LocalDateTime now);

    /**
     * Trouve tous les examens dont la date de fin est antérieure à la date actuelle.
     * @param now La date et l'heure actuelles.
     * @return Une liste d'examens terminés.
     */
    List<Exam> findByDateFinBefore(LocalDateTime now);

    // Note : La méthode findByParticipants_Id() que nous avions précédemment n'est plus nécessaire ici
    // car la relation étudiant-examen est maintenant gérée par StudentExamParticipation.
    // Pour trouver les examens d'un étudiant, il faut passer par StudentExamParticipationRepository.
}
