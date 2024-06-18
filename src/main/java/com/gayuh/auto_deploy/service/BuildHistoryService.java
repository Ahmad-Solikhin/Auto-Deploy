package com.gayuh.auto_deploy.service;

import com.gayuh.auto_deploy.entity.Project;

import java.io.IOException;
import java.util.stream.Stream;

public interface BuildHistoryService {
    void addBuildHistory(Process process, String projectId) throws InterruptedException, IOException;
}
