package com.BrainTech.Online_exam_App_server.model;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor

public class ReponseQuestionEtudiant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;
    @Column(columnDefinition = "TEXT")
    private String reponseConventionnelle;

    @ManyToMany
    @JoinTable(
            name = "reponse_question_option",
            joinColumns = @JoinColumn(name = "reponse_question_id"),
            inverseJoinColumns = @JoinColumn(name = "option_id")
    )
    private List<Option> optionChoisies;

    @Column(columnDefinition = "TEXT")
    private String reponseMixteConventionnelle;

    @ManyToMany
    @JoinTable(
            name = "reponse_question_mixte_option",
            joinColumns = @JoinColumn(name = "reponse_question_id"),
            inverseJoinColumns = @JoinColumn(name = "option_id")
    )
    private  List<Option> optionMixteChoisie;
    @ManyToOne
    @JoinColumn(name = "reponse_etudiant_id", nullable = false)
    private ReponseEtudiant reponseEtudiant;

    private Double score; // Score obtenu pour cette question




}
