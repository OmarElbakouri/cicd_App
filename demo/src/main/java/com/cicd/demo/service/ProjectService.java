package com.cicd.demo.service;

import com.cicd.demo.model.Project;

import java.util.List;
import java.util.Optional;

public interface ProjectService {
    List<Project> getAllProjects();
    Optional<Project> getProjectById(Long id);
    Project saveProject(Project project);
    void deleteProject(Long id);
}
