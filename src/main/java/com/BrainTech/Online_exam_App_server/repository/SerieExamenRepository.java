package com.BrainTech.Online_exam_App_server.repository;

import com.BrainTech.Online_exam_App_server.model.SerieExamen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SerieExamenRepository extends JpaRepository<SerieExamen,Long> {
    Optional<SerieExamen> findByNom(String nom);
    //Méthodes de requete spécifiques si necessaire
}
