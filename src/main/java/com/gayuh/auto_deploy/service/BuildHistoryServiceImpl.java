package com.gayuh.auto_deploy.service;

import com.gayuh.auto_deploy.entity.BuildHistory;
import com.gayuh.auto_deploy.entity.Project;
import com.gayuh.auto_deploy.repository.BuildHistoryLogRepository;
import com.gayuh.auto_deploy.repository.BuildHistoryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

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
    public void addBuildHistoryLog(Flux<String> lines, BuildHistory buildHistory) {

        List<String> block = lines.collectList().block();
        block.forEach(log::info);

    }

    @Override
    @Transactional
    public BuildHistory addBuildHistory(Project project, long startTime) {
        return buildHistoryRepository.save(BuildHistory.builder()
                .project(project)
                .executeAt(startTime)
                .build());
    }
}
