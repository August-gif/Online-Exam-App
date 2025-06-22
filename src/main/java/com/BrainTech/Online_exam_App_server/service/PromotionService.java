package com.BrainTech.Online_exam_App_server.service;

import com.BrainTech.Online_exam_App_server.model.Promotion;
import com.BrainTech.Online_exam_App_server.model.Student;

import java.util.List;
import java.util.Optional;

public interface PromotionService {
    List<Promotion> getAllPromotions();
    Optional<Promotion> getPromotionById(Long id);
    Promotion createPromotion(Promotion promotion);
    Promotion updatePromotion(Long id, Promotion promotion);
    void deletePromotion(Long id);
    Optional<Promotion> findByNom(String nom);
     //Ajouter un étudiant à une promotion.

    Promotion addStudentToPromotion(Long promotionId, Long studentId);

    Promotion removeStudentFromPromotion(Long studentId, Long promotionId); //Retirer un étudiant d'une promotion.



}
