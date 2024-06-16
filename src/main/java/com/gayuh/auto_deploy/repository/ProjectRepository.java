package com.gayuh.auto_deploy.repository;

import com.gayuh.auto_deploy.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project, String> {
    Optional<Project> findByIdOrName(String id, String name);
}
