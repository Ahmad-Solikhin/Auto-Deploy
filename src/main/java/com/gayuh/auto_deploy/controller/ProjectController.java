package com.gayuh.auto_deploy.controller;

import com.gayuh.auto_deploy.dto.ProjectRequest;
import com.gayuh.auto_deploy.service.ProjectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("api/v1/projects")
@RequiredArgsConstructor
public class ProjectController {
    @Value("${SECRET_KEY}")
    private String secretKey;
    //private final ProcessBuilder processBuilder;
    private final ProjectService projectService;
    //private final BuildHistoryService buildHistoryService;

    //For now only print the output, not save the history into the database because for now i don't know how to do that simultaneously

    private void checkHeaderSecret(String secret) {
        if (!secret.equals(secretKey))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Secret key is not valid");
    }

    @GetMapping(value = "{projectId}")
    public ResponseEntity<Object> getById(
            @PathVariable(name = "projectId") String projectId,
            @RequestHeader(name = "SECRET_KEY") String secret
    ) {
        checkHeaderSecret(secret);

        var response = projectService.getProjectById(projectId);

        return ResponseEntity.ok(Map.of("data", response, "message", "Success, data found"));
    }

    @GetMapping
    public ResponseEntity<Object> getAll(@RequestHeader(name = "SECRET_KEY") String secret) {
        checkHeaderSecret(secret);
        var response = projectService.getAllProject();

        return ResponseEntity.ok(Map.of("data", response, "message", "Success, data count is " + response.size()));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Object> saveProject(
            @RequestParam(name = "file") MultipartFile file,
            @RequestParam(name = "name") String name,
            @RequestParam(name = "language") String language,
            @RequestParam(name = "description") String description,
            @RequestHeader(name = "SECRET_KEY") String secret
    ) throws IOException {
        checkHeaderSecret(secret);
        var request = new ProjectRequest(null, name, language, description);

        projectService.addProject(request, file);

        return ResponseEntity.ok("Oke");
    }

    @PutMapping(value = "{projectId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Object> editProject(
            @PathVariable(name = "projectId") String projectId,
            @RequestParam(name = "file", required = false) MultipartFile file,
            @RequestParam(name = "name") String name,
            @RequestParam(name = "language") String language,
            @RequestParam(name = "description") String description,
            @RequestHeader(name = "SECRET_KEY") String secret
    ) throws IOException {
        checkHeaderSecret(secret);
        var request = new ProjectRequest(projectId, name, language, description);

        projectService.updateProject(request, file);

        return ResponseEntity.ok(null);
    }

    @DeleteMapping(value = "{projectId}")
    public ResponseEntity<Object> deleteProject(
            @PathVariable(name = "projectId") String projectId,
            @RequestHeader(name = "SECRET_KEY") String secret
    ) {
        checkHeaderSecret(secret);
        projectService.deleteProject(projectId);

        return ResponseEntity.ok(Map.of("message", "success delete data"));
    }

    @GetMapping(value = "/build/{projectId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> startProjectBuild(
            @PathVariable(name = "projectId") String projectId,
            @RequestHeader(name = "SECRET_KEY", required = false) String secret,
            @RequestParam(name = "secret", required = false) String secretParam
    ) throws IOException {

        if (secretParam == null && secret == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Must provide ecret valid key");

        if (secretParam != null) checkHeaderSecret(secretParam);

        if (secret != null) checkHeaderSecret(secret);

        var project = projectService.getEntityProjectById(projectId);

        var builder = projectService.buildProject(project);

        Process process = builder.start();

        BufferedReader stream = new BufferedReader(new InputStreamReader(process.getInputStream()));

        return Flux.using(
                () -> stream,
                reader -> Flux.fromStream(new BufferedReader(reader).lines()),
                reader -> {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
        );

        //buildHistoryService.addBuildHistoryLog(response, null);

        /*return Flux.using(
                () -> stream,
                reader -> Flux.fromStream(new BufferedReader(reader).lines()),
                reader -> {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
        ).collectList().subscribe(dataList -> buildHistoryService.addBuildHistory(dataList, project, startTime, LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)));*/

        //return Flux.fromStream(stream.lines());
    }
}
