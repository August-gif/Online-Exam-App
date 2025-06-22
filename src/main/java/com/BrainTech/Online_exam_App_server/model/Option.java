package com.BrainTech.Online_exam_App_server.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Option {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String libellé;

    private boolean estCorrecte = false;

    @ManyToOne
    @JoinColumn(name = "question_id")
    private QuestionChoixMultiple question;

    @ManyToOne
    @JoinColumn(name = "question_mixte_id") // Conservez ceci
    private QuestionMixte questionMixte; // Conservez ceci

}
