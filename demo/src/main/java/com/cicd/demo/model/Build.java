package com.cicd.demo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
//Désigne une classe comme entité JPA persistante en base de données.
@Table(name = "builds")
//Génère automatiquement les getters et setters.
@Data
//Crée un constructeur sans paramètres.
@NoArgsConstructor
//Crée un constructeur avec tous les paramètres.
@AllArgsConstructor
public class Build {

    @Id
    //Génère automatiquement une clé primaire auto-incrémentée.
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    //Indique que la relation est une association bidirectionnelle.
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @Enumerated(EnumType.STRING)
    //Indique que la colonne est de type énumération.
    @Column(nullable = false)
    private BuildStatus status;

    @Column(columnDefinition = "TEXT")
    //Indique que la colonne est de type texte.
    private String logs;

    @Column(nullable = false)
    private LocalDateTime startTime;

    private LocalDateTime endTime;

    @PrePersist
    protected void onCreate() {
        startTime = LocalDateTime.now();
        status = BuildStatus.PENDING;
    }

    public boolean isCompleted() {
        return status == BuildStatus.SUCCESS || status == BuildStatus.FAILURE;
    }
}
