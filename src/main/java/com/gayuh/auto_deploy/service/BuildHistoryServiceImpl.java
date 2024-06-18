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
import java.io.InputStreamReader;
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
    public void addBuildHistory(Process process, Project project) throws IOException {
        boolean successStatus = true;
        var success = new BufferedReader(new InputStreamReader(process.getInputStream()));
        var error = new BufferedReader(new InputStreamReader(process.getErrorStream()));

        Long executeAt = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);

        var buildHistory = BuildHistory.builder()
                .executeAt(executeAt)
                .project(project)
                .build();

        buildHistoryRepository.save(buildHistory);

        var listBuildHistoryLog = new ArrayList<BuildHistoryLog>();

        String line;
        int lineNumber = 1;
        while ((line = success.readLine()) != null) {
            log.info("Success in execution : {}", line);
            listBuildHistoryLog.add(
                    BuildHistoryLog.builder()
                            .buildHistory(buildHistory)
                            .lineNumber(lineNumber)
                            .line(line)
                            .build()
            );
            lineNumber++;
        }

        if (error.readLine() != null) {
            while ((line = error.readLine()) != null) {
                log.error("Error in execution : {}", line);
            }

            successStatus = false;
        }

        buildHistory.setExecutionTime(LocalDateTime.now().toEpochSecond(ZoneOffset.UTC) - executeAt);
        buildHistory.setSuccess(successStatus);

        buildHistoryRepository.save(buildHistory);

        buildHistoryLogRepository.saveAll(listBuildHistoryLog);
    }
}
