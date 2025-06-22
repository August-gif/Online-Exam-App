package com.BrainTech.Online_exam_App_server.service.implementation;

import com.BrainTech.Online_exam_App_server.exceptions.ResourceNotFoundException;
import com.BrainTech.Online_exam_App_server.model.Professsor;
import com.BrainTech.Online_exam_App_server.repository.ProfessorRepository;
import com.BrainTech.Online_exam_App_server.service.ProfessorService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
public class ProfessorServiceImpl implements ProfessorService {

    private final ProfessorRepository professorRepository;

    public ProfessorServiceImpl(ProfessorRepository professorRepository) {
        this.professorRepository = professorRepository;
    }
    @Override
    public List<Professsor> getAllProfessors() {
        return professorRepository.findAll();
    }

    @Override
    public Optional<Professsor> getProfessorById(Long id) {
        return professorRepository.findById(id);
    }

    @Override
    public Professsor createProfessor(Professsor professor) {
        return professorRepository.save(professor);
    }

    @Override
    public Professsor updateProfessor(Long id, Professsor professor) {
        return professorRepository.findById(id)
                .map(existingProfessor -> {
                    professor.setId(id);
                    return professorRepository.save(professor);
                })
                .orElseThrow(() -> new ResourceNotFoundException("Professor not found with ID: " + id)); // Ou lancez une exception
    }

    @Override
    public void deleteProfessor(Long id) {
        professorRepository.deleteById(id);
    }

    @Override
    public Optional<Professsor> findByNom(String nom) {
        return professorRepository.findByNom(nom);
    }

    // Implémentez d'autres méthodes spécifiques ici

}
