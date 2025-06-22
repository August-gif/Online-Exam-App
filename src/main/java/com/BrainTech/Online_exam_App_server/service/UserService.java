package com.BrainTech.Online_exam_App_server.service;

import com.BrainTech.Online_exam_App_server.model.Professsor;
import com.BrainTech.Online_exam_App_server.model.Student;

import java.util.List;
import java.util.Optional;

public interface UserService {
    /**
     * Tente de trouver un étudiant par son nom d'utilisateur, qui peut être son email ou son matricule.
     *
     * @param username L'email ou le matricule de l'étudiant.
     * @return Un Optional contenant l'objet Student si trouvé, vide sinon.
     */
    Optional<Student> findStudentByUsername(String username);

    /**
     * Tente de trouver un professeur par son nom d'utilisateur, qui est son email.
     *
     * @param username L'email du professeur.
     * @return Un Optional contenant l'objet Professsor si trouvé, vide sinon.
     */
    Optional<Professsor> findProfessorByUsername(String username);


}
