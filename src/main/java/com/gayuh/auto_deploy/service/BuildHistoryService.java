package com.gayuh.auto_deploy.service;

import com.gayuh.auto_deploy.entity.Project;

import java.io.IOException;

public interface BuildHistoryService {
    void addBuildHistory(Process process, Project project) throws InterruptedException, IOException;
}
