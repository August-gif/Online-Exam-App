package com.BrainTech.Online_exam_App_server.service.implementation;

import com.BrainTech.Online_exam_App_server.exceptions.DuplicateResourceException;
import com.BrainTech.Online_exam_App_server.exceptions.ResourceNotFoundException;
import com.BrainTech.Online_exam_App_server.exceptions.InvalidOperationException; // Importez l'exception
import com.BrainTech.Online_exam_App_server.model.Promotion;
import com.BrainTech.Online_exam_App_server.model.Student;
import com.BrainTech.Online_exam_App_server.repository.PromotionRepository;
import com.BrainTech.Online_exam_App_server.repository.StudentRepository;
import com.BrainTech.Online_exam_App_server.service.PromotionService;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class PromotionServiceImpl implements PromotionService {

    private final PromotionRepository promotionRepository;
    private final StudentRepository studentRepository;

    public PromotionServiceImpl(PromotionRepository promotionRepository, StudentRepository studentRepository) {
        this.promotionRepository = promotionRepository;
        this.studentRepository = studentRepository;
    }

    @Override
    public List<Promotion> getAllPromotions() {
        return promotionRepository.findAll();
    }

    @Override
    public Optional<Promotion> getPromotionById(Long id) {
        return promotionRepository.findById(id);
    }

    @Override
    public Promotion createPromotion(Promotion promotion) {
        // Vérifie si une promotion avec le même nom existe déjà
        if (promotionRepository.findByNom(promotion.getNom()).isPresent()) {
            throw new DuplicateResourceException("Promotion with name " + promotion.getNom() + " already exists.");
        }
        return promotionRepository.save(promotion);
    }

    @Override
    public Promotion updatePromotion(Long id, Promotion promotion) {
        return promotionRepository.findById(id)
                .map(existingPromotion -> {
                    // Vérifie si le nom est modifié et s'il est déjà pris par une autre promotion
                    if (!existingPromotion.getNom().equals(promotion.getNom()) &&
                            promotionRepository.findByNom(promotion.getNom()).isPresent()) {
                        throw new DuplicateResourceException("Promotion with name " + promotion.getNom() + " already exists.");
                    }
                    promotion.setId(id);
                    return promotionRepository.save(promotion);
                })
                .orElseThrow(() -> new ResourceNotFoundException("Promotion not found with ID: " + id));
    }

    @Override
    public void deletePromotion(Long id) {
        if (!promotionRepository.existsById(id)) {
            throw new ResourceNotFoundException("Promotion not found with ID: " + id);
        }
        promotionRepository.deleteById(id);
    }

    @Override
    public Optional<Promotion> findByNom(String nom) {
        return promotionRepository.findByNom(nom);
    }


    @Override
    public Promotion addStudentToPromotion(Long promotionId, Long studentId) {
        Promotion promotion = promotionRepository.findById(promotionId)
                .orElseThrow(() -> new ResourceNotFoundException("Promotion not found with ID: " + promotionId));
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with ID: " + studentId));

        if (promotion.getStudents().contains(student)) {
            throw new InvalidOperationException("Student with ID " + studentId + " is already in promotion with ID " + promotionId);
        }

        promotion.getStudents().add(student);
        student.setPromotion(promotion); // Mettez à jour la relation inverse
        studentRepository.save(student); // Sauvegarde l'étudiant pour MAJ sa promotion
        return promotionRepository.save(promotion);
    }

    @Override
    public Promotion removeStudentFromPromotion(Long promotionId, Long studentId) {
        Promotion promotion = promotionRepository.findById(promotionId)
                .orElseThrow(() -> new ResourceNotFoundException("Promotion not found with ID: " + promotionId));
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with ID: " + studentId));

        if (!promotion.getStudents().contains(student)) {
            throw new InvalidOperationException("Student with ID " + studentId + " is not in promotion with ID " + promotionId);
        }

        promotion.getStudents().remove(student);
        student.setPromotion(null); // Détache l'étudiant de la promotion
        studentRepository.save(student); // Sauvegarde l'étudiant pour MAJ sa promotion
        return promotionRepository.save(promotion);
    }
}