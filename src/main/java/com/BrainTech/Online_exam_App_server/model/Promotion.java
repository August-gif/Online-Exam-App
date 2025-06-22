package com.BrainTech.Online_exam_App_server.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Promotion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String nom;
    private String description;
    @Column(nullable = false)
    private LocalDateTime dateCreation;
    @ManyToMany(mappedBy = "promotionsCibles")
    private List<Exam> exams;
    @OneToMany(mappedBy = "promotion")
    private List<Student> students;

}
