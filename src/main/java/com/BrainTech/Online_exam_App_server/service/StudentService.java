package com.BrainTech.Online_exam_App_server.service;

import com.BrainTech.Online_exam_App_server.model.Student;

import java.util.List;
import java.util.Optional;

public interface StudentService {
    Student createStudent(Student student);
    Optional<Student> getStudentById(Long id);
    List<Student> getAllStudents();
    Student updateStudent(Long id, Student studentDetails);
    void deleteStudent(Long id);

    // Suppression des méthodes d'inscription/désinscription directes d'examens
    // car elles sont maintenant gérées via StudentExamParticipationService.
    // List<Exam> getExamsByStudent(Long studentId); // Cette logique sera dans StudentExamParticipationService.
}
