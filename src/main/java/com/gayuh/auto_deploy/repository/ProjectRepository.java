package com.gayuh.auto_deploy.repository;

import com.gayuh.auto_deploy.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<Project, String> {
}
