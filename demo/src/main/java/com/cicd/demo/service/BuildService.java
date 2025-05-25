package com.cicd.demo.service;

import com.cicd.demo.model.Build;

import java.util.List;
import java.util.Optional;

public interface BuildService {
    List<Build> getAllBuilds();
    List<Build> getRecentBuilds(int limit);
    List<Build> getBuildsByProjectId(Long projectId);
    Optional<Build> getBuildById(Long id);
    Build saveBuild(Build build);
    Build triggerBuild(Long projectId);
}
