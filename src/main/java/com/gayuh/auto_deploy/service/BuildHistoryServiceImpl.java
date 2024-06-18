package com.gayuh.auto_deploy.service;

import com.gayuh.auto_deploy.entity.BuildHistory;
import com.gayuh.auto_deploy.entity.BuildHistoryLog;
import com.gayuh.auto_deploy.repository.BuildHistoryLogRepository;
import com.gayuh.auto_deploy.repository.BuildHistoryRepository;
import com.gayuh.auto_deploy.repository.ProjectRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class BuildHistoryServiceImpl implements BuildHistoryService {
    private final BuildHistoryRepository buildHistoryRepository;
    private final BuildHistoryLogRepository buildHistoryLogRepository;
    private final ProjectRepository projectRepository;

    @Async
    @Override
    @Transactional
    public void addBuildHistory(Process process, String projectId) throws IOException {
        boolean successStatus = true;

        BufferedReader successReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));

        Stream<String> stringStream = Stream.concat(successReader.lines(), errorReader.lines());

        Long executeAt = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);

        var project = projectRepository.findById(projectId).orElse(null);

        var buildHistory = BuildHistory.builder()
                .executeAt(executeAt)
                .project(project)
                .build();

        buildHistoryRepository.save(buildHistory);

        var listBuildHistoryLog = new ArrayList<BuildHistoryLog>();
        AtomicInteger lineNumber = new AtomicInteger(1);
        stringStream.forEach(data ->
                listBuildHistoryLog.add(
                        BuildHistoryLog.builder()
                                .buildHistory(buildHistory)
                                .lineNumber(lineNumber.getAndIncrement())
                                .line(data)
                                .build()
                )
        );

        buildHistory.setExecutionTime(LocalDateTime.now().toEpochSecond(ZoneOffset.UTC) - executeAt);
        buildHistory.setSuccess(successStatus);

        buildHistoryRepository.save(buildHistory);

        buildHistoryLogRepository.saveAll(listBuildHistoryLog);
    }
}
