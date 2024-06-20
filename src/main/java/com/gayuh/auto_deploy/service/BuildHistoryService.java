package com.gayuh.auto_deploy.service;

import com.gayuh.auto_deploy.entity.Project;

public interface BuildHistoryService {
    void addBuildHistory(Process process, Project project);
}
