package com.gayuh.auto_deploy.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ConcurrentTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Slf4j
@Component
@EnableAsync
public class ConfigurationApp implements WebMvcConfigurer {
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

    @Override
    public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
        configurer.setTaskExecutor(new ConcurrentTaskExecutor(Executors.newFixedThreadPool(10)));
    }
}
