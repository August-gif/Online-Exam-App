package com.BrainTech.Online_exam_App_server.service.implementation;

import com.BrainTech.Online_exam_App_server.exceptions.ResourceNotFoundException;
import com.BrainTech.Online_exam_App_server.model.Student;
import com.BrainTech.Online_exam_App_server.repository.StudentRepository;
import com.BrainTech.Online_exam_App_server.service.StudentService;
import org.springframework.security.crypto.password.PasswordEncoder;


import java.util.List;
import java.util.Optional;

public class StudentServiceImpl implements StudentService {
    private final StudentRepository studentRepository;
    private final PasswordEncoder passwordEncoder; // Injectez PasswordEncoder

    public StudentServiceImpl(StudentRepository studentRepository,PasswordEncoder passwordEncoder ) {
        this.studentRepository = studentRepository;
        this.passwordEncoder=passwordEncoder;

    }

    @Override
    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    @Override
    public Optional<Student> getStudentById(Long id) {
        return studentRepository.findById(id);
    }

    @Override
    public Student createStudent(Student student) {
        // Hacher le mot de passe avant de sauvegarder
        student.setMotDePasse(passwordEncoder.encode(student.getMotDePasse()));
        return studentRepository.save(student);
    }

    @Override
    public Student updateStudent(Long id, Student student) {
        return studentRepository.findById(id)
                .map(existingStudent -> {
                    student.setId(id);
                    // Ne mettez à jour le mot de passe que s'il est fourni et différent
                    if (student.getMotDePasse() != null && !student.getMotDePasse().isEmpty() &&
                            !passwordEncoder.matches(student.getMotDePasse(), existingStudent.getMotDePasse()))
                    {
                        student.setMotDePasse(passwordEncoder.encode(student.getMotDePasse()));
                    } else {
                        student.setMotDePasse(existingStudent.getMotDePasse());
                    }
                    return studentRepository.save(student);
                })
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with ID: " + id)); // Ou lancez une exception
    }

    @Override
    public void deleteStudent(Long id) {
        studentRepository.deleteById(id);

    }

    @Override
    public Optional<Student> findByMatricule(String matricule) {
        return studentRepository.findByMatricule(matricule);
    }

    @Override
    public Optional<Student> findByNom(String nom) {
        return Optional.empty();
    }

    @Override
    public Optional<Student> findByEmail(String email) {
        return studentRepository.findByEmail(email);
    }

    @Override
    public List<Student> getStudentsByPromotionId(Long promotionId) {
        return studentRepository.findByPromotionId(promotionId);
    }
}
