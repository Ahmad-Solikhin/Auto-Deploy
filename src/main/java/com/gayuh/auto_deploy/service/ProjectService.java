package com.gayuh.auto_deploy.service;

import com.gayuh.auto_deploy.dto.ProjectRequest;
import com.gayuh.auto_deploy.dto.ProjectResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ProjectService {
    ProjectResponse getProjectById(String projectId);

    List<ProjectResponse> getAllProject();

    void addProject(ProjectRequest request, MultipartFile file) throws IOException;

    void deleteProject(String projectId);

    void updateProject(ProjectRequest request, MultipartFile file) throws IOException;
}
