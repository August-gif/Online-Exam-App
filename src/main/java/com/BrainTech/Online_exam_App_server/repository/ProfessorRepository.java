package com.BrainTech.Online_exam_App_server.repository;


import com.BrainTech.Online_exam_App_server.model.Professsor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface ProfessorRepository extends JpaRepository<Professsor, Long> {
    // Souvent utilisé pour la recherche par email lors de l'authentification.
    Optional<Professsor> findByEmail(String email);

    Optional<Professsor> findByNom(String nom);
}
