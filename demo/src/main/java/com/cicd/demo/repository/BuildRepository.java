package com.cicd.demo.repository;

import com.cicd.demo.model.Build;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BuildRepository extends JpaRepository<Build, Long> {
    List<Build> findByProjectId(Long projectId);
    List<Build> findAllByOrderByStartTimeDesc(Pageable pageable);
}
