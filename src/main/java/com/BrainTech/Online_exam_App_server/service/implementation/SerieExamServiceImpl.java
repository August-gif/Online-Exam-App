package com.BrainTech.Online_exam_App_server.service.implementation;

import com.BrainTech.Online_exam_App_server.exceptions.ResourceNotFoundException;
import com.BrainTech.Online_exam_App_server.model.SerieExamen;
import com.BrainTech.Online_exam_App_server.repository.SerieExamenRepository;
import com.BrainTech.Online_exam_App_server.service.SerieExamService;

import java.util.List;
import java.util.Optional;

public class SerieExamServiceImpl implements SerieExamService {

    private final SerieExamenRepository serieExamenRepository;

    public SerieExamServiceImpl(SerieExamenRepository serieExamenRepository) {
        this.serieExamenRepository = serieExamenRepository;
    }

    @Override
    public List<SerieExamen> getAllSerieExamens() {
        return serieExamenRepository.findAll();
    }

    @Override
    public Optional<SerieExamen> getSerieExamenById(Long id) {
        return serieExamenRepository.findById(id);
    }

    @Override
    public SerieExamen createSerieExamen(SerieExamen serieExamen) {
        return serieExamenRepository.save(serieExamen);
    }

    @Override
    public SerieExamen updateSerieExamen(Long id, SerieExamen serieExamen) {
        return serieExamenRepository
                .findById(id)
                .map(
                        existingSerie -> {
                            serieExamen.setId(id);
                            return serieExamenRepository.save(serieExamen);
                        }
                ).orElseThrow(() -> new ResourceNotFoundException("Serie Examen not found with ID: " + id));
    }

    @Override
    public void deleteSerieExamen(Long id) {

    }

    @Override
    public Optional<SerieExamen> findByNom(String nom) {
        return serieExamenRepository.findByNom(nom);
    }
}
