package com.BrainTech.Online_exam_App_server.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReponseEtudiant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private LocalDateTime dateSoumission;
    private Double scoreTotal; // Score total obtenu pour cet examen

    @ManyToOne
    @JoinColumn(name = "etudiant_id", nullable = false)
    private Student etudiant;

    @ManyToOne
    @JoinColumn(name = "examen_id", nullable = false)
    private Exam examen;

    @OneToMany(mappedBy = "reponseEtudiant", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReponseQuestionEtudiant> reponsesAuxQuestion;
}
