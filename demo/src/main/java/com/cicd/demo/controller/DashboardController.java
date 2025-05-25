package com.cicd.demo.controller;

import com.cicd.demo.model.BuildStatus;
import com.cicd.demo.service.BuildService;
import com.cicd.demo.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final ProjectService projectService;
    private final BuildService buildService;

    @GetMapping
    public String dashboard(Model model) {
        try {
            // Project stats
            model.addAttribute("projectCount", projectService.getAllProjects().size());
            
            // Build stats
            var allBuilds = buildService.getAllBuilds();
            long successfulBuilds = allBuilds.stream()
                    .filter(build -> build.getStatus() == BuildStatus.SUCCESS)
                    .count();
            long failedBuilds = allBuilds.stream()
                    .filter(build -> build.getStatus() == BuildStatus.FAILURE)
                    .count();
            
            model.addAttribute("successfulBuilds", successfulBuilds);
            model.addAttribute("failedBuilds", failedBuilds);
            
            // Recent projects and builds
            model.addAttribute("recentProjects", projectService.getAllProjects());
            
            // Séparer les builds récents par statut
            var recentBuilds = buildService.getRecentBuilds(10);
            var successBuilds = recentBuilds.stream()
                    .filter(build -> build.getStatus() == BuildStatus.SUCCESS)
                    .limit(5)
                    .toList();
            var failureBuilds = recentBuilds.stream()
                    .filter(build -> build.getStatus() == BuildStatus.FAILURE)
                    .limit(5)
                    .toList();
                    
            model.addAttribute("successBuilds", successBuilds);
            model.addAttribute("failureBuilds", failureBuilds);
            
            return "dashboard";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error loading dashboard: " + e.getMessage());
            return "dashboard";
        }
    }
}
