package com.BrainTech.Online_exam_App_server.service.implementation;

import com.BrainTech.Online_exam_App_server.model.ReponseQuestionEtudiant;
import com.BrainTech.Online_exam_App_server.service.ReponseQuestionEtudiantService;

import java.util.List;
import java.util.Optional;

public class ReponseQuestionEtudiantServiceImpl implements ReponseQuestionEtudiantService {
    @Override
    public ReponseQuestionEtudiant createReponseQuestionEtudiant(ReponseQuestionEtudiant reponseQuestionEtudiant) {
        return null;
    }

    @Override
    public Optional<ReponseQuestionEtudiant> getReponseQuestionEtudiantById(Long id) {
        return Optional.empty();
    }

    @Override
    public List<ReponseQuestionEtudiant> getAllReponseQuestionEtudiants() {
        return null;
    }

    @Override
    public List<ReponseQuestionEtudiant> getReponsesByReponseEtudiantId(Long reponseEtudiantId) {
        return null;
    }

    @Override
    public List<ReponseQuestionEtudiant> getReponsesByQuestionId(Long questionId) {
        return null;
    }

    @Override
    public ReponseQuestionEtudiant updateReponseQuestionEtudiant(Long id, ReponseQuestionEtudiant updatedReponseQuestionEtudiant) {
        return null;
    }

    @Override
    public void deleteReponseQuestionEtudiant(Long id) {

    }
}
