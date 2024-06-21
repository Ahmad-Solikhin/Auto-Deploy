package com.gayuh.auto_deploy.service;

import com.gayuh.auto_deploy.entity.Project;
import reactor.core.publisher.Flux;

public interface BuildHistoryService {
    void addBuildHistory(Flux<String> lines, Project project, long startTime);
}
