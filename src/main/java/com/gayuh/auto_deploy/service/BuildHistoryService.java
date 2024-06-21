package com.gayuh.auto_deploy.service;

import com.gayuh.auto_deploy.entity.BuildHistory;
import com.gayuh.auto_deploy.entity.Project;
import reactor.core.publisher.Flux;

public interface BuildHistoryService {
    void addBuildHistoryLog(Flux<String> lines, BuildHistory buildHistory);

    BuildHistory addBuildHistory(Project project, long startTime);
}
