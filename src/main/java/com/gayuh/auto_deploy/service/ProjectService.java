package com.gayuh.auto_deploy.service;

import com.gayuh.auto_deploy.dto.ProjectRequest;
import com.gayuh.auto_deploy.entity.Project;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ProjectService {
    Project getProjectByIdOrName(String idOrName);

    List<Project> getAllProject();

    void addProject(ProjectRequest request, MultipartFile file) throws IOException, InterruptedException;
}
