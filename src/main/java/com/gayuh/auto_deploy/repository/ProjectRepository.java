package com.gayuh.auto_deploy.repository;

import com.gayuh.auto_deploy.dto.ProjectResponse;
import com.gayuh.auto_deploy.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project, String> {
    @Query(value = """
                select new com.gayuh.auto_deploy.dto.ProjectResponse(
                id, name, language, description
                ) from Project where id = :projectId
            """)
    Optional<ProjectResponse> findProjectResponseById(String projectId);
    @Query(value = """
                select new com.gayuh.auto_deploy.dto.ProjectResponse(
                id, name, language, description
                ) from Project
            """)
    List<ProjectResponse> findAllProjectResponse();
}
