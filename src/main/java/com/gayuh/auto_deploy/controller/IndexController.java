package com.gayuh.auto_deploy.controller;

import com.gayuh.auto_deploy.repository.BuildHistoryRepository;
import com.gayuh.auto_deploy.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@Slf4j
@RestController
@RequestMapping("api/v1")
@RequiredArgsConstructor
public class IndexController {

    private final ProjectRepository projectRepository;
    private final BuildHistoryRepository buildHistoryRepository;
    private final ProcessBuilder processBuilder = new ProcessBuilder();

    @PostMapping(value = "commands")
    public ResponseEntity<Object> saveCommand() {

        return null;
    }

    @GetMapping(value = "test", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> test() throws IOException {
        boolean isWindows = System.getProperty("os.name").toLowerCase().contains("windows");

        if (isWindows) {
            processBuilder.command("cmd.exe", "/c", "ping www.google.com");
        } else {
            processBuilder.command("sh", "-c", "whoami");
        }

        log.info(processBuilder.command().toString());

        Process process = processBuilder.start();

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
