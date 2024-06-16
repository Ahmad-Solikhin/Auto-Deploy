package com.gayuh.auto_deploy.service;

import com.gayuh.auto_deploy.dto.ProjectRequest;
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
    public Project getProjectByIdOrName(String idOrName) {
        if (Objects.isNull(idOrName))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Please provide project id or project name");

        return projectRepository
                .findByIdOrName(idOrName, idOrName)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Id or name " + idOrName + " not found")
                );
    }

    @Override
    public List<Project> getAllProject() {
        return projectRepository.findAll();
    }

    @Override
    @Transactional
    public void addProject(ProjectRequest request, MultipartFile file) throws IOException, InterruptedException {
        var project = Project.builder()
                .id(UUID.randomUUID().toString())
                .name(request.getName())
                .language(request.getLanguage())
                .description(request.getDescription())
                .build();

        commandShellService.saveProjectCommand(project, file);

        String fileName = project.getId() + "-" + project.getName();

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
            log.error(reader.readLine());
            commandShellService.deleteCommandShellFile(fileName);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Something wrong with the file");
        }

        projectRepository.save(project);
    }
}
