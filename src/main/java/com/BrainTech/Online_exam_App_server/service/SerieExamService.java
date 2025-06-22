package com.BrainTech.Online_exam_App_server.service;

import com.BrainTech.Online_exam_App_server.model.SerieExamen;

import java.util.List;
import java.util.Optional;

public interface SerieExamService {
    List<SerieExamen> getAllSerieExamens();

    Optional<SerieExamen> getSerieExamenById(Long id);

    SerieExamen createSerieExamen(SerieExamen serieExamen);
    SerieExamen updateSerieExamen(Long id, SerieExamen serieExamen);
    void deleteSerieExamen(Long id);
    Optional<SerieExamen> findByNom(String nom);
}
