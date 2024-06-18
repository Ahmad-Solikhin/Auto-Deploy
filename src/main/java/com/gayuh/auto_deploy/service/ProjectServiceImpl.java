package com.gayuh.auto_deploy.service;

import com.gayuh.auto_deploy.dto.BuildHistoryResponse;
import com.gayuh.auto_deploy.dto.ProjectDetailResponse;
import com.gayuh.auto_deploy.dto.ProjectRequest;
import com.gayuh.auto_deploy.dto.ProjectResponse;
import com.gayuh.auto_deploy.entity.Project;
import com.gayuh.auto_deploy.repository.ProjectRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProjectServiceImpl implements ProjectService {
    private final ProcessBuilder processBuilder;
    private final Path folderCommandPath;
    private final ProjectRepository projectRepository;
    private final CommandShellService commandShellService;
    private final BuildHistoryService buildHistoryService;

    @Override
    public ProjectDetailResponse getProjectById(String projectId) {
        if (Objects.isNull(projectId))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Please provide correct project id");

        var queries = projectRepository.findProjectBuildHistoryQueryById(projectId);

        if (queries.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found");

        var listBuildHistory = new ArrayList<BuildHistoryResponse>();

        if (queries.get(0).buildHistoryId() != null) {
            queries.forEach(query -> listBuildHistory.add(new BuildHistoryResponse(
                    query.buildHistoryId(),
                    query.success(),
                    null,
                    query.executionTime(),
                    null,
                    query.executeAt()
            )));
        }


        return new ProjectDetailResponse(
                queries.get(0).projectId(),
                queries.get(0).name(),
                queries.get(0).language(),
                queries.get(0).description(),
                null,
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
    public Flux<String> buildProject(String projectId) throws InterruptedException, IOException {
        var project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found"));


        processBuilder.command("/bin/bash", "-c", project.getPath());

        log.info("Start build project {} with path {}", project.getName(), project.getPath());

        Process process = processBuilder.start();

        buildHistoryService.addBuildHistory(process, project);

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));

        return Flux.fromStream(bufferedReader.lines());
    }

    private void allowExecuteFile(String fileName) throws IOException {
        Path filePath = folderCommandPath.resolve(fileName);

        processBuilder.command("/bin/bash", "-c", "chmod", "+x", filePath.toAbsolutePath().toString());

        Process process;
        try {
            process = processBuilder.start();
        } catch (IOException exception) {
            commandShellService.deleteCommandShellFile(fileName);
            log.error(exception.getMessage());
            Arrays.stream(exception.getStackTrace()).forEach(err -> log.error(err.toString()));
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, exception.getMessage());
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()));

        if (reader.readLine() != null) {
            commandShellService.deleteCommandShellFile(fileName);
            log.error(reader.readLine());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Something wrong with the file");
        }
    }
}
