package com.cicd.demo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity //Désigne une classe comme entité JPA persistante en base de données.
@Table(name = "projects") //Génère automatiquement les getters et setters.
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) //Génère automatiquement une clé primaire auto-incrémentée. 
    private Long id;

    @NotBlank(message = "Project name is required") //Indique que la colonne est obligatoire.
    @Column(nullable = false)
    private String name;

    @NotBlank(message = "Repository URL is required") //Indique que la colonne est obligatoire.
    @Column(nullable = false)
    private String repositoryUrl;

    @Column(nullable = false)
    private String branch = "main"; //Indique que la colonne est obligatoire.

    @Column(nullable = false)
    private LocalDateTime createdAt;   //Indique que la colonne est obligatoire.

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Build> builds = new ArrayList<>();

    @PrePersist //Indique que la méthode onCreate() sera appelée avant la persistance de l'entité.
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
