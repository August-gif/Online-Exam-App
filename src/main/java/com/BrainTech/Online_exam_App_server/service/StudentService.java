package com.BrainTech.Online_exam_App_server.service;

import com.BrainTech.Online_exam_App_server.model.Student;

import java.util.List;
import java.util.Optional;

public interface StudentService {
    List<Student> getAllStudents();
    Optional<Student> getStudentById(Long id);
    Student createStudent(Student student);
    Student updateStudent(Long id, Student student);
    void deleteStudent(Long id);
    Optional<Student> findByMatricule(String matricule);
    Optional<Student> findByNom(String nom);
    Optional<Student> findByEmail(String email);
    List<Student> getStudentsByPromotionId(Long promotionId);

    // Ajoutez d'autres méthodes spécifiques à la logique métier des étudiants
}
