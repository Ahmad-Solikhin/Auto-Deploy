package com.gayuh.auto_deploy.service;

import com.gayuh.auto_deploy.entity.BuildHistory;
import com.gayuh.auto_deploy.entity.BuildHistoryLog;
import com.gayuh.auto_deploy.entity.Project;
import com.gayuh.auto_deploy.repository.BuildHistoryLogRepository;
import com.gayuh.auto_deploy.repository.BuildHistoryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BuildHistoryServiceImpl implements BuildHistoryService {
    private final BuildHistoryRepository buildHistoryRepository;
    private final BuildHistoryLogRepository buildHistoryLogRepository;

    @Async
    @Override
    @Transactional
    public void addBuildHistory(Flux<String> lines, Project project, long startTime) {

        List<String> dataLines = lines.collectList().block();

        var buildHistory = BuildHistory.builder()
                .executeAt(startTime)
                .project(project)
                .executionTime(Duration.between(Instant.ofEpochMilli(startTime), LocalDateTime.now().atOffset(ZoneOffset.UTC)).toMillis())
                .build();

        buildHistoryRepository.save(buildHistory);

        buildHistoryLogRepository.save(
                BuildHistoryLog.builder()
                        .line(String.join("\n", dataLines))
                        .buildHistory(buildHistory)
                        .build()
        );
    }
}
