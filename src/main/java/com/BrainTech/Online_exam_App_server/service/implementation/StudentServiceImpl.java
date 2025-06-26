package com.BrainTech.Online_exam_App_server.service.implementation;

import com.BrainTech.Online_exam_App_server.exceptions.InvalidOperationException;
import com.BrainTech.Online_exam_App_server.exceptions.ResourceNotFoundException;
import com.BrainTech.Online_exam_App_server.model.Student;
import com.BrainTech.Online_exam_App_server.repository.StudentRepository;
import com.BrainTech.Online_exam_App_server.service.StudentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
@Slf4j
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;
    @Override
    @Transactional
    public Student createStudent(Student student) {
        log.info("Tentative de création d'un étudiant avec l'email: {} et nom : {}", student.getEmail(),student.getNom());
        if (studentRepository.findByEmail(student.getEmail()).isPresent()) {
            throw new InvalidOperationException("Un étudiant avec cet email existe déjà.");
        }
        Student savedStudent = studentRepository.save(student);
        log.info("Étudiant créé avec succès avec l'ID: {}", savedStudent.getId());
        return savedStudent;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Student> getStudentById(Long id) {
        log.debug("Récupération de l'étudiant avec l'ID: {}", id);
        return studentRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Student> getAllStudents() {
        log.debug("Récupération de tous les étudiants.");
        return studentRepository.findAll();
    }

    @Override
    @Transactional
    public Student updateStudent(Long id, Student studentDetails) {
        log.info("Tentative de mise à jour de l'étudiant avec l'ID: {}", id);
        Student existingStudent = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Étudiant non trouvé avec l'ID: " + id));

        if (!existingStudent.getEmail().equals(studentDetails.getEmail())) {
            if (studentRepository.findByEmail(studentDetails.getEmail()).isPresent()) {
                throw new InvalidOperationException("Un autre étudiant avec cet email existe déjà.");
            }
        }

        existingStudent.setNom(studentDetails.getNom());
        existingStudent.setPrenom(studentDetails.getPrenom());
        existingStudent.setEmail(studentDetails.getEmail());
        existingStudent.setDateNaissance(studentDetails.getDateNaissance());

        Student updatedStudent = studentRepository.save(existingStudent);
        log.info("Étudiant avec l'ID {} mis à jour avec succès.", id);
        return updatedStudent;
    }

    @Override
    @Transactional
    public void deleteStudent(Long id) {
        log.info("Tentative de suppression de l'étudiant avec l'ID: {}", id);
        if (!studentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Étudiant non trouvé avec l'ID: " + id);
        }
        studentRepository.deleteById(id);
        log.info("Étudiant avec l'ID {} supprimé avec succès.", id);

    }
}
