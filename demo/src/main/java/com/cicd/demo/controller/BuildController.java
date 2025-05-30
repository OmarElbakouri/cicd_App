package com.cicd.demo.controller;

import com.cicd.demo.model.Build;
import com.cicd.demo.model.BuildStatus;
import com.cicd.demo.service.BuildService;
import com.cicd.demo.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

//Indique qu'une classe est un contrôleur Spring MVC qui gère les requêtes HTTP et retourne des vues.
@Controller
//Indique que les méthodes de la classe seront mappées aux URLs commençant par "/builds".
@RequestMapping("/builds")
//Indique que le contrôleur doit être initialisé avec des dépendances injectées.
@RequiredArgsConstructor
public class BuildController {

    private final BuildService buildService;
    private final ProjectService projectService;

    @GetMapping
    //Indique que la méthode listBuilds() sera mappée à la requête GET sur "/builds".
    public String listBuilds(Model model) {
        // Rediriger vers la page des builds réussis par défaut
        return "redirect:/builds/success";
    }
    
    @GetMapping("/success")
    //Indique que la méthode listSuccessBuilds() sera mappée à la requête GET sur "/builds/success".
    public String listSuccessBuilds(Model model) {
        try {
            // Récupérer les builds réussis
            var allBuilds = buildService.getRecentBuilds(30);
            var successBuilds = allBuilds.stream()
                    .filter(build -> build.getStatus() == BuildStatus.SUCCESS)
                    .limit(20)
                    .toList();
            
            model.addAttribute("successBuilds", successBuilds);
            return "builds/success";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Erreur lors du chargement des builds réussis: " + e.getMessage());
            model.addAttribute("successBuilds", java.util.Collections.emptyList());
            return "builds/success";
        }
    }
    
    @GetMapping("/failure")
    //Indique que la méthode listFailureBuilds() sera mappée à la requête GET sur "/builds/failure".
    public String listFailureBuilds(Model model) {
        try {
            // Récupérer les builds échoués
            var allBuilds = buildService.getRecentBuilds(30);
            var failureBuilds = allBuilds.stream()
                    .filter(build -> build.getStatus() == BuildStatus.FAILURE)
                    .limit(20)
                    .toList();
            
            model.addAttribute("failureBuilds", failureBuilds);
            return "builds/failure";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Erreur lors du chargement des builds échoués: " + e.getMessage());
            model.addAttribute("failureBuilds", java.util.Collections.emptyList());
            return "builds/failure";
        }
    }

    @GetMapping("/{id}")
    //Indique que la méthode viewBuild() sera mappée à la requête GET sur "/builds/{id}".
        public String viewBuild(@PathVariable Long id, Model model) {
        return buildService.getBuildById(id)
                .map(build -> {
                    model.addAttribute("build", build);
                    return "builds/view";
                })
                .orElse("redirect:/builds");
    }

    @PostMapping("/{projectId}")
    //Indique que la méthode triggerBuild() sera mappée à la requête POST sur "/builds/{projectId}".
        public String triggerBuild(@PathVariable Long projectId, RedirectAttributes redirectAttributes) {
        try {
            Build build = buildService.triggerBuild(projectId);
            redirectAttributes.addFlashAttribute("successMessage", 
                    "Build triggered successfully! Build ID: " + build.getId());
            return "redirect:/builds/" + build.getId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                    "Failed to trigger build: " + e.getMessage());
            return "redirect:/projects";
        }
    }

    @GetMapping("/projects/{projectId}")
    //Indique que la méthode getProjectBuilds() sera mappée à la requête GET sur "/builds/projects/{projectId}".
    public String getProjectBuilds(@PathVariable Long projectId, Model model) {
        return projectService.getProjectById(projectId)
                .map(project -> {
                    model.addAttribute("project", project);
                    
                    // Récupérer tous les builds du projet
                    var projectBuilds = buildService.getBuildsByProjectId(projectId);
                    
                    // Séparer les builds par statut
                    var successBuilds = projectBuilds.stream()
                            .filter(build -> build.getStatus() == BuildStatus.SUCCESS)
                            .toList();
                            
                    var failureBuilds = projectBuilds.stream()
                            .filter(build -> build.getStatus() == BuildStatus.FAILURE)
                            .toList();
                    
                    // Ajouter les listes au modèle
                    model.addAttribute("successBuilds", successBuilds);
                    model.addAttribute("failureBuilds", failureBuilds);
                    
                    return "builds/project-builds";
                })
                .orElse("redirect:/projects");
    }
}
