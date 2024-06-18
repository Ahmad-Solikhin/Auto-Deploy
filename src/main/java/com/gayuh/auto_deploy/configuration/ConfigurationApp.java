package com.gayuh.auto_deploy.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Slf4j
@Component
@EnableAsync
public class ConfigurationApp {
    @Value("${data.DEPLOY_PATH}")
    private String commandFolderPath;

    @Bean
    public Path getCommandShellFolder() {
        return Paths.get(commandFolderPath);
    }

    @Bean
    public ProcessBuilder getProcessBuilder() {
        return new ProcessBuilder();
    }

    @Bean
    public Executor taskExecutor() {
        return Executors.newFixedThreadPool(2);
    }
}
