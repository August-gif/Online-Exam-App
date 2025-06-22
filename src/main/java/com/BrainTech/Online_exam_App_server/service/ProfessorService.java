package com.BrainTech.Online_exam_App_server.service;

import com.BrainTech.Online_exam_App_server.model.Professsor;

import java.util.List;
import java.util.Optional;

public interface ProfessorService {
    List<Professsor> getAllProfessors();
    Optional<Professsor> getProfessorById(Long id);
    Professsor createProfessor(Professsor professsor);
    Professsor updateProfessor(Long id, Professsor professsor);
    void deleteProfessor(Long id);
    Optional<Professsor> findByNom(String nom);
    //Optional<Professsor> findByEmail(String email);
    // Ajoutez d'autres méthodes spécifiques à la logique métier des professeurs

}
