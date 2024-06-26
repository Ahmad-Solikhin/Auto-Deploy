package com.gayuh.auto_deploy.service;

import com.gayuh.auto_deploy.dto.*;
import com.gayuh.auto_deploy.entity.Project;
import com.gayuh.auto_deploy.repository.ProjectRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProjectServiceImpl implements ProjectService {
    private final ProcessBuilder processBuilder;
    private final Path folderCommandPath;
    private final ProjectRepository projectRepository;
    private final CommandShellService commandShellService;

    @Override
    public Project getEntityProjectById(String projectId) {
        return projectRepository.findById(projectId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Please provide correct project id")
        );
    }

    @Override
    public ProjectDetailResponse getProjectById(String projectId) {
        if (Objects.isNull(projectId))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Please provide correct project id");

        ProjectBuildHistoryQuery queries = projectRepository.findProjectBuildHistoryQueryById(projectId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found")
        );

        var listBuildHistory = new BuildHistoryResponse(
                queries.buildHistoryId(), null, queries.executionTime(), null, queries.executeAt()
        );

        return new ProjectDetailResponse(
                queries.projectId(),
                queries.name(),
                queries.language(),
                queries.description(),
                null,
                null,
                listBuildHistory
        );
    }

    @Override
    public List<ProjectResponse> getAllProject() {
        return projectRepository.findAllProjectResponse();
    }

    @Override
    @Transactional
    public void addProject(ProjectRequest request, MultipartFile file) throws IOException {
        var project = Project.builder()
                .id(UUID.randomUUID().toString())
                .name(request.name())
                .language(request.language())
                .description(request.description())
                .build();

        commandShellService.saveProjectCommand(project, file);

        allowExecuteFile(project.getFileName());

        projectRepository.save(project);
    }

    @Override
    @Transactional
    public void deleteProject(String projectId) {
        var project = projectRepository
                .findById(projectId)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found")
                );

        commandShellService.deleteCommandShellFile(project.getFileName());

        projectRepository.deleteById(project.getId());
    }

    @Override
    @Transactional
    public void updateProject(ProjectRequest request, MultipartFile file) throws IOException {
        var project = projectRepository
                .findById(request.id())
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found")
                );

        project.setName(request.name());
        project.setLanguage(request.language());
        project.setDescription(request.description());

        Boolean isUpdateFile = commandShellService.updateCommandShellFile(project, file);

        log.info("Is update file {}", isUpdateFile);

        if (isUpdateFile) allowExecuteFile(project.getFileName());

        projectRepository.save(project);
    }

    @Override
    public ProcessBuilder buildProject(Project project) {

        log.info("Start build project {} with path {}", project.getName(), project.getPath());

        return processBuilder.command("sh", "-c", project.getPath()).redirectErrorStream(true);
    }

    private void allowExecuteFile(String fileName) throws IOException {
        Path filePath = folderCommandPath.resolve(fileName);

        processBuilder.command("sh", "-c", "chmod +x " + filePath.toAbsolutePath());

        Process process;
        try {
            log.info("Start Command : {}", processBuilder.command());
            process = processBuilder.start();
        } catch (IOException exception) {
            commandShellService.deleteCommandShellFile(fileName);
            log.error(exception.getMessage());
            Arrays.stream(exception.getStackTrace()).forEach(err -> log.error(err.toString()));
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, exception.getMessage());
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()));

        if (reader.lines().findAny().isPresent()) {
            commandShellService.deleteCommandShellFile(fileName);
            reader.lines().forEach(log::error);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Something wrong with the file");
        }

        processBuilder.command("sh", "-c", "dos2unix " + filePath.toAbsolutePath());
        try {
            log.info("Start Command : {}", processBuilder.command());
            processBuilder.start();
        } catch (IOException exception) {
            commandShellService.deleteCommandShellFile(fileName);
            log.error(exception.getMessage());
            Arrays.stream(exception.getStackTrace()).forEach(err -> log.error(err.toString()));
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, exception.getMessage());
        }
    }
}
