package com.cicd.demo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "builds")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Build {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BuildStatus status;

    @Column(columnDefinition = "TEXT")
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
