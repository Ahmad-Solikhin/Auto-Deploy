package com.gayuh.auto_deploy.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.nio.file.Paths;

@Component
@Slf4j
public class ConfigurationApp {
    @Value("${data.DEPLOY_PATH}")
    private String commandFolderPath;

    @Bean
    public Path getCommandShellFolder() {
        log.warn("Path is {}", commandFolderPath);
        return Paths.get(commandFolderPath);
    }

    @Bean
    public ProcessBuilder getProcessBuilder() {
        return new ProcessBuilder();
    }
}
