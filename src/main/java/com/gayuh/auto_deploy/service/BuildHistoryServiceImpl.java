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

import java.io.BufferedReader;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;

@Slf4j
@Service
@RequiredArgsConstructor
public class BuildHistoryServiceImpl implements BuildHistoryService {
    private final BuildHistoryRepository buildHistoryRepository;
    private final BuildHistoryLogRepository buildHistoryLogRepository;

    @Async
    @Override
    @Transactional
    public void addBuildHistory(BufferedReader stream, Project project) throws InterruptedException, IOException {

        long executeAt = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);

        var buildHistory = BuildHistory.builder()
                .executeAt(executeAt)
                .project(project)
                .build();

        buildHistoryRepository.save(buildHistory);

        long sleepDuration = Duration.ofMinutes(5).toMillis();

        Thread.sleep(sleepDuration);

        stream.reset();

        var listBuildHistoryLog = new ArrayList<String>();

        stream.lines().forEach(listBuildHistoryLog::add);

        buildHistory.setExecutionTime(LocalDateTime.now().toEpochSecond(ZoneOffset.UTC) - executeAt - sleepDuration);

        buildHistoryRepository.save(buildHistory);

        buildHistoryLogRepository.save(
                BuildHistoryLog.builder()
                        .line(String.join("\n", listBuildHistoryLog))
                        .buildHistory(buildHistory)
                        .build()
        );
    }
}
