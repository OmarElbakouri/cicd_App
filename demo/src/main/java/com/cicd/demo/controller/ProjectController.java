package com.cicd.demo.controller;

import com.cicd.demo.model.Project;
import com.cicd.demo.service.ProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/projects")
//Indique que le contrôleur doit être initialisé avec des dépendances injectées.
@RequiredArgsConstructor
//Indique qu'une classe est un contrôleur Spring MVC qui gère les requêtes HTTP et retourne des vues.
public class ProjectController {

    private final ProjectService projectService;

    @GetMapping
    //Indique que la méthode listProjects() sera mappée à la requête GET sur "/projects".
    public String listProjects(Model model) {
        model.addAttribute("projects", projectService.getAllProjects());
        model.addAttribute("project", new Project());
        return "projects/list";
    }

    @PostMapping 
    public String addProject(@Valid/*demande à Spring de valider automatiquement l'objet*/  @ModelAttribute("project") Project project, 
                           BindingResult result, 
                           RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "projects/list";
        }
        
        projectService.saveProject(project);
        redirectAttributes.addFlashAttribute("successMessage", "Project added successfully!");
        return "redirect:/projects";
    }

    @GetMapping("/{id}")
    public String viewProject(@PathVariable/* Extrait des variables depuis l'URL*/  Long id, Model model) {
        return projectService.getProjectById(id)
                .map(project -> {
                    model.addAttribute("project", project);
                    return "projects/view";
                })
                .orElse("redirect:/projects");
    }

    @GetMapping("/{id}/edit")
    public String editProjectForm(@PathVariable Long id, Model model) {
        return projectService.getProjectById(id)
                .map(project -> {
                    model.addAttribute("project", project);
                    return "projects/edit";
                })
                .orElse("redirect:/projects");
    }

    @PostMapping("/{id}")
    public String updateProject(@PathVariable Long id, 
                              @Valid @ModelAttribute("project") Project project,
                              BindingResult result,
                              RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "projects/edit";
        }
        
        projectService.saveProject(project);
        redirectAttributes.addFlashAttribute("successMessage", "Project updated successfully!");
        return "redirect:/projects";
    }

    @PostMapping("/{id}/delete")
    public String deleteProject(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        projectService.deleteProject(id);
        redirectAttributes.addFlashAttribute("successMessage", "Project deleted successfully!");
        return "redirect:/projects";
    }
}
