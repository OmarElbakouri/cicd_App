package com.cicd.demo.service;

import com.cicd.demo.model.Build;
import com.cicd.demo.model.BuildStatus;
import com.cicd.demo.model.Project;
import com.cicd.demo.repository.BuildRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermission;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class BuildServiceImpl implements BuildService {

    private final BuildRepository buildRepository;
    private final ProjectService projectService;

    @Value("${cicd.docker.image}")
    private String dockerImage;

    @Value("${cicd.docker.workdir}")
    private String dockerWorkdir;

    @Override
    public List<Build> getAllBuilds() {
        return buildRepository.findAll();
    }

    @Override
    public List<Build> getRecentBuilds(int limit) {
        return buildRepository.findAllByOrderByStartTimeDesc(PageRequest.of(0, limit));
    }

    @Override
    public List<Build> getBuildsByProjectId(Long projectId) {
        return buildRepository.findByProjectId(projectId);
    }

    @Override
    public Optional<Build> getBuildById(Long id) {
        return buildRepository.findById(id);
    }

    @Override
    public Build saveBuild(Build build) {
        return buildRepository.save(build);
    }

    @Override
    public Build triggerBuild(Long projectId) {
        Optional<Project> projectOpt = projectService.getProjectById(projectId);
        if (projectOpt.isEmpty()) {
            throw new IllegalArgumentException("Project not found with ID: " + projectId);
        }

        Project project = projectOpt.get();
        Build build = new Build();
        build.setProject(project);
        build.setStatus(BuildStatus.PENDING);
        build.setStartTime(LocalDateTime.now());
        
        Build savedBuild = saveBuild(build);
        
        // Run the build process asynchronously
        CompletableFuture.runAsync(() -> executeBuild(savedBuild));
        
        return savedBuild;
    }
    
    private void executeBuild(Build build) {
        StringBuilder logs = new StringBuilder();
        try {
            build.setStatus(BuildStatus.RUNNING);
            saveBuild(build);
            
            Project project = build.getProject();
            String buildId = UUID.randomUUID().toString();
            String containerName = "build-" + buildId;
            String repoUrl = project.getRepositoryUrl();
            String branch = project.getBranch();
            
            // Prepare the build runner script
            String scriptPath = prepareRunnerScript();
            if (scriptPath == null) {
                throw new IOException("Failed to prepare build runner script");
            }
            
            logs.append("Starting build for project: ").append(project.getName())
                .append("\nRepository: ").append(repoUrl)
                .append("\nBranch: ").append(branch)
                .append("\n\n");
            
            // Create a unique workspace directory for this build
            String uniqueWorkspace = dockerWorkdir + "/build-" + buildId;
            
            // Command to run Docker container with Maven to clone and build the project
            String[] command = {
                "docker", "run", "--name", containerName, "--rm",
                "-v", scriptPath + ":/build-runner.sh",
                "-v", uniqueWorkspace + ":/workspace",
                dockerImage,
                "/bin/sh", "/build-runner.sh", repoUrl, branch
            };
            
            logs.append("Executing command: ").append(String.join(" ", command)).append("\n\n");
            
            Process process = Runtime.getRuntime().exec(command);
            
            // Capture the output
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    logs.append(line).append("\n");
                }
            }
            
            // Capture the error output
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    logs.append(line).append("\n");
                }
            }
            
            int exitCode = process.waitFor();
            build.setStatus(exitCode == 0 ? BuildStatus.SUCCESS : BuildStatus.FAILURE);
            
            logs.append("\nBuild completed with exit code: ").append(exitCode)
                .append("\nStatus: ").append(build.getStatus());
            
        } catch (Exception e) {
            logs.append("\nBuild failed with error: ").append(e.getMessage());
            build.setStatus(BuildStatus.FAILURE);
            log.error("Error executing build", e);
        } finally {
            build.setLogs(logs.toString());
            build.setEndTime(LocalDateTime.now());
            saveBuild(build);
        }
    }
    
    private String prepareRunnerScript() {
        try {
            // Create a temporary directory to store the script
            Path tempDir = Files.createTempDirectory("mini-cicd-");
            Path scriptPath = tempDir.resolve("build-runner.sh");
            
            // Copy the script from resources to the temporary directory
            ClassPathResource resource = new ClassPathResource("scripts/build-runner.sh");
            FileCopyUtils.copy(resource.getInputStream(), Files.newOutputStream(scriptPath));
            
            // Make the script executable (if on a POSIX system)
            try {
                Set<PosixFilePermission> permissions = new HashSet<>();
                permissions.add(PosixFilePermission.OWNER_READ);
                permissions.add(PosixFilePermission.OWNER_WRITE);
                permissions.add(PosixFilePermission.OWNER_EXECUTE);
                Files.setPosixFilePermissions(scriptPath, permissions);
            } catch (UnsupportedOperationException e) {
                // Not on a POSIX system, ignore
                log.warn("Could not set execute permissions on script (not a POSIX system)");
            }
            
            return scriptPath.toAbsolutePath().toString();
        } catch (IOException e) {
            log.error("Failed to prepare runner script", e);
            return null;
        }
    }
}
