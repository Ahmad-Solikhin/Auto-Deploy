package com.gayuh.auto_deploy.controller;

import com.gayuh.auto_deploy.dto.ProjectRequest;
import com.gayuh.auto_deploy.service.ProjectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
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
    private final ProcessBuilder processBuilder;
    private final ProjectService projectService;


    @GetMapping(value = "{idOrName}")
    public ResponseEntity<Object> getById(@PathVariable(name = "idOrName") String idOrName) {
        var response = projectService.getProjectByIdOrName(idOrName);

        return ResponseEntity.ok(Map.of("data", response, "message", "oke"));
    }

    @GetMapping
    public ResponseEntity<Object> getAll() {
        var response = projectService.getAllProject();

        return ResponseEntity.ok(Map.of("data", response, "message", "oke"));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Object> saveProject(
            @RequestParam(name = "file") MultipartFile file,
            @RequestParam(name = "name") String name,
            @RequestParam(name = "language") String language,
            @RequestParam(name = "description") String description
    ) throws IOException, InterruptedException {
        var request = ProjectRequest.builder()
                .name(name)
                .language(language)
                .description(description)
                .build();

        log.info(file.getContentType());

        projectService.addProject(request, file);

        return ResponseEntity.ok("Oke");
    }

    @PutMapping(value = "{projectId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Object> editProject(
            @PathVariable(name = "projectId") String projectId,
            @RequestParam(name = "file") MultipartFile file,
            @RequestParam(name = "name") String name,
            @RequestParam(name = "language") String language,
            @RequestParam(name = "description") String description
    ) {
        log.info(projectId);
        log.info(file.getContentType());
        var projectRequest = ProjectRequest.builder()
                .name(name)
                .language(language)
                .description(description)
                .build();
        log.info(projectRequest.toString());
        return ResponseEntity.ok(null);
    }

    @DeleteMapping(value = "{projectId}")
    public ResponseEntity<Object> deleteProject(@PathVariable(name = "projectId") String projectId) {
        //Todo : Delete project by id

        return ResponseEntity.ok(Map.of("message", "success delete data"));
    }

    @GetMapping(value = "test/{command}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> test(@PathVariable String command) throws IOException {
        boolean isWindows = System.getProperty("os.name").toLowerCase().contains("windows");

        if (isWindows) {
            processBuilder.command("powershell.exe", "/c", command);
        } else {
            processBuilder.command("sh", "-c", "whoami");
        }

        log.info(processBuilder.command().toString());

        Process process = processBuilder.start();

        //Todo: Send the process to async function to insert it to logs and to calculate the execution time, and if there are error in ErrorStream change the build success to failed

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));

        return Flux.fromStream(bufferedReader.lines());

        /*return Flux.using(
                // resource factory creates FileReader instance
                () -> new InputStreamReader(process.getInputStream()),
                // transformer function turns the FileReader into a Flux
                reader -> Flux.fromStream(new BufferedReader(reader).lines()),
                // resource cleanup function closes the FileReader when the Flux is complete
                reader -> {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
        );*/
    }
}
