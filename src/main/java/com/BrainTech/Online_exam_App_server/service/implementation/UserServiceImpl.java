package com.BrainTech.Online_exam_App_server.service.implementation;

import com.BrainTech.Online_exam_App_server.model.Professsor;
import com.BrainTech.Online_exam_App_server.model.Student;
import com.BrainTech.Online_exam_App_server.repository.ProfessorRepository;
import com.BrainTech.Online_exam_App_server.repository.StudentRepository;
import com.BrainTech.Online_exam_App_server.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final StudentRepository studentRepository;
    private final ProfessorRepository professorRepository;

    /**
     * Tente de trouver un étudiant par son nom d'utilisateur, qui peut être son email ou son matricule.
     *
     * @param username L'email ou le matricule de l'étudiant.
     * @return Un Optional contenant l'objet Student si trouvé, vide sinon.
     */
    @Override
    @Transactional(readOnly = true) // Lecture seule, pas de modification de la base de données
    public Optional<Student> findStudentByUsername(String username) {
        log.debug("Recherche d'un étudiant par son Email: {}", username);
        Optional<Student> student = studentRepository.findByEmail(username);
        if (student.isEmpty()) {
            log.debug("Étudiant non trouvé par email, recherche par matricule: {}", username);
            student = studentRepository.findByMatricule(username);
        }
        student.ifPresentOrElse(
                s -> log.debug("Étudiant trouvé: {}", s.getEmail()),
                () -> log.debug("Aucun étudiant trouvé pour l'identifiant: {}", username)
        );
        return student;
    }

    /**
     * Tente de trouver un professeur par son nom d'utilisateur, qui est son email.
     *
     * @param username L'email du professeur.
     * @return Un Optional contenant l'objet Professsor si trouvé, vide sinon.
     */

    @Override
    @Transactional(readOnly = true) // Lecture seule, pas de modification de la base de données
    public Optional<Professsor> findProfessorByUsername(String username) {
        log.debug("Recherche d'un professeur par email: {}", username);
        Optional<Professsor> professor = professorRepository.findByEmail(username);
        professor.ifPresentOrElse(
                p -> log.debug("Professeur trouvé: {}", p.getEmail()),
                () -> log.debug("Aucun professeur trouvé pour l'identifiant: {}", username)
        );
        return professor;
    }
}
