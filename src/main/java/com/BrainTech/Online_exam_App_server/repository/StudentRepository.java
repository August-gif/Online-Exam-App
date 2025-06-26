package com.BrainTech.Online_exam_App_server.repository;

import com.BrainTech.Online_exam_App_server.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student,Long> {
    // Utile pour la recherche par matricule et email (qui sont uniques).
    Optional<Student> findByNom(String matricule);
    Optional<Student> findByEmail(String email);

    // Pour trouver les étudiants d'une promotion spécifique.
    Optional<Student> findById(Long Id);
}
