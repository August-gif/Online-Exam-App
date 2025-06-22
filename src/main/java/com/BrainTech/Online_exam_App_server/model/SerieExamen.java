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
public class SerieExamen {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String nom;
    private String description;
    @Column(nullable = false)
    private LocalDateTime dateCreation;

    @ManyToMany
    @JoinTable(
            name = "serie_examen_examen",
            joinColumns = @JoinColumn(name = "serie_examen_id"),
            inverseJoinColumns = @JoinColumn(name = "examen_id")
    )
    private List<Exam> examens;

    @ManyToMany(mappedBy = "serieExamen")
    private List<Student> etudiants;

}
